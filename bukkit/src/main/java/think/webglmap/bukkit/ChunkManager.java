package think.webglmap.bukkit;

import io.netty.buffer.ByteBuf;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;

import java.util.concurrent.*;

public class ChunkManager {

    private final WebglMapPlugin plugin;
    private final World world;

    public ChunkManager(WebglMapPlugin plugin, World world) {
        this.plugin = plugin;
        this.world = world;
    }

    private static long chunkKey(int x, int z) {
        return ((long) x << 32) | z & 0xFFFFFFFL;
    }

    public void cleanup() {
    }

    public boolean getChunkBytes(final int x, final int z, ByteBuf out) {
        ChunkSnapshot chunk = null;
        try {
            chunk = plugin.getServer().getScheduler().callSyncMethod(plugin, new Callable<ChunkSnapshot>() {
                @Override
                public ChunkSnapshot call() throws Exception {
                    return world.getChunkAt(x, z).getChunkSnapshot(false, true, false);
                }
            }).get();
        } catch (InterruptedException e) {
            Thread.interrupted();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (chunk == null) {
            return false;
        }
        int mask = 0;
        for (int i = 0; i < 16; i++) {
            if (!chunk.isSectionEmpty(i))
                mask |= 1 << i;
        }
        out.writeInt(x);
        out.writeInt(z);
        out.writeShort(mask);
        for (int i = 0; i < 16; i++) {
            if (!chunk.isSectionEmpty(i)) {
                for (int oy = 0; oy < 16; oy++) {
                    for (int oz = 0; oz < 16; oz++) {
                        for (int ox = 0; ox < 16; ox++) {
                            out.writeShort(chunk.getBlockTypeId(ox, oy + i * 16, oz));
                            out.writeByte(chunk.getBlockData(ox, oy + i * 16, oz));
                            out.writeByte(chunk.getBlockEmittedLight(ox, oy + i * 16, oz));
                            out.writeByte(chunk.getBlockSkyLight(ox, oy + i * 16, oz));
                        }
                    }
                }
            }
        }
        return true;
    }
}
