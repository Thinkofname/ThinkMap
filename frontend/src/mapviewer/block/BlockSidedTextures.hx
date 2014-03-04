package mapviewer.block;
import mapviewer.block.Block.Face;
import mapviewer.renderer.TextureInfo;

class BlockSidedTextures extends Block {

	@:chain public var textures : Map<String, String>;
	
	public function new() {
		super();
	}
	
	override public function getTexture(face : Face) : TextureInfo {
		return Main.blockTextureInfo[textures[face.name]];
	}
	
}