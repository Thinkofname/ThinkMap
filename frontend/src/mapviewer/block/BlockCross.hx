package mapviewer.block;
import mapviewer.renderer.webgl.BlockBuilder;
import mapviewer.world.Chunk;
import mapviewer.block.Block.Face;
import mapviewer.model.Model;
import mapviewer.model.Model.ModelFace;

using Lambda;

class BlockCross extends Block {

	public function new() {	
		super();
	}
	
	override public function render(builder : BlockBuilder, x : Int, y : Int, z : Int, chunk : Chunk) {		
		if (model == null) {
			var r = 255;
			var g = 255;
			var b = 255;
			if (forceColour) {
				r = (colour >> 16) & 0xFF;
				g = (colour >> 8) & 0xFF;
				b = colour & 0xFF;
			}
			model = new Model();
			var face = ModelFace.fromFace(Face.FRONT).chainModelFace()
				.texture(texture)
				.r(r)
				.g(g)
				.b(b).ret();
			face.vertices.foreach(function(e) {
				if (e.x == 1) e.z = 1;
				return true;
			});
			model.faces.push(face);
			
			face = ModelFace.fromFace(Face.BACK).chainModelFace()
				.texture(texture)
				.r(r)
				.g(g)
				.b(b).ret();
			face.vertices.foreach(function(e) {
				if (e.x == 1) e.z = 1;
				return true;
			});
			model.faces.push(face);
			
			face = ModelFace.fromFace(Face.FRONT).chainModelFace()
				.texture(texture)
				.r(r)
				.g(g)
				.b(b).ret();
			face.vertices.foreach(function(e) {
				if (e.x == 0) e.z = 1;
				return true;
			});
			model.faces.push(face);
			
			face = ModelFace.fromFace(Face.BACK).chainModelFace()
				.texture(texture)
				.r(r)
				.g(g)
				.b(b).ret();
			face.vertices.foreach(function(e) {
				if (e.x == 0) e.z = 1;
				return true;
			});
			model.faces.push(face);
		}
		super.render(builder, x, y, z, chunk);	
	}
	
}