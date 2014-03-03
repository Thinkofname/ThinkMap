package mapviewer.world;

import mapviewer.block.Block;
import mapviewer.block.Blocks;
import mapviewer.js.Utils;
import haxe.Timer;
import mapviewer.logging.Logger;
import mapviewer.renderer.webgl.WebGLChunk.WebGLSnapshot;
import mapviewer.worker.WorkerWorldProxy;
import mapviewer.world.World.BuildJob;
class World {

    private static var logger : Logger = new Logger("World");

    public var chunks : Map<String, Chunk>;

    public var currentTime : Int = 6000;
    private var proxy : WorkerWorldProxy;
    public var needSort : Bool = false;

    public function new(?haveProxy : Bool = true) {
        chunks = new Map<String, Chunk>();
        waitingForBuild = new Map<String, Bool>();
        buildQueue = new Array<BuildJob>();
        if (haveProxy) proxy = new WorkerWorldProxy(this);
        new Timer(Std.int(1000/20)).run = tick;
    }

    private function tick() {
        currentTime = (currentTime + 1) % 24000;
    }

    public function writeRequestChunk(x : Int, z : Int) {
       proxy.requestChunk(x, z);
    }

    public function addChunk(chunk : Chunk) {
        var key = chunkKey(chunk.x, chunk.z);
        if (chunks[key] != null) {
            // Chunk is already loaded ignore it
            return;
        }
        chunks[key] = chunk;
        for (x in -1 ... 2) {
            for (z in -1 ... 2) {
                var c = getChunk(chunk.x + x, chunk.z + z);
                if (c != null) c.rebuild();
            }
        }
        chunk.rebuild();
    }

    public function removeChunk(x : Int, z : Int) {
        var chunk = getChunk(x, z);
        chunks.remove(chunkKey(x, z));
        chunk.unload(Main.renderer);
        for (i in 0 ... 16) {
            waitingForBuild.remove(buildKey(x, z, i));
        }
    }

    // Build related methods

    private var waitingForBuild : Map<String, Bool>;
    private var buildQueue : Array<BuildJob>;
    private var currentBuild : BuildJob;
    private var currentSnapshot : Dynamic;

    public function requestBuild(chunk : Dynamic, i : Int) {
        var key = buildKey(chunk.x, chunk.z, i);
        if (waitingForBuild.exists(key)) {
            // Already queued
            return;
        }
        waitingForBuild[key] = true;
        buildQueue.push(new BuildJob(chunk, i));
        needSort = true;
    }

    inline static var BUILD_LIMIT_MS : Int = 8;

    public function tickBuildQueue(endTime : Int) {
        if (currentBuild != null) {
            var job = currentBuild;
            if (getChunk(job.chunk.x, job.chunk.z) == null) {
                currentBuild = null;
                currentSnapshot = null;
            } else {
                var snapshot = job.exec(currentSnapshot, endTime);
                currentBuild = null;
                currentSnapshot = null;
                if (snapshot != null) {
                    currentBuild = job;
                    currentSnapshot =  snapshot;
                    return;
                }
            }
        }

        if (Utils.now() >= endTime) {
            return;
        }

        if (buildQueue.length != 0 && needSort) {
            needSort = false;
            buildQueue.sort(queueCompare);
        }
        while (Utils.now() < endTime && buildQueue.length != 0) {
            var job = buildQueue.pop();
            var key = buildKey(job.chunk.x, job.chunk.z, job.i);
            if (!waitingForBuild.exists(key)) {
                continue;
            }
            waitingForBuild.remove(key);
            if (getChunk(job.chunk.x, job.chunk.z) == null) {
                continue;
            }
            var snapshot = job.exec(null, endTime);
            if (snapshot != null) {
                currentBuild = job;
                currentSnapshot = snapshot;
                return;
            }
        }
    }

    public function newChunk() : Chunk { throw "NYI"; return null; }
    public function queueCompare(a : BuildJob, b : BuildJob) : Int { throw "NYI"; return 0; }

    // General methods

    private var cacheX : Int;
    private var cacheZ : Int;
    private var cacheChunk : Chunk;

    public function getChunk(x : Int, z : Int) : Chunk {
        if (cacheChunk != null && cacheX == x && cacheZ == z) {
            return cacheChunk;
        }
        cacheX = x;
        cacheZ = z;
        cacheChunk = chunks[chunkKey(x, z)];
        return cacheChunk;
    }

    public function getBlock(x : Int, y : Int, z : Int) : Block {
        if (y < 0 || y > 255) return Blocks.AIR;
        var chunk = getChunk(x >> 4, z >> 4);
        if (chunk == null) return Blocks.NULL_BLOCK;
        return chunk.getBlock(x & 0xF, y, z & 0xF);
    }

    public function getLight(x : Int, y : Int, z : Int) : Int {
        if (y < 0 || y > 255) return 0;
        var chunk = getChunk(x >> 4, z >> 4);
        if (chunk == null) return 0;
        return chunk.getLight(x & 0xF, y, z & 0xF);
    }

    public function getSky(x : Int, y : Int, z : Int) : Int {
        if (y < 0 || y > 255) return 15;
        var chunk = getChunk(x >> 4, z >> 4);
        if (chunk == null) return 15;
        return chunk.getSky(x & 0xF, y, z & 0xF);
    }

    public function isLoaded(x : Int, y : Int, z : Int) : Bool {
        if (y < 0 || y > 255) return false;
        return getChunk(x >> 4, z >> 4) != null;
    }

    public static function chunkKey(x : Int, z : Int) : String {
        return '$x:$z';
    }

    public static function buildKey(x : Int, z : Int, i : Int) : String {
        return '$x:$z@$i';
    }
}

class BuildJob {
	
	public var chunk : Chunk;
	public var i : Int;
	
	public function new(chunk : Chunk, i : Int)  {
		this.chunk = chunk;
		this.i = i;
	}
	
	public function exec(snapshot : Dynamic, endTime : Int) : Dynamic {
		return chunk.buildSection(i, snapshot, endTime);
	}
	
	public function drop(sn : Dynamic) {
		var snapshot : WebGLSnapshot = cast sn;
		snapshot.builder.free();
		snapshot.builderTrans.free();
	}
}