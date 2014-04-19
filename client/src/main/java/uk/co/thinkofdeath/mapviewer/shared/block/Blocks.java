package uk.co.thinkofdeath.mapviewer.shared.block;

public final class Blocks {

    public static Block AIR;
    public static Block NULL_BLOCK;
    public static Block MISSING_BLOCK;

    /**
     * Called to load the static values after the blocks have been registered
     */
    public static void init(BlockRegistry blockRegistry) {
        AIR = blockRegistry.get("minecraft:air");
        NULL_BLOCK = blockRegistry.get("thinkmap:null");
        MISSING_BLOCK = blockRegistry.get("thinkmap:missing_block");
    }
}
