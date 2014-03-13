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
import js.html.Worker;
import mapviewer.logging.Logger;
import mapviewer.Main;
import mapviewer.renderer.webgl.WebGLChunk;
import mapviewer.world.World;

class WorkerProxy {

	private static var lastId : Int = 0;
	private static var logger : Logger = new Logger("WorkerProxy");
	
	public var id : Int;
	public var owner : WorkerWorldProxy;
	public var worker : Worker;
	public var loaded : Bool = false;
	
	public function new(owner : WorkerWorldProxy, onLoad : Void -> Void) {
		this.owner = owner;		
		id = lastId++;
		worker = new Worker("work.js");
		worker.onmessage = function(msg) {
			if (!loaded && msg.data == "ready") {
				logger.info('$this loaded');
				loaded = true;
				onLoad();
				return;
			}
			onData(msg.data);
		}
		worker.postMessage( {
			type: "start"
		});
	}
	
	private function onData(message : Dynamic) {
		switch(message.type) {
			case "chunk":
				var chunk = Main.world.newChunk();
				chunk.world = owner.owner;
				chunk.fromMap(message);
				Main.world.addChunk(chunk);
				chunk.rebuild();
			case "build":
				var c = Main.world.getChunk(message.x, message.z);
				if (c == null) return;
				var chunk : WebGLChunk = cast c;
				chunk.createBuffer(message.i, message.data, message.dataTrans);
				Main.world.waitingForBuild.remove(World.buildKey(chunk.x, chunk.z, message.i));
		}
	}
	
	public function toString() : String {
		return 'WorkerProxy[$id]';
	}	
}