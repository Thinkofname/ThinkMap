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
        for (int x = -6; x < 6; x++) {
            for (int z = -6; z < 6; z++) {
                writeRequestChunk(x, z);
            }
        }
    }

    data(MessageEvent e) {
        ByteData reader = new ByteData.view(e.data);
        switch (reader.getUint8(0)) {
            case 0:
                readTimeUpdate(new ByteData.view(reader.buffer, 1));
                break;
            case 1: // Chunk Data TODO: Switch to gzip'ed http requests
                world.loadChunk(reader.buffer);
                break;
        }
    }

    readTimeUpdate(ByteData data) {
        world.currentTime = data.getInt32(0, Endianness.BIG_ENDIAN);
    }

    writeRequestChunk(int x, int z) {
        ByteData data = new ByteData(1 + 4 + 4);
        data.setUint8(0, 0);
        data.setInt32(1, x);
        data.setInt32(5, z);
        websocket.sendTypedData(data);
    }
}