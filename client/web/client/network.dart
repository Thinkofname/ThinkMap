part of mapViewer;

const String DEFAULT_ADDRESS = "ws://localhost:23333/server";

class Connection {

    WebSocket websocket;

    Connection([String address = DEFAULT_ADDRESS]) {
        websocket = new WebSocket(address);
        websocket.binaryType = "arraybuffer";
        websocket.onOpen.listen(start);
        websocket.onMessage.listen(data);
    }

    start(Event e) {
        print("Connected to server");
        renderer.connected();
    }

    data(MessageEvent e) {
        ByteData reader = new ByteData.view(e.data);
        switch (reader.getUint8(0)) {
            case 0:
                readTimeUpdate(new ByteData.view(reader.buffer, 1));
                break;
        }
    }

    readTimeUpdate(ByteData data) {
        world.currentTime = data.getInt32(0, Endianness.BIG_ENDIAN);
    }

    Map<String, bool> sentFor = new Map<String, bool>();

    writeRequestChunk(int x, int z) {
        String key = "$x:$z";
        if (sentFor.containsKey(key)) return;
        var req = new HttpRequest();
        req.open("POST", "http://${window.location.hostname}:23333/chunk", async: true);
        req.responseType = "arraybuffer";
        req.onReadyStateChange.listen((e) {
            if (req.readyState == 4 && req.status == 200) {
                ByteData data = new ByteData.view(req.response);
                if (renderer.shouldLoad(data.getInt32(0), data.getInt32(4)))
                    world.loadChunk(req.response);
            }
            if (req.readyState == 4) sentFor.remove(key);
        });
        sentFor[key] = true;
        req.send(key);
    }
}