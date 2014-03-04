package mapviewer.block;

/*
    Contains static regerences to blocks needed by the map viewer
 */
import mapviewer.block.BlockRegistry.BlockRegistrationEntry;
class Blocks {

    /// Air - Pretty much what you would expect it to be
    public static var AIR(get, never) : Block;
	static function get_AIR() { return BlockRegistry.get("air"); }
	
    /// Bedrock - Unbreakable block (unless Mojang messes up... again)
    public static var BEDROCK(get, never) : Block;
	static function get_BEDROCK() { return BlockRegistry.get("bedrock"); }
	
    /// Water - Blue swimmable stuff, strangely not drinkable
    public static var WATER(get, never) : Block;
	static function get_WATER() { return BlockRegistry.get("water"); }
	
    /// Flowing Water - Blue swimmable stuff that is flowing in a direction
    public static var FLOWING_WATER(get, never) : Block;
	static function get_FLOWING_WATER() { return BlockRegistry.get("flowing_water"); }
	
    /**
     * Missing block - If you see this block I messed up somewhere (or you have some sort of
     * mod on the server)
     */
    public static var MISSING_BLOCK(get, never) : Block;
	static function get_MISSING_BLOCK() { return BlockRegistry.get("webglmap:missing_block"); }
	
    /// Null Block - Solid air
    public static var NULL_BLOCK(get, never) : Block;
	static function get_NULL_BLOCK() { return BlockRegistry.get("webglmap:null"); }
}
