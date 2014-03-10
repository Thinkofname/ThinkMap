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
			type: "models",
			data: Main.modelData
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