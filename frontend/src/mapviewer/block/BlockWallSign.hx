package mapviewer.block;
import mapviewer.model.Model;
import mapviewer.block.Block.Face;

class BlockWallSign {
	
	private static var _model : Model;
	public static var model(get, never) : Model;
	
	static function get_model() : Model {
		if (_model != null) return _model;
		_model = new Model();
		_model.faces.push(ModelFace.fromFace(Face.FRONT).chainModelFace()
			.texture("planks_oak").ret()
			.moveZ(1)
			.sizeY(-8)
			.sizeY(-4, true)
			.sizeX(8, true)
			.moveY(4)
			.moveY(4, true)
			.moveX(18, true));
		_model.faces.push(ModelFace.fromFace(Face.BACK).chainModelFace()
			.texture("planks_oak").ret()
			.sizeY(-8)
			.sizeY(-4, true)
			.sizeX(8, true)
			.moveY(4)
			.moveY(4, true)
			.moveX(18, true));
		_model.faces.push(ModelFace.fromFace(Face.LEFT).chainModelFace()
			.texture("planks_oak").ret()
			.sizeZ(-15)
			.sizeX(-14, true)
			.moveX(16)
			.sizeY(-8)
			.sizeY(-4, true)
			.moveY(4));
		_model.faces.push(ModelFace.fromFace(Face.RIGHT).chainModelFace()
			.texture("planks_oak").ret()
			.sizeZ(-15)
			.sizeX(-14, true)
			.sizeY(-8)
			.sizeY(-4, true)
			.moveY(4));
		_model.faces.push(ModelFace.fromFace(Face.TOP).chainModelFace()
			.texture("planks_oak").ret()
			.sizeZ(-15)
			.sizeY(-4, true)
			.moveY(12));
		_model.faces.push(ModelFace.fromFace(Face.BOTTOM).chainModelFace()
			.texture("planks_oak").ret()
			.sizeZ(-15)
			.sizeY(-4, true)
			.moveY(4));
		return _model;
	}
}