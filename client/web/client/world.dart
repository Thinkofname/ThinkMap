part of mapViewer;

class World {

    Map<int, Chunk> chunks = new Map();

    int currentTime = 6000;

    World() {
        new Timer.periodic(new Duration(milliseconds: 1000 ~/ 20), tick);
    }

    tick(Timer timer) {
        currentTime += 1;
        currentTime %= 24000;
    }

    Map<String, bool> _waitingForBuild = new Map();
    List<_BuildJob> _buildQueue = new List();
    _BuildJob currentBuild;
    BuildSnapshot currentSnapshot;
    Stopwatch stopwatch = new Stopwatch();

    requestBuild(Chunk chunk, int i) {
        String key = chunk.x.toString() + ":" + chunk.z.toString() + "@" + i.toString();
        if (_waitingForBuild.containsKey(key)) {
            return; // Already queued
        }
        _waitingForBuild[key] = true;
        _buildQueue.add(new _BuildJob(chunk, i)); //TODO
    }

    static const int BUILD_LIMIT_MS = 8;
    int lastSort = 0;

    render(RenderingContext gl) {
        stopwatch.reset();
        stopwatch.start();
        bool run = true;
        if (currentBuild != null) {
            var job = currentBuild;
            BuildSnapshot snapshot = job.chunk.buildSection(job.i, currentSnapshot, stopwatch);
            currentBuild = null;
            currentSnapshot = null;
            if (snapshot != null) {
                currentBuild = job;
                currentSnapshot = snapshot;
                run = false;
            }
        }
        if (_buildQueue.isNotEmpty && lastSort <= 0) {
            lastSort = _buildQueue.length * 2;
            window.console.time("Sort");
            _buildQueue.sort(_queueCompare);
            window.console.timeEnd("Sort");
        }
        lastSort--;
        while (run && stopwatch.elapsedMilliseconds < BUILD_LIMIT_MS && _buildQueue.isNotEmpty) {
            var job = _buildQueue.removeLast();
            String key = job.chunk.x.toString() + ":" + job.chunk.z.toString() + "@" + job.i.toString();
            _waitingForBuild.remove(key);
            BuildSnapshot snapshot = job.chunk.buildSection(job.i, null, stopwatch);
            if (snapshot != null) {
                currentBuild = job;
                currentSnapshot = snapshot;
                break;
            }
        }
        stopwatch.stop();
        chunks.forEach((k, v) {
            v.render(gl);
        });
    }

    int _queueCompare(_BuildJob a, _BuildJob b) {
        num distA = a.chunk.x * a.chunk.x + a.chunk.z * a.chunk.z + a.i * a.i;
        num distB = b.chunk.x * b.chunk.x + b.chunk.z * b.chunk.z + b.i * b.i;
        return distB - distA;
    }

    addChunk(Chunk chunk) {
        chunk.world = this;
        chunks[_chunkKey(chunk.x, chunk.z)] = chunk;
    }

    int cacheX;
    int cacheZ;
    Chunk cacheChunk;

    Block getBlock(int x, int y, int z) {
        int cx = x >> 4;
        int cz = z >> 4;
        if (cacheChunk != null && cacheX == cx && cacheZ == cz) {
            return cacheChunk.getBlock(x & 0xF, y, z & 0xF);
        }
        cacheX = cx;
        cacheZ = cz;
        var chunk = chunks[_chunkKey(cx, cz)];
        if (chunk == null) {
            return Block.AIR;
        }
        cacheChunk = chunk;
        return cacheChunk.getBlock(x & 0xF, y, z & 0xF);
    }

    int _chunkKey(int x, int z) {
        return (x & 0xFFFF) | ((z & 0xFFFF)) << 16;
    }
}

class _BuildJob {
    Chunk chunk;
    int i;
    _BuildJob(this.chunk, this.i);
}