package mapviewer.network;

import js.html.DataView;
import js.html.Uint8Array;
import js.html.WebSocket;
import mapviewer.logging.Logger;

class Connection {

    private static var logger : Logger = new Logger("Connection");

    private var websocket : WebSocket;
    public var address : String;

    public function new(address : String) {
        websocket = new WebSocket('ws://$address/server');
        websocket.binaryType = "arraybuffer";
        websocket.onopen = start;
        websocket.onmessage = data;
		this.address = address;
    }

    private function start(e) {
        logger.info("Connected to server");
        websocket.send(new Uint8Array(1).buffer);
        Main.renderer.connected();
    }

    private function data(e) {
        var reader : DataView = new DataView(e.data);
        switch (reader.getUint8(0)) {
            case 0:
                readTimeUpdate(new DataView(reader.buffer, 1));
            case 1:
                readSpawnPosition(new DataView(reader.buffer, 1));
        }
    }

    private function readTimeUpdate(data : DataView) {
        Main.world.currentTime = data.getInt32(0, false);
    }

    private function readSpawnPosition(data : DataView) {
        Main.renderer.moveTo(data.getInt32(0, false), data.getUint8(4),
            data.getInt32(5, false));
    }
}