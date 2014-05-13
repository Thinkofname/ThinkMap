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

package uk.co.thinkofdeath.thinkmap.bukkit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.apache.commons.io.FileUtils;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.thinkofdeath.thinkmap.bukkit.textures.BufferedTexture;
import uk.co.thinkofdeath.thinkmap.bukkit.textures.BufferedTextureFactory;
import uk.co.thinkofdeath.thinkmap.bukkit.textures.TextureDetailsSerializer;
import uk.co.thinkofdeath.thinkmap.bukkit.web.Packets;
import uk.co.thinkofdeath.thinkmap.bukkit.web.WebHandler;
import uk.co.thinkofdeath.thinkmap.bukkit.world.ChunkManager;
import uk.co.thinkofdeath.thinkmap.textures.StitchResult;
import uk.co.thinkofdeath.thinkmap.textures.TextureDetails;
import uk.co.thinkofdeath.thinkmap.textures.TextureFactory;
import uk.co.thinkofdeath.thinkmap.textures.TextureStitcher;
import uk.co.thinkofdeath.thinkmap.textures.mojang.MojangTextureProvider;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThinkMapPlugin extends JavaPlugin implements Runnable {

    public static final String MINECRAFT_VERSION = "1.7.9";
    public static final String RESOURCE_VERSION = "2";

    private final Map<String, ChunkManager> chunkManagers = new HashMap<String, ChunkManager>();
    private WebHandler webHandler;
    private World targetWorld;

    private final ExecutorService chunkExecutor = Executors.newFixedThreadPool(4);

    private File resourceDir;
    private File worldDir;

    @Override
    public void onEnable() {
        resourceDir = new File(getDataFolder(),
                "resources/" + MINECRAFT_VERSION + "-" + RESOURCE_VERSION);
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

        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        config.addDefault("webserver.port", 23333);
        config.addDefault("webserver.bind-address", "0.0.0.0");
        saveConfig();

        // Resource loading
        final File blockTextures = new File(
                resourceDir,
                "blocks.png");
        if (blockTextures.exists()) {
            webHandler = new WebHandler(this);
            webHandler.start();
        } else {
            getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    try {
                        blockTextures.getParentFile().mkdirs();
                        TextureFactory textureFactory = new BufferedTextureFactory();
                        getLogger().info("Downloading textures. This may take some time");
                        MojangTextureProvider textureProvider =
                                new MojangTextureProvider(MINECRAFT_VERSION, textureFactory);
                        try (InputStream in =
                                     getClassLoader().getResourceAsStream("textures/missing_texture.png")) {
                            textureProvider.addTexture("missing_texture",
                                    textureFactory.fromInputStream(in));
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
                        FileUtils.writeStringToFile(
                                new File(
                                        resourceDir,
                                        "blocks.json"
                                ),
                                gson.toJson(result.getDetails())
                        );
                        ImageIO.write(((BufferedTexture) result.getOutput()).getImage(), "PNG",
                                blockTextures);
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
        webHandler.interrupt();
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
        getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                sendAll(frame);
            }
        });
    }

    public void sendAll(BinaryWebSocketFrame frame) {
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
}
