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

    writeRequestChunk(int x, int z) {
        var req = new HttpRequest();
        req.open("POST", "http://${window.location.hostname}:23333/chunk", async: true);
        req.responseType = "arraybuffer";
        req.onReadyStateChange.listen((e) {
            if (req.readyState == 4 && req.status == 200) {
                world.loadChunk(req.response);
            }
        });
        req.send("$x:$z");
    }
}