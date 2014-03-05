package mapviewer.worker;
import js.html.Uint8Array;
import js.html.WorkerContext;
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
				case "textures":					
					var js = msg.data.data;
					for (e in Reflect.fields(js)) {
						var ti = Reflect.field(js, e);
						Main.blockTextureInfo[e] = new TextureInfo(ti.start, ti.end);
					}
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
					chunk.sendBuild(msg.data.i);
			}
		};
	}
	
	static function __init__() {
		Logger.CAN_LOG = false;
	}
}