package think.webglmap.bukkit;

import io.netty.buffer.ByteBuf;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;

import java.util.concurrent.*;

public class ChunkManager {

    private final WebglMapPlugin plugin;
    private final World world;
    private final IOThread ioThread;

    public ChunkManager(WebglMapPlugin plugin, World world) {
        this.plugin = plugin;
        this.world = world;
        ioThread = new IOThread();
        ioThread.start();
    }

    private static long chunkKey(int x, int z) {
        return ((long) x << 32) | z & 0xFFFFFFFL;
    }

    public void cleanup() {
        try {
            ioThread.stopping = true;
            ioThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
                        }
                    }
                }
            }
        }
        return true;
    }

    private class IOThread extends Thread {
        public volatile boolean stopping = false;

        private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<Runnable>();

        public void saveChunk(ChunkSnapshot chunk) {
            tasks.add(new Runnable() {
                @Override
                public void run() {
                    //TODO: Saving
                }
            });
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Runnable task;
                    if (stopping) {
                        task = tasks.poll();
                        if (task == null) {
                            return;
                        }
                    } else {
                        task = tasks.poll(2, TimeUnit.SECONDS);
                        if (task == null) {
                            if (stopping) {
                                return;
                            } else {
                                continue;
                            }
                        }

                    }
                    task.run();
                }
            } catch (InterruptedException e) {
                interrupt();
            }
        }
    }
}
