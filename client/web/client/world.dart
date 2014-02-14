part of mapViewer;

abstract class World {

    Map<String, Chunk> chunks = new Map();
    Map<String, bool> chunksLoading = new Map();

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
        var job = new _LoadJob(newChunk(), byteBuffer);
        String key = _chunkKey(job.chunk.x, job.chunk.z);
        job.chunk.world = this;
        if (!chunksLoading.containsKey(key) && !chunks.containsKey(key)) {
            chunksLoading[key] = true;
            _buildQueueLow.add(job);
        }
    }

    addChunk(Chunk chunk) {
        String key = _chunkKey(chunk.x, chunk.z);
        if (chunks[key] != null) {
            print("Dropped chunk after load");
            // Chunk is already loaded ignore it
            return;
        }
        chunks[key] = chunk;
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
        for (int i = 0; i < 16; i++) {
            _waitingForBuild.remove(_buildKey(x, z, i));
        }
    }

    // Build related methods

    Map<String, bool> _waitingForBuild = new Map();
    List<_BuildJob> _buildQueue = new List();
    List<_BuildJob> _buildQueueLow = new List();
    _BuildJob currentBuild;
    Object currentSnapshot;
    _BuildJob currentBuildLow;
    Object currentSnapshotLow;

    requestBuild(Chunk chunk, int i) {
        String key = _buildKey(chunk.x, chunk.z, i);
        if (_waitingForBuild.containsKey(key)) {
            // Already queued
            return;
        }
        _waitingForBuild[key] = true;
        _buildQueue.add(new _BuildJob(chunk, i));
    }

    static int BUILD_LIMIT_MS = 8000;
    // Lower time to allow for some rendering to occur
    static int LOAD_LIMIT_MS = 68000;
    int lastSort = 0;

    tickBuildQueue(Stopwatch stopwatch) {
        lastSort--;
        if (currentBuild != null) {
            var job = currentBuild;
            Object snapshot = job.exec(currentSnapshot, stopwatch);
            currentBuild = null;
            currentSnapshot = null;
            if (snapshot != null) {
                currentBuild = job;
                currentSnapshot = snapshot;
                return;
            }
        }

        if (stopwatch.elapsedMicroseconds > World.BUILD_LIMIT_MS) {
            return;
        }

        if (currentBuildLow != null) {
            var job = currentBuildLow;
            Object snapshot = job.exec(currentSnapshotLow, stopwatch);
            currentBuildLow = null;
            currentSnapshotLow = null;
            if (snapshot != null) {
                currentBuildLow = job;
                currentSnapshotLow = snapshot;
            }
        }

        if (stopwatch.elapsedMicroseconds > World.BUILD_LIMIT_MS) {
            return;
        }

        if ((_buildQueue.isNotEmpty || _buildQueueLow.isNotEmpty) && lastSort <= 0) {
            lastSort = 60;
            _buildQueue.sort(_queueCompare);
            _buildQueueLow.sort(_queueCompare);
        }
        while (stopwatch.elapsedMicroseconds < BUILD_LIMIT_MS && (_buildQueue.isNotEmpty || _buildQueueLow.isNotEmpty)) {
            bool low = (_buildQueueLow.isNotEmpty && (stopwatch.elapsedMicroseconds < LOAD_LIMIT_MS || _buildQueue.isEmpty));
            if (!low && _buildQueue.isEmpty) break;

            if (low) {
                if (currentBuildLow != null) {
                    break;
                }
            } else {
                if (currentBuild != null) {
                    break;
                }
            }
            var job = low
                ? _buildQueueLow.removeLast() : _buildQueue.removeLast();
            if (!(job is _LoadJob)) {
                String key = _buildKey(job.chunk.x, job.chunk.z, job.i);
                if (!_waitingForBuild.containsKey(key)) continue;
                _waitingForBuild.remove(key);
                if (world.getChunk(job.chunk.x, job.chunk.z) == null) continue;
            }
            Object snapshot = job.exec(null, stopwatch);
            if (snapshot != null) {
                if (low) {
                    currentBuildLow = job;
                    currentSnapshotLow = snapshot;
                } else {
                    currentBuild = job;
                    currentSnapshot = snapshot;
                }
            }
        }
    }

    Chunk newChunk();

    int _queueCompare(_BuildJob a, _BuildJob b);

    // General methods

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
        if (y < 0 || y > 255) return Blocks.AIR;
        int cx = x >> 4;
        int cz = z >> 4;
        var chunk = getChunk(cx, cz);
        if (chunk == null) {
            return Blocks.NULL_BLOCK;
        }
        return chunk.getBlock(x & 0xF, y, z & 0xF);
    }

    int getLight(int x, int y, int z) {
        if (y < 0 || y > 255) return 0;
        int cx = x >> 4;
        int cz = z >> 4;
        var chunk = getChunk(cx, cz);
        if (chunk == null) {
            return 7;
        }
        return chunk.getLight(x & 0xF, y, z & 0xF);
    }

    int getSky(int x, int y, int z) {
        if (y < 0 || y > 255) return 15;
        int cx = x >> 4;
        int cz = z >> 4;
        var chunk = getChunk(cx, cz);
        if (chunk == null) {
            return 7;
        }
        return chunk.getSky(x & 0xF, y, z & 0xF);
    }

    String _chunkKey(int x, int z) {
        return "${x}:${z}";
    }

    String _buildKey(int x, int z, int i) {
        return "${x.toSigned(32)}:${z.toSigned(32)}@$i";
    }
}