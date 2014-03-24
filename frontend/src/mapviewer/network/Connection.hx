/*
 * Copyright 2014 Matthew Collins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mapviewer.network;

import haxe.io.Bytes;
import js.html.DataView;
import js.html.Uint8Array;
import js.html.WebSocket;
import mapviewer.chat.TextComponent;
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
			case 2:
                readChat(new Uint8Array(reader.buffer, 1));
				
        }
    }
	
	private function readChat(data : Uint8Array) {
		// TODO: Convert from the old format
		Main.renderer.ui.appendLine(new TextComponent(Bytes.ofData(cast data).toString()));
	}

    private function readTimeUpdate(data : DataView) {
        Main.world.currentTime = data.getInt32(0, false);
    }

    private function readSpawnPosition(data : DataView) {
        Main.renderer.moveTo(data.getInt32(0, false), data.getUint8(4),
            data.getInt32(5, false));
    }
}