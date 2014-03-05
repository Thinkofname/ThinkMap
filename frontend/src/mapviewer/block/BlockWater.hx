package mapviewer.block;

class BlockWater extends Block {

	public function new() {
		super();
	}
	
	override public function shouldRenderAgainst(block : Block) : Bool {
		return !block.solid && block != Blocks.WATER && block != Blocks.FLOWING_WATER;
	}
}