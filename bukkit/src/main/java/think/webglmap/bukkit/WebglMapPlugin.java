package think.webglmap.bukkit;

import gnu.trove.map.hash.TIntObjectHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.Getter;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import think.webglmap.bukkit.web.WebHandler;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WebglMapPlugin extends JavaPlugin implements Runnable {

    /**
     * Returns the plugin's web handler
     *
     * @return the web handler
     */
    @Getter
    private WebHandler webHandler;

    private final Map<String, ChunkManager> chunkManagers = new HashMap<String, ChunkManager>();

    public final Map<Integer, SocketChannel> activeConnections = Collections.synchronizedMap(new HashMap<Integer, SocketChannel>());
    public AtomicInteger lastConnectionId = new AtomicInteger();

    public World targetWorld;

    @Override
    public void onEnable() {
        webHandler = new WebHandler(this);
        webHandler.start();

        getServer().getPluginManager().registerEvents(new Events(this), this);
        getServer().getScheduler().runTaskTimer(this, this, 0l, 20 * 5l);

        for (World world : getServer().getWorlds()) {
            if (targetWorld == null) targetWorld = world;
                getChunkManager(world);
        }
    }

    @Override
    public void onDisable() {
        webHandler.interrupt();
        for (ChunkManager chunkManager : chunkManagers.values()) chunkManager.cleanup();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(Thread.currentThread().toString());
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
        ByteBuf timeUpdate = Unpooled.buffer(5);
        timeUpdate.writeByte(0);
        timeUpdate.writeInt((int) targetWorld.getTime());
        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(timeUpdate);
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
    }
}
