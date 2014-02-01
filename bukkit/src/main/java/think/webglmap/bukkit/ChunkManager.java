package think.webglmap.bukkit;

import gnu.trove.map.hash.TLongObjectHashMap;
import io.netty.buffer.ByteBuf;
import org.bukkit.ChunkSnapshot;
import think.webglmap.bukkit.world.ActiveChunk;

import java.util.concurrent.*;

public class ChunkManager {

    private final WebglMapPlugin plugin;
    private final String world;
    private final TLongObjectHashMap<ActiveChunk> activeChunks = new TLongObjectHashMap<ActiveChunk>();
    private final BlockingQueue<ChunkSnapshot> toProcess = new LinkedBlockingQueue<ChunkSnapshot>();
    private final ConcurrentMap<Long, ChunkSnapshot> beingProcessed = new ConcurrentHashMap<Long, ChunkSnapshot>();
    private final ConverterThread converterThread;
    private final IOThread ioThread;

    public ChunkManager(WebglMapPlugin plugin, String world) {
        this.plugin = plugin;
        this.world = world;
        converterThread = new ConverterThread();
        converterThread.start();
        ioThread = new IOThread();
        ioThread.start();
    }

    public void addChunk(ChunkSnapshot chunkSnapshot) {
        toProcess.add(chunkSnapshot);
        beingProcessed.put(chunkKey(chunkSnapshot.getX(), chunkSnapshot.getZ()), chunkSnapshot);
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

    public boolean getChunkBytes(int x, int z, ByteBuf out) {
        synchronized (activeChunks) {
            System.out.println("Converting " + x + "," +  z);
            ActiveChunk chunk = activeChunks.get(chunkKey(x, z));
            if (chunk == null) {
                System.out.println("failed");
                return false;
            }
            short[][] blocks = chunk.getBlocks();
            int mask = 0;
            for (int i = 0; i < 16; i++) {
                if (blocks[i] != null)
                    mask |= 1 << i;
            }
            out.writeInt(x);
            out.writeInt(z);
            out.writeShort(mask);
            for (int i = 0; i < 16; i++) {
                if (blocks[i] != null) {
//                    for (short b : blocks[i]) {
//                        out.writeShort(b);
//                    }
                    for (int oy = 0; oy < 16; oy++) {
                        for (int oz = 0; oz < 16; oz++) {
                            for (int ox = 0; ox < 16; ox++) {
                                out.writeShort(chunk.getBlock(ox, oy + i*16, oz));
                            }
                        }
                    }
                }
            }
        }
        return true;
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
                    System.out.println("Processing " + chunkSnapshot.getX() + "," + chunkSnapshot.getZ());
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
                            System.out.println("Done");
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
