package mapviewer.renderer.webgl;
import js.html.webgl.RenderingContext;
import mapviewer.js.Utils;
import mapviewer.world.Chunk;
import mapviewer.world.World;

class WebGLWorld extends World {

	public function new() {
		super(true);
	}
	
	private var lastRender : Int;
	
	public function render(renderer : WebGLRenderer) {		
		renderer.gl.uniform1i(renderer.disAlphaLocation, 1);
		for (chunk in chunks) {
			var w : WebGLChunk = cast chunk;
			w.render(renderer, 0);
		}
		
		renderer.gl.enable(RenderingContext.BLEND);
		renderer.gl.uniform1i(renderer.disAlphaLocation, 0);
		for (chunk in chunks) {
			var w : WebGLChunk = cast chunk;
			w.render(renderer, 1);
		}
		renderer.gl.disable(RenderingContext.BLEND);
		
		var endTime = 16 - (Utils.now() - lastRender);
		if (endTime < 5) endTime = 5;
		
		tickBuildQueue(Utils.now() + endTime);
		
		lastRender = Utils.now();		
		
		proxy.tick();
	}
	
	override public function newChunk() : Chunk {
		return new WebGLChunk();
	}
	
	override public function queueCompare(a : BuildJob, b : BuildJob) : Int {
		var web : WebGLRenderer = cast Main.renderer;
		var camera = web.camera;
		var adx = (a.chunk.x * 16) + 8 - camera.x;
		var ady = (a.i * 16) + 8 - camera.y;
		var adz = (a.chunk.z * 16) + 8 - camera.z;
		var distA = adx * adx + ady * ady + adz * adz;
		var bdx = (b.chunk.x * 16) + 8 - camera.x;
		var bdy = (b.i * 16) + 8 - camera.y;
		var bdz = (b.chunk.z * 16) + 8 - camera.z;
		var distB = bdx * bdx + bdy * bdy + bdz * bdz;
		return Std.int(distB - distA);
	}
}