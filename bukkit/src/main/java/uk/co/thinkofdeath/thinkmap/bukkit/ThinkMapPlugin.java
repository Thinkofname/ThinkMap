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

import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.thinkofdeath.thinkmap.bukkit.web.Packets;
import uk.co.thinkofdeath.thinkmap.bukkit.web.WebHandler;
import uk.co.thinkofdeath.thinkmap.bukkit.world.ChunkManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ThinkMapPlugin extends JavaPlugin implements Runnable {

    public final Map<Integer, SocketChannel> activeConnections = Collections.synchronizedMap(new HashMap<Integer, SocketChannel>());
    private final Map<String, ChunkManager> chunkManagers = new HashMap<String, ChunkManager>();
    public AtomicInteger lastConnectionId = new AtomicInteger();
    /**
     * Returns the plugin's web handler
     *
     * @return the web handler
     */
    @Getter
    private WebHandler webHandler;
    private World targetWorld;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Events(this), this);
        getServer().getScheduler().runTaskTimer(this, this, 20l, 20 * 2l);

        for (World world : getServer().getWorlds()) {
            if (targetWorld == null) {
                targetWorld = world;
                getChunkManager(world);
                break; // Support multiple worlds
            }
        }

        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        config.addDefault("webserver.port", 23333);
        config.addDefault("webserver.bind-address", "0.0.0.0");
        saveConfig();

        webHandler = new WebHandler(this);
        webHandler.start();
    }

    @Override
    public void onDisable() {
        webHandler.interrupt();
        for (ChunkManager chunkManager : chunkManagers.values()) chunkManager.cleanup();
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        return true;
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
        synchronized (activeConnections) {
            Iterator<SocketChannel> it = activeConnections.values().iterator();
            while (it.hasNext()) {
                SocketChannel channel = it.next();
                if (!channel.isActive() || !channel.isOpen()) {
                    it.remove();
                    continue;
                }
                frame.retain();
                channel.writeAndFlush(frame);
            }
        }
        frame.release();
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
}
