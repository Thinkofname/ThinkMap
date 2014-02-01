package think.webglmap.bukkit;

import gnu.trove.map.hash.TIntObjectHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import think.webglmap.bukkit.web.WebHandler;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WebglMapPlugin extends JavaPlugin implements Runnable {

    /**
     * Returns the plugin's web handler
     *
     * @returns the web handler
     */
    @Getter
    private WebHandler webHandler;

    private Map<String, ChunkManager> chunkManagers = new HashMap<String, ChunkManager>();

    public final Map<Integer, SocketChannel> activeConnections = Collections.synchronizedMap(new HashMap<Integer, SocketChannel>());
    public AtomicInteger lastConnectionId = new AtomicInteger();

    @Override
    public void onEnable() {
        webHandler = new WebHandler(this);
        webHandler.start();

        getServer().getPluginManager().registerEvents(new Events(this), this);
        getServer().getScheduler().runTaskTimer(this, this, 0l, 20 * 5l);
    }

    @Override
    public void onDisable() {
        webHandler.interrupt();
        for (ChunkManager chunkManager : chunkManagers.values()) chunkManager.cleanup();
    }

    public ChunkManager getChunkManager(World world) {
        if (chunkManagers.containsKey(world.getName())) {
            return chunkManagers.get(world.getName());
        }
        ChunkManager chunkManager = new ChunkManager(this, world.getName());
        chunkManagers.put(world.getName(), chunkManager);
        return chunkManager;
    }

    @Override
    public void run() {
        ByteBuf timeUpdate = Unpooled.buffer(5);
        timeUpdate.writeByte(0);
        timeUpdate.writeInt((int) getServer().getWorlds().get(0).getTime()); //FIXME: Temp
        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(timeUpdate);
        synchronized (activeConnections) {
            Iterator<SocketChannel> it = activeConnections.values().iterator();
            while (it.hasNext()) {
                SocketChannel channel = it.next();
                if (!channel.isActive() || !channel.isOpen()) {
                    it.remove();
                    continue;
                }
                channel.writeAndFlush(frame);
            }
        }
    }
}
