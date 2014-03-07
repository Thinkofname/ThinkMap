package mapviewer.renderer.webgl;
import js.html.webgl.RenderingContext;
import mapviewer.js.Utils;
import mapviewer.world.Chunk;
import mapviewer.world.World;

class WebGLWorld extends World {
	
	private var chunkList : Array<WebGLChunk>;

	public function new() {
		super(true);
		chunkList = new Array<WebGLChunk>();
	}
	
	public function render(renderer : WebGLRenderer) {		
		chunkList.sort(chunkSort);
		
		renderer.gl.uniform1i(renderer.disAlphaLocation, 1);
		for (chunk in chunkList) {
			chunk.render(renderer, 0);
		}
		
		renderer.gl.enable(RenderingContext.BLEND);
		renderer.gl.depthMask(false);
		renderer.gl.uniform1i(renderer.disAlphaLocation, 0);
		for (chunk in chunkList) {
			chunk.render(renderer, 1);
		}
		renderer.gl.disable(RenderingContext.BLEND);
		renderer.gl.depthMask(true);
	}
	
	private static function chunkSort(a : WebGLChunk, b : WebGLChunk) : Int {
		var renderer : WebGLRenderer = cast Main.renderer;
		var ax = a.x * 16 + 8 - Std.int(renderer.camera.x);
		var az = a.z * 16 + 8 - Std.int(renderer.camera.z);
		var bx = b.x * 16 + 8 - Std.int(renderer.camera.x);
		var bz = b.z * 16 + 8 - Std.int(renderer.camera.z);
		return (bx * bx + bz * bz) - (ax * ax + az * az);
	}

    override public function addChunk(chunk : Chunk) : Bool {
        if (!super.addChunk(chunk)) {
			return false;
		}
		chunkList.push(cast chunk);
		return true;
    }

    override public function removeChunk(x : Int, z : Int) {
        var chunk = getChunk(x, z);
		chunkList.remove(cast chunk);
		super.removeChunk(x, z);
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