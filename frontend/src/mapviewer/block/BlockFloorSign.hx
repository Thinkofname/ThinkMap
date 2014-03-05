package mapviewer.block;
import mapviewer.model.Model;
import mapviewer.block.Block.Face;

class BlockFloorSign {
	
	private static var _model : Model;
	public static var model(get, never) : Model;
	
	static function get_model() : Model {
		if (_model != null) return _model;
		var _model = new Model();
		var top = new Model();
		top.faces.push(ModelFace.fromFace(Face.FRONT).chainModelFace()
			.texture("planks_oak").ret()
			.moveZ(1)
			.sizeY(-8)
			.sizeY(-4, true)
			.sizeX(8, true)
			.moveY(4)
			.moveY(4, true)
			.moveX(18, true));
		top.faces.push(ModelFace.fromFace(Face.BACK).chainModelFace()
			.texture("planks_oak").ret()
			.sizeY(-8)
			.sizeY(-4, true)
			.sizeX(8, true)
			.moveY(4)
			.moveY(4, true)
			.moveX(18, true));
		top.faces.push(ModelFace.fromFace(Face.LEFT).chainModelFace()
			.texture("planks_oak").ret()
			.sizeZ(-15)
			.sizeX(-14, true)
			.moveX(16)
			.sizeY(-8)
			.sizeY(-4, true)
			.moveY(4));
		top.faces.push(ModelFace.fromFace(Face.RIGHT).chainModelFace()
			.texture("planks_oak").ret()
			.sizeZ(-15)
			.sizeX(-14, true)
			.sizeY(-8)
			.sizeY(-4, true)
			.moveY(4));
		top.faces.push(ModelFace.fromFace(Face.TOP).chainModelFace()
			.texture("planks_oak").ret()
			.sizeZ(-15)
			.sizeY(-14, true)
			.sizeX(8, true)
			.moveX(18, true)
			.moveY(12));
		top.faces.push(ModelFace.fromFace(Face.BOTTOM).chainModelFace()
			.texture("planks_oak").ret()
			.sizeZ(-15)
			.sizeY(-14, true)
			.sizeX(8, true)
			.moveX(18, true)
			.moveY(4));
		
		//Post
		var post = new Model();
		post.faces.push(ModelFace.fromFace(Face.FRONT).chainModelFace()
			.texture("log_oak").ret()
			.moveZ(1)
			.sizeX(-15)
			.sizeX( -14, true)
			.sizeY( -7)
			.sizeY( -4, true));
		post.faces.push(ModelFace.fromFace(Face.BACK).chainModelFace()
			.texture("log_oak").ret()
			.sizeX(-15)
			.sizeX( -14, true)
			.sizeY( -7)
			.sizeY( -4, true));
		post.faces.push(ModelFace.fromFace(Face.LEFT).chainModelFace()
			.texture("log_oak").ret()
			.moveX(1)
			.sizeZ(-15)
			.sizeX( -14, true)
			.sizeY( -7)
			.sizeY( -4, true));
		post.faces.push(ModelFace.fromFace(Face.RIGHT).chainModelFace()
			.texture("log_oak").ret()
			.sizeZ(-15)
			.sizeX( -14, true)
			.sizeY( -7)
			.sizeY( -4, true));
			
		return _model.join(top, 0, 5, 8).join(post, 7.5, 0, 8);
	}
}