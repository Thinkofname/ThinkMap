package mapviewer.renderer.webgl;
import mapviewer.world.Chunk;
import mapviewer.world.World;

class WebGLWorld extends World {

	public function new() {
		super();
	}
	
	public function render(renderer : WebGLRenderer) {
		//TODO
	}
	
	override public function newChunk() : Chunk {
		return new WebGLChunk();
	}
	
	override public function queueCompare(a : Dynamic, b : Dynamic) : Int {
		return 0; //TODO
	}
}