part of map_viewer;

/**
 * An IsolateWorldProxy provides a bridge between a local
 * world and a world stored on an isolate
 */
class IsolateWorldProxy {

  List<_IsolateProxy> proxies;
  int _numberLoaded = 0;

  World owner;

  int _lastProcessor = 0;

  IsolateWorldProxy(this.owner) {
    proxies = new List.generate(4, (i) {
      return new _IsolateProxy(this, () => _numberLoaded++);
    });
  }

  List _toLoad = new List();

  void tick() {
    if (_toLoad.isEmpty || _numberLoaded != proxies.length) return;
    var dl = _toLoad.removeLast();
    int x = dl[0];
    int z = dl[1];

    var req = new HttpRequest();
    req.open("POST", "http://${connection.address}/chunk", async: true);
    req.responseType = "arraybuffer";
    req.send(World._chunkKey(x, z));
    req.onReadyStateChange.listen((e) {
      if (req.readyState == 4 && req.status == 200) {
        _processChunk(req.response, x, z);
      }
    });
  }

  requestChunk(int x, int z) {
    _toLoad.add([x, z]);
  }

  _processChunk(ByteBuffer buffer, int x, int z) {
    Uint8List data = new Uint8List.view(buffer);
    Map message = new Map();
    message['x'] = x;
    message['z'] = z;
    message['data'] = CryptoUtils.bytesToBase64(data);
    message['sendBack'] = false;
    for (_IsolateProxy proxy in proxies) {
      if (proxy.id == _lastProcessor) message['sendBack'] = true;
      proxy.sendPort.send(message);
      if (proxy.id == _lastProcessor) message['sendBack'] = false;
    }
    _lastProcessor = (_lastProcessor + 1) % proxies.length;
  }

  /**
   * Isolate entry point
   */
  static void isolateMain(List args, SendPort port) {
    for (String model in args) {
      models[model] = new Model();
    }
    Logger.canLog = false;
    BlockRegistry.init();
    ReceivePort receivePort = new ReceivePort();
    port.send(receivePort.sendPort);

    world = new IsolateWorld();

    receivePort.listen((message) {
      int x = message['x'];
      int z = message['z'];
      bool sendBack = message['sendBack'];
      Uint8List data = new Uint8List.fromList(CryptoUtils.base64StringToBytes(message['data']));
      if (!sendBack) {
        world.addChunk(new IsolateChunk(data));
      } else {
        world.addChunk(new IsolateChunk.stream(data, port));
      }
    });
  }
}

class _IsolateProxy {

  static int _lastId = 0;
  static final Logger logger = new Logger("IsolateProxy");

  int id;
  IsolateWorldProxy owner;
  SendPort sendPort;

  _IsolateProxy(this.owner, void onLoad()) {
    id = _lastId++;

    ReceivePort receivePort = new ReceivePort();
    Isolate.spawnUri(Uri.parse("isolate.dart"), new List.from(models.keys), receivePort.sendPort);
    var broadcast = receivePort.asBroadcastStream();
    broadcast.first.then((SendPort port) {
      sendPort = port;
      logger.info("$this loaded");
      onLoad();
      broadcast.listen(_onData);
    });
  }

  bool get loaded => sendPort != null;

  void _onData(message) {
    world.addChunk(world.newChunk()..world = owner.owner ..fromMap(message)..init());
  }

  @override
  String toString() {
    return "IsolateProxy[$id]";
  }
}

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