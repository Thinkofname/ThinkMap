package mapviewer.worker;
import js.html.Uint8Array;
import js.html.WorkerContext;
import mapviewer.block.BlockRegistry;
import mapviewer.logging.Logger;
import mapviewer.Main;

class WorkerMain {
	
	public static var self : Dynamic = untyped __js__("self");
	
    static function main() {
		Logger.CAN_LOG = false;
		//TODO: Models
		BlockRegistry.init();
		Main.world = new WorkerWorld();
		
		self.onmessage = function(msg : Dynamic) {
			if (msg.data == "start") {
				self.postMessage("ready");
				return;
			}
			if (msg.data.sendBack) {
				Main.world.addChunk(new WorkerChunk(new Uint8Array(msg.data.data)));
			} else {
				var chunk = new WorkerChunk(new Uint8Array(msg.data.data));
				chunk.send();
				Main.world.addChunk(chunk);
			}
		};
	}
	
	static function __init__() {
		Logger.CAN_LOG = false;
	}
}