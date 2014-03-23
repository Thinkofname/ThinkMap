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
package mapviewer.worker;
import js.html.Uint8Array;
import js.html.WorkerContext;
import mapviewer.assets.TextureLoader;
import mapviewer.block.BlockRegistry;
import mapviewer.logging.Logger;
import mapviewer.Main;
import mapviewer.model.Model;
import mapviewer.renderer.TextureInfo;
import haxe.Json;

class WorkerMain {
	
	public static var self : Dynamic = untyped __js__("self");
	
    static function main() {
		untyped importScripts("gl-matrix-min.js");
		Logger.CAN_LOG = false;
		Main.world = new WorkerWorld();
		
		self.onmessage = function(msg : Dynamic) {
			switch (msg.data.type) {
				case "start":			
					Main.blockTextureInfo = TextureLoader.textures;	
					BlockRegistry.init();
					self.postMessage("ready");
				case "load":
					var chunk = new WorkerChunk(msg.data.data);
					chunk.world = Main.world;
					if (msg.data.sendBack) {
						chunk.send();
					}
					Main.world.addChunk(chunk);
				case "remove":
					Main.world.removeChunk(msg.data.x, msg.data.z);
				case "build":
					var c = Main.world.getChunk(msg.data.x, msg.data.z);
					if (c == null) {
						return;
					}
					var chunk : WorkerChunk = cast c;
					chunk.sendBuild(msg.data.i, msg.data.bid);
			}
		};
	}
	
	static function __init__() {
		Logger.CAN_LOG = false;
	}
}