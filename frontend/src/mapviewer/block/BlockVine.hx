package mapviewer.block;
import mapviewer.model.Model;
import mapviewer.world.Chunk;
import mapviewer.renderer.webgl.BlockBuilder;

/**
 * ...
 * @author Thinkofdeath
 */
class BlockVine extends Block {
	
	static var top : Model = new Model();
	
	public static function register() {		
		top.faces.push(ModelFace.fromFace(Block.Face.BOTTOM)			
			.moveY(15).chainModelFace()
			.r(0x87)
			.g(0xBA)
			.b(0x34)
			.texture("vine")
			.ret());
		for (i in 0 ... 16) {
			var model = new Model();
			if (i == 0) {
				model.faces.concat(top.faces);
			}
			if (i & 2 == 2) {
				model.faces.push(ModelFace.fromFace(Block.Face.LEFT)
					.moveX(1).chainModelFace()
					.r(0x87)
					.g(0xBA)
					.b(0x34)
					.texture("vine")
					.ret());
			}
			if (i & 8 == 8) {
				model.faces.push(ModelFace.fromFace(Block.Face.RIGHT)
					.moveX(15).chainModelFace()
					.r(0x87)
					.g(0xBA)
					.b(0x34)
					.texture("vine")
					.ret());
			}
			if (i & 4 == 4) {
				model.faces.push(ModelFace.fromFace(Block.Face.FRONT)
					.moveZ(1).chainModelFace()
					.r(0x87)
					.g(0xBA)
					.b(0x34)
					.texture("vine")
					.ret());
			}
			if (i & 1 == 1) {
				model.faces.push(ModelFace.fromFace(Block.Face.BACK)
					.moveZ(15).chainModelFace()
					.r(0x87)
					.g(0xBA)
					.b(0x34)
					.texture("vine")
					.ret());
			}
			BlockRegistry.registerBlock('vine_$i', new BlockVine().chainBlock()
				.solid(false)
				.collidable(false)
				.forceColour(true)
				.colour(0x87BA34)
				.texture("vine")
				.model(model).ret())
				.legacyId(106)
				.dataValue(i)
				.build();
		}
	}

	public function new() {
		super();
	}
	
	override public function shouldRenderAgainst(block : Block) : Bool {
		return !block.solid;
	}
	
	override public function render(builder : BlockBuilder, x : Int, y : Int, z : Int, chunk : Chunk) {
		model.render(builder, x, y, z, chunk);
		if (!shouldRenderAgainst(chunk.world.getBlock((chunk.x << 4) + x, y + 1, (chunk.z << 4) + z))) {
			top.render(builder, x, y, z, chunk);
		}
	}
	
}