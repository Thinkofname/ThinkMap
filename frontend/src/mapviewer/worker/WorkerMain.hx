package mapviewer.worker;
import js.html.Uint8Array;
import js.html.WorkerContext;
import mapviewer.block.BlockRegistry;
import mapviewer.logging.Logger;
import mapviewer.Main;
import mapviewer.model.Model;

class WorkerMain {
	
	public static var self : Dynamic = untyped __js__("self");
	
    static function main() {
		untyped importScripts("gl-matrix-min.js");
		Logger.CAN_LOG = false;
		//TODO: Models
		Main.world = new WorkerWorld();
		
		self.onmessage = function(msg : Dynamic) {
			switch (msg.data.type) {
				case "models":				
					var mJs = msg.data.data;
					for (k in Reflect.fields(mJs)) {
						var model = new Model();
						model.fromMap(Reflect.field(mJs, k));
						Model.models[k] = model;
					}
					BlockRegistry.init();
					self.postMessage("ready");
				case "load":
					if (msg.data.sendBack) {
						Main.world.addChunk(new WorkerChunk(new Uint8Array(msg.data.data)));
					} else {
						var chunk = new WorkerChunk(new Uint8Array(msg.data.data));
						chunk.send();
						Main.world.addChunk(chunk);
					}
				case "remove":
					Main.world.removeChunk(msg.data.x, msg.data.z);
			}
		};
	}
	
	static function __init__() {
		Logger.CAN_LOG = false;
	}
}