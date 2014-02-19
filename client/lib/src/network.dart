part of map_viewer;

/**
 * A connection to a backend server that is running the Bukkit
 * plugin (or something else that supports the protocol)
 */
class Connection {

  /// The websocket that is
  WebSocket _websocket;
  /// Address of the server that this is connected to
  final String address;

  /**
   * Creates a new connection targeted at the [address]
   *
   * The [address] does not include the 'ws://' prefix or
   * the '/server' suffix.
   */
  Connection(this.address) {
    _websocket = new WebSocket("ws://$address/server");
    _websocket.binaryType = "arraybuffer";
    _websocket.onOpen.listen(_start);
    _websocket.onMessage.listen(_data);
  }

  /// Called once the connection is open
  _start(Event e) {
    print("Connected to server");
    _websocket.sendByteBuffer(new Uint8List(1).buffer);
    renderer.connected();
  }

  /// Called when a message is received from the server
  _data(MessageEvent e) {
    ByteData reader = new ByteData.view(e.data);
    switch (reader.getUint8(0)) {
      case 0:
        _readTimeUpdate(new ByteData.view(reader.buffer, 1));
        break;
      case 1:
        _readSpawnPosition(new ByteData.view(reader.buffer, 1));
        break;
    }
  }

  /// Read and act on a time update packet
  _readTimeUpdate(ByteData data) {
    world.currentTime = data.getInt32(0, Endianness.BIG_ENDIAN);
  }

  /// Read and act on a spawn position packet
  _readSpawnPosition(ByteData data) {
    renderer.moveTo(data.getInt32(0, Endianness.BIG_ENDIAN), data.getUint8(4),
        data.getInt32(5, Endianness.BIG_ENDIAN));
  }

  /// Used to prevent double chunk requests
  Map<String, bool> _sentFor = new Map<String, bool>();

  /**
   * Request a chunk from the server
   */
  writeRequestChunk(int x, int z) {
    String key = "$x:$z";
    if (_sentFor.containsKey(key)) return;
    var req = new HttpRequest();
    req.open("POST", "http://$address/chunk", async:
        true);
    req.responseType = "arraybuffer";
    req.onReadyStateChange.listen((e) {
      if (req.readyState == 4 && req.status == 200) {
        ByteData data = new ByteData.view(req.response);
        if (renderer.shouldLoad(data.getInt32(0), data.getInt32(4)))
            world.loadChunk(req.response);
      }
      if (req.readyState == 4) _sentFor.remove(key);
    });
    _sentFor[key] = true;
    req.send(key);
  }
}
