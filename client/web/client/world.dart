part of mapViewer;

class World {

    Map<String, Chunk> chunks = new Map();

    int currentTime = 6000;

    World() {
        new Timer.periodic(new Duration(milliseconds: 1000 ~/ 20), tick);
    }

    tick(Timer timer) {
        currentTime += 1;
        currentTime %= 24000;
    }

    List<ByteBuffer> toLoad = new List();

    loadChunk(ByteBuffer byteBuffer) {
        toLoad.add(byteBuffer);
    }

    addChunk(Chunk chunk) {
        chunks[_chunkKey(chunk.x, chunk.z)] = chunk;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Chunk c = getChunk(chunk.x + x, chunk.z + z);
                if (c != null) c.rebuild();
            }
        }
        chunk.rebuild();
    }

    removeChunk(int x, int z) {
        Chunk chunk = getChunk(x, z);
        chunks.remove(_chunkKey(x, z));
        chunk.unload(renderer);
    }

    int cacheX;
    int cacheZ;
    Chunk cacheChunk;

    Chunk getChunk(int x, int z) {
        x = x.toSigned(32);
        z = z.toSigned(32);
        if (cacheChunk != null && cacheX == x && cacheZ == z) {
            return cacheChunk;
        }
        cacheX = x;
        cacheZ = z;
        cacheChunk = chunks[_chunkKey(x, z)];
        return cacheChunk;
    }

    Block getBlock(int x, int y, int z) {
        if (y < 0 || y > 255) return Block.AIR;
        int cx = x >> 4;
        int cz = z >> 4;
        var chunk = getChunk(cx, cz);
        if (chunk == null) {
            return Block.BEDROCK;
        }
        return chunk.getBlock(x & 0xF, y, z & 0xF);
    }

    int getData(int x, int y, int z) {
        if (y < 0 || y > 255) return 0;
        int cx = x >> 4;
        int cz = z >> 4;
        var chunk = getChunk(cx, cz);
        if (chunk == null) {
            return 0;
        }
        return chunk.getData(x & 0xF, y, z & 0xF);
    }

    int getLight(int x, int y, int z) {
        if (y < 0 || y > 255) return 0;
        int cx = x >> 4;
        int cz = z >> 4;
        var chunk = getChunk(cx, cz);
        if (chunk == null) {
            return 0;
        }
        return chunk.getLight(x & 0xF, y, z & 0xF);
    }

    int getSky(int x, int y, int z) {
        if (y < 0 || y > 255) return 15;
        int cx = x >> 4;
        int cz = z >> 4;
        var chunk = getChunk(cx, cz);
        if (chunk == null) {
            return 15;
        }
        return chunk.getSky(x & 0xF, y, z & 0xF);
    }

    String _chunkKey(int x, int z) {
        return "${x}:${z}";
    }
}