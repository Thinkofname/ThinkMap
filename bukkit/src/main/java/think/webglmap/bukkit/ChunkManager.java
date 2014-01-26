package think.webglmap.bukkit;

import gnu.trove.map.hash.TLongObjectHashMap;
import org.bukkit.ChunkSnapshot;
import think.webglmap.bukkit.world.ActiveChunk;

import java.util.concurrent.*;

//TODO: Per a world
public class ChunkManager {

    private final WebglMapPlugin plugin;
    private final TLongObjectHashMap<ActiveChunk> activeChunks = new TLongObjectHashMap<ActiveChunk>();
    private final BlockingQueue<ChunkSnapshot> toProcess = new LinkedBlockingQueue<ChunkSnapshot>();
    private final ConcurrentMap<Long, ChunkSnapshot> beingProcessed = new ConcurrentHashMap<Long, ChunkSnapshot>();
    private final ConverterThread converterThread;
    private final IOThread ioThread;

    public ChunkManager(WebglMapPlugin plugin) {
        this.plugin = plugin;
        converterThread = new ConverterThread();
        converterThread.start();
        ioThread = new IOThread();
        ioThread.start();
    }

    public void addChunk(ChunkSnapshot chunkSnapshot) {
        toProcess.add(chunkSnapshot);
    }

    private static long chunkKey(int x, int z) {
        return ((long) x << 32) | z & 0xFFFFFFFL;
    }

    public void cleanup() {
        converterThread.interrupt();
        try {
            ioThread.stopping = true;
            ioThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void removeChunk(int x, int z) {
        boolean hadMapped;
        long key = chunkKey(x, z);
        synchronized (activeChunks) {
            hadMapped = activeChunks.remove(key) != null;
        }
        if (!hadMapped && beingProcessed.containsKey(key)) {
            beingProcessed.remove(key);
        }
    }

    private class ConverterThread extends Thread {
        @SuppressWarnings("deprecation")
        @Override
        public void run() {
            try {
                while (true) {
                    ChunkSnapshot chunkSnapshot = toProcess.take();
                    long key = chunkKey(chunkSnapshot.getX(), chunkSnapshot.getZ());
                    if (!beingProcessed.containsKey(key)) continue;
                    ActiveChunk activeChunk = new ActiveChunk(chunkSnapshot.getX(), chunkSnapshot.getZ());
                    for (int y = 0; y < 256; y++) {
                        if (chunkSnapshot.isSectionEmpty(y >> 4)) continue;
                        for (int x = 0; x < 16; x++) {
                            for (int z = 0; z < 16; z++) {
                                activeChunk.setBlock(x, y, z, (short) chunkSnapshot.getBlockTypeId(x, y, z));
                                activeChunk.setData(x, y, z, (byte) chunkSnapshot.getBlockData(x, y, z));
                                activeChunk.setBlockLight(x, y, z, (byte) chunkSnapshot.getBlockEmittedLight(x, y, z));
                                activeChunk.setSkyLight(x, y, z, (byte) chunkSnapshot.getBlockSkyLight(x, y, z));
                            }
                        }
                    }
                    if (beingProcessed.containsKey(key)) { //Not cancelled
                        synchronized (activeChunks) {
                            activeChunks.put(key, activeChunk);
                        }
                        beingProcessed.remove(key);
                    }
                }
            } catch (InterruptedException e) {
                interrupt();
            }
        }
    }

    private class IOThread extends Thread {
        public volatile boolean stopping = false;

        private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<Runnable>();

        public void saveChunk(ActiveChunk chunk) {
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
                        task = tasks.poll(5, TimeUnit.SECONDS);
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
