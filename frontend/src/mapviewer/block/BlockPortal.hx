package mapviewer.block;
import mapviewer.model.Model;
import mapviewer.renderer.webgl.BlockBuilder;
import mapviewer.world.Chunk;
import mapviewer.block.Block.Face;

class BlockPortal extends Block { 

	private static var _model : Model;
	public static var modelNormal(get, never) : Model;
	private static var _modelRot : Model;
	public static var modelRot(get, never) : Model;
	
	static function get_modelNormal() : Model {
		if (_model != null) return _model;
		_model = new Model();
		_model.faces.push(ModelFace.fromFace(Face.LEFT).chainModelFace()
			.texture("portal").ret()
			.moveX(10));
		_model.faces.push(ModelFace.fromFace(Face.RIGHT).chainModelFace()
			.texture("portal").ret()
			.moveX(6));
		return _model;
	}
	
	static function get_modelRot() : Model {
		if (_modelRot != null) return _modelRot;
		return _modelRot = modelNormal.clone().rotateY(90);
	}

	public function new() {
		super();
	}
	
	override public function render(builder : BlockBuilder, x : Int, y : Int, z : Int, chunk : Chunk) {
		if (chunk.world.getBlock((chunk.x << 4) + x - 1, y, (chunk.z << 4) + z) == this
			|| chunk.world.getBlock((chunk.x << 4) + x + 1, y, (chunk.z << 4) + z) == this) {
			modelRot.render(builder, x, y, z, chunk);
		} else {
			modelNormal.render(builder, x, y, z, chunk);
		}
	}
}