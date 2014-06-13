/*
 * Copyright 2014 Matthew Collins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.thinkofdeath.thinkcraft.bukkit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.apache.commons.io.FileUtils;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.thinkofdeath.thinkcraft.bukkit.textures.BufferedTexture;
import uk.co.thinkofdeath.thinkcraft.bukkit.textures.BufferedTextureFactory;
import uk.co.thinkofdeath.thinkcraft.bukkit.textures.TextureDetailsSerializer;
import uk.co.thinkofdeath.thinkcraft.bukkit.web.Packets;
import uk.co.thinkofdeath.thinkcraft.bukkit.web.WebHandler;
import uk.co.thinkofdeath.thinkcraft.bukkit.world.ChunkManager;
import uk.co.thinkofdeath.thinkcraft.textures.*;
import uk.co.thinkofdeath.thinkcraft.textures.mojang.MojangTextureProvider;
import uk.co.thinkofdeath.thinkcraft.textures.mojang.ZipTextureProvider;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ThinkMapPlugin extends JavaPlugin implements Runnable {

    public static final String MINECRAFT_VERSION = "1.7.9";
    public static final int RESOURCE_VERSION = 1;
    public static final int WORLD_VERSION = 1;

    private final Map<String, ChunkManager> chunkManagers = new HashMap<String, ChunkManager>();
    private WebHandler webHandler;
    private World targetWorld;

    private final ExecutorService chunkExecutor = Executors.newFixedThreadPool(4);

    private File resourceDir;
    private File worldDir;
    private Date startUpDate = new Date((System.currentTimeMillis() / 1000) * 1000);

    @Override
    public void onEnable() {
        worldDir = new File(getDataFolder(), "worlds");
        getServer().getPluginManager().registerEvents(new Events(this), this);
        getServer().getScheduler().runTaskTimer(this, this, 20l, 20 * 2l);

        for (World world : getServer().getWorlds()) {
            if (targetWorld == null) {
                targetWorld = world;
                getChunkManager(world);
                break; // TODO: Support multiple worlds
            }
        }

        final FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        config.addDefault("webserver.port", 23333);
        config.addDefault("webserver.bind-address", "0.0.0.0");
        config.addDefault("resources.pack-name", "");

        if (config.getInt("no-touchy.resource-version", 0) != RESOURCE_VERSION) {
            getLogger().info("Deleting ThinkMap-Resources due to a format update");
            config.set("no-touchy.resource-version", RESOURCE_VERSION);
            try {
                FileUtils.deleteDirectory(new File(getDataFolder(), "resources"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (config.getInt("no-touchy.world-version", 0) != WORLD_VERSION) {
            getLogger().info("Deleting ThinkMap-Worlds due to a format update");
            config.set("no-touchy.world-version", WORLD_VERSION);
            try {
                FileUtils.deleteDirectory(worldDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Client settings

        config.addDefault("client.hide-ores", false);

        saveConfig();


        resourceDir = new File(getDataFolder(),
                "resources/"
                        + (config.getString("resources.pack-name").length() == 0 ?
                        "default" : config.getString("resources.pack-name"))
        );

        // Resource loading
        final File blockInfo = new File(
                resourceDir,
                "blocks.json");
        if (blockInfo.exists()) {
            webHandler = new WebHandler(this);
            webHandler.start();
        } else {
            String resourcePack = config.getString("resources.pack-name");
            final File resourceFile = new File(getDataFolder(), resourcePack + ".zip");
            if (!resourceFile.exists()) {
                getLogger().log(Level.SEVERE, "Unable to find the resource pack "
                        + config.getString("resources.pack-name"));
                resourcePack = "";
            }
            final String finalResourcePack = resourcePack;
            getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    try {
                        blockInfo.getParentFile().mkdirs();
                        TextureFactory textureFactory = new BufferedTextureFactory();
                        getLogger().info("Downloading textures. This may take some time");
                        TextureProvider textureProvider =
                                new MojangTextureProvider(MINECRAFT_VERSION, textureFactory);

                        try (InputStream in =
                                     getClassLoader().getResourceAsStream("textures/missing_texture.png")) {
                            ((MojangTextureProvider) textureProvider).addTexture("missing_texture",
                                    textureFactory.fromInputStream(in));
                        }

                        if (finalResourcePack.length() > 0) {
                            textureProvider = new JoinedProvider(
                                    new ZipTextureProvider(
                                            new FileInputStream(resourceFile), textureFactory
                                    ),
                                    textureProvider
                            );
                        }

                        TextureStitcher stitcher = new TextureStitcher(textureProvider, textureFactory);
                        getLogger().info("Stitching textures. The mapviewer will start after this " +
                                "completes");
                        long start = System.currentTimeMillis();
                        StitchResult result = stitcher.stitch();
                        // Save the result
                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(TextureDetails.class,
                                        new TextureDetailsSerializer())
                                .create();
                        HashMap<String, Object> info = new HashMap<String, Object>();
                        info.put("textures", result.getDetails());
                        info.put("textureImages", result.getOutput().length);
                        FileUtils.writeStringToFile(
                                blockInfo,
                                gson.toJson(info)
                        );
                        int i = 0;
                        for (Texture texture : result.getOutput()) {
                            ImageIO.write(((BufferedTexture) texture).getImage(), "PNG",
                                    new File(resourceDir, "blocks_" + (i++) + ".png"));
                        }
                        getLogger().info("Stitching complete in " + (System.currentTimeMillis() -
                                start) + "ms");

                        webHandler = new WebHandler(ThinkMapPlugin.this);
                        webHandler.start();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    @Override
    public void onDisable() {
        if (webHandler != null) {
            webHandler.interrupt();
        }
        chunkExecutor.shutdown();
        try {
            chunkExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        chunkExecutor.shutdownNow();
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equals("forcegen")) {
            sender.sendMessage("Generating world data - Please wait");
            for (World world : getServer().getWorlds()) {
                sender.sendMessage("Generating world: " + world.getName());
                File worldFolder = new File(world.getWorldFolder(), "region");
                if (!worldFolder.exists()) {
                    // handle nether/end
                    worldFolder = new File(world.getWorldFolder(), String.format("DIM%d/region", world.getEnvironment().getId()));
                    if (!worldFolder.exists()) {
                        sender.sendMessage("Failed to generate: " + world.getName());
                        continue;
                    }
                }
                String[] regions = worldFolder.list();
                int i = 0;
                for (String region : regions) {
                    if (!region.endsWith(".mca")) {
                        continue;
                    }
                    String[] parts = region.split("\\.");
                    int rx = Integer.parseInt(parts[1]);
                    int rz = Integer.parseInt(parts[2]);
                    for (int x = 0; x < 32; x++) {
                        for (int z = 0; z < 32; z++) {
                            int cx = (rx << 5) + x;
                            int cz = (rz << 5) + z;
                            boolean unload = !world.isChunkLoaded(cx, cz);
                            if (world.loadChunk(cx, cz, false)) {
                                Chunk chunk = world.getChunkAt(cx, cz);
                                if (unload) {
                                    world.unloadChunkRequest(cx, cz);
                                }
                            }
                        }
                    }
                    i++;
                    sender.sendMessage(String.format("Progress: %d/%d", i, regions.length));
                }

            }
            sender.sendMessage("Complete");
        }
        return true;
    }

    public ExecutorService getChunkExecutor() {
        return chunkExecutor;
    }

    public File getWorldDir() {
        return worldDir;
    }

    public ChunkManager getChunkManager(World world) {
        synchronized (chunkManagers) {
            if (chunkManagers.containsKey(world.getName())) {
                return chunkManagers.get(world.getName());
            }
            ChunkManager chunkManager = new ChunkManager(this, world);
            chunkManagers.put(world.getName(), chunkManager);
            return chunkManager;
        }
    }

    @Override
    public void run() {
        if (targetWorld == null) return;
        final BinaryWebSocketFrame frame = new BinaryWebSocketFrame(
                Packets.writeTimeUpdate((int) targetWorld.getTime())
        );
        sendAll(frame);
    }

    public void sendAll(BinaryWebSocketFrame frame) {
        if (getWebHandler() == null) {
            return;
        }
        getWebHandler().getChannelGroup().writeAndFlush(frame);
    }


    public World getTargetWorld() {
        if (targetWorld == null) {
            targetWorld = getServer().getWorlds().get(0);
        }
        return targetWorld;
    }

    public void setTargetWorld(World targetWorld) {
        this.targetWorld = targetWorld;
    }

    public WebHandler getWebHandler() {
        return this.webHandler;
    }

    public File getResourceDir() {
        return resourceDir;
    }

    public Date getStartUpDate() {
        return startUpDate;
    }
}
