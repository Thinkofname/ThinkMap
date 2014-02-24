part of map_viewer;

class LoaderIsolate {

  SendPort isolateOut;
  World world;
  bool usable = false;

  LoaderIsolate(this.world) {
    ReceivePort rec = new ReceivePort();
    Isolate.spawnUri(Uri.parse("isolate.dart"), new List.from(models.keys), rec.sendPort).catchError((e) {
      print("$e");
    });
    rec.listen((msg) {
      if (isolateOut == null) {
        isolateOut = msg;
        usable = true;
        return;
      }
      var chunk = world.newChunk()..fromMap(msg)
        ..world = world
        ..init();
      world.chunksLoading.remove(world._chunkKey(chunk.x, chunk.z));
      world.addChunk(chunk);
      usable = true;
    });
  }

  send(dynamic data) {
    usable = false;
    isolateOut.send(data);
  }

  static void isolate(List args, SendPort port) {
    for (String model in args) {
      models[model] = new Model();
    }
    ReceivePort rec = new ReceivePort();
    Logger.canLog = false;
    port.send(rec.sendPort);
    rec.listen((Uint8List msg) {
      if (!(msg is Uint8List)) {
        msg = new Uint8List.fromList(msg);
      }
      port.send(Chunk.processData(msg, new ByteData.view(msg.buffer)));
    });
  }
}

abstract class World {

  Map<String, Chunk> chunks = new Map();
  Map<String, bool> chunksLoading = new Map();

  int currentTime = 6000;
  List<LoaderIsolate> workers = new List(5);

  List<Uint8List> _needLoad = new List();

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

  void loadChunk(ByteBuffer byteBuffer) {
    ByteData data = new ByteData.view(byteBuffer);
    var x = data.getInt32(0);
    var z = data.getInt32(4);
    String key = _chunkKey(x, z);
    if (!chunksLoading.containsKey(key) && !chunks.containsKey(key)) {
      chunksLoading[key] = true;
      _needLoad.add(new Uint8List.view(byteBuffer));
    }
  }

  void addChunk(Chunk chunk) {
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

  void removeChunk(int x, int z) {
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
  }

  static int BUILD_LIMIT_MS = 8000;
  // Lower time to allow for some rendering to occur
  static int LOAD_LIMIT_MS = 6800;
  int lastSort = 60;

  void tickBuildQueue(Stopwatch stopwatch) {
    for (LoaderIsolate iso in workers) {
      if (stopwatch.elapsedMicroseconds > World.BUILD_LIMIT_MS) {
        return;
      }
      if (iso.usable && _needLoad.isNotEmpty) {
        iso.send(_needLoad.removeLast());
      }
    }

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

    if (stopwatch.elapsedMicroseconds > World.BUILD_LIMIT_MS) {
      return;
    }

    lastSort--;
    if (_buildQueue.isNotEmpty && lastSort <= 0) {
      lastSort = 60;
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

  bool isLoaded(int x, int y, int z) {
    if (y < 0 || y > 255) return false;
    int cx = x >> 4;
    int cz = z >> 4;
    return getChunk(cx, cz) != null;
  }

  String _chunkKey(int x, int z) {
    return "${x}:${z}";
  }

  String _buildKey(int x, int z, int i) {
    return "${x.toSigned(32)}:${z.toSigned(32)}@$i";
  }
}
