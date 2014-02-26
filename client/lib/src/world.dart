part of map_viewer;

class LoaderIsolate {

  SendPort isolateOut;
  World world;
  bool usable = false;

  Chunk currentChunk;

  LoaderIsolate(this.world) {
    ReceivePort rec = new ReceivePort();
    List args = new List();
    args.add(connection.address);
    args.addAll(models.keys);
    Isolate.spawnUri(Uri.parse("isolate.dart"), args, rec.sendPort).catchError((e) {
      print("$e");
    });
    rec.listen((msg) {
      if (isolateOut == null) {
        isolateOut = msg;
        usable = true;
        return;
      }
      if (currentChunk == null) {
        currentChunk = world.newChunk()..fromMap(msg)
          ..world = world
          ..init();
      } else {
        currentChunk.fromMap(msg);
      }
      if (msg['done']) {
        world.chunksLoading.remove(World._chunkKey(currentChunk.x, currentChunk.z));
        world.addChunk(currentChunk);
        usable = true;
        currentChunk = null;
      }
    });
  }

  send(int x, int z) {
    usable = false;
    isolateOut.send([x, z]);
  }

  static void isolate(List args, SendPort port) {
    for (String model in args.sublist(1)) {
      models[model] = new Model();
    }
    ReceivePort rec = new ReceivePort();
    Logger.canLog = false;
    port.send(rec.sendPort);
    rec.listen((List<int> msg) {
      var req = new HttpRequest();
      req.open("POST", "http://${args[0]}/chunk", async: false);
      req.responseType = "arraybuffer";
      req.send(World._chunkKey(msg[0], msg[1]));
      if (req.readyState == 4 && req.status == 200) {
        ByteData data = new ByteData.view(req.response);
        Chunk.processData(new Uint8List.view(data.buffer), data, port);
      }
    });
  }
}

abstract class World {

  static final Logger logger = new Logger("World");

  Map<String, Chunk> chunks = new Map();
  Map<String, bool> chunksLoading = new Map();

  int currentTime = 6000;
  List<LoaderIsolate> workers = new List(5);

  List<List<int>> _needLoad = new List();
  bool needSort = false;

  World() {
    new Timer.periodic(new Duration(milliseconds: 1000 ~/ 20), tick);
    for (int i = 0; i < workers.length; i++) {
      workers[i] = new LoaderIsolate(this);
    }
  }

  void tick(Timer timer) {
    currentTime += 1;
    currentTime %= 24000;
  }

  /**
   * Request a chunk from the server
   */
  void writeRequestChunk(int x, int z) {
    _needLoad.add([x, z]);
  }

  void addChunk(Chunk chunk) {
    String key = _chunkKey(chunk.x, chunk.z);
    if (chunks[key] != null) {
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

  void removeChunk(int x, int z) {
    Chunk chunk = getChunk(x, z);
    chunks.remove(_chunkKey(x, z));
    chunk.unload(renderer);
    for (int i = 0; i < 16; i++) {
      _waitingForBuild.remove(_buildKey(x, z, i));
    }
  }

  // Build related methods

  void tickLoaders() {
    for (LoaderIsolate iso in workers) {
      if (iso.usable && _needLoad.isNotEmpty) {
        var a = _needLoad.removeLast();
        iso.send(a[0], a[1]);
      }
    }
  }

  Map<String, bool> _waitingForBuild = new Map();
  List<_BuildJob> _buildQueue = new List();
  _BuildJob currentBuild;
  Object currentSnapshot;

  void requestBuild(Chunk chunk, int i) {
    String key = _buildKey(chunk.x, chunk.z, i);
    if (_waitingForBuild.containsKey(key)) {
      // Already queued
      return;
    }
    _waitingForBuild[key] = true;
    _buildQueue.add(new _BuildJob(chunk, i));
    needSort = true;
  }

  static int BUILD_LIMIT_MS = 8000;

  void tickBuildQueue(Stopwatch stopwatch) {
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

    if (_buildQueue.isNotEmpty && needSort) {
      needSort = false;
      _buildQueue.sort(_queueCompare);
    }
    while (stopwatch.elapsedMicroseconds < BUILD_LIMIT_MS && _buildQueue.isNotEmpty) {
      var job = _buildQueue.removeLast();
      String key = _buildKey(job.chunk.x, job.chunk.z, job.i);
      if (!_waitingForBuild.containsKey(key)) continue;
      _waitingForBuild.remove(key);
      if (world.getChunk(job.chunk.x, job.chunk.z) == null) continue;
      Object snapshot = job.exec(null, stopwatch);
      if (snapshot != null) {
        currentBuild = job;
        currentSnapshot = snapshot;
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

  bool isLoaded(int x, int y, int z) {
    if (y < 0 || y > 255) return false;
    int cx = x >> 4;
    int cz = z >> 4;
    return getChunk(cx, cz) != null;
  }

  static String _chunkKey(int x, int z) {
    return "${x}:${z}";
  }

  static String _buildKey(int x, int z, int i) {
    return "${x.toSigned(32)}:${z.toSigned(32)}@$i";
  }
}
