part of mapViewer;

class Block {

    static final Block AIR = new Block._airBlock();

    static final Block STONE = new Block._internal(1, "minecraft:stone", 0x6D6D6D);
    static final Block GRASS = new Block._internal(2, "minecraft:grass", 0x609252, texture: "dirt"); //TODO:
    static final Block DIRT = new Block._internal(3, "minecraft:dirt", 0x715036, texture: "dirt");
    static final Block COBBLESTONE = new Block._internal(4, "minecraft:cobblestone", 0x505050, texture: "cobblestone");
    static final Block PLANKS = new Block._internal(5, "minecraft:planks", 0xB08E5C, texture: "planks_oak");
    static final Block SAPLINGS = new BlockSapling._internal(6, "minecraft:sapling");
    static final Block BEDROCK = new Block._internal(7, "minecraft:bedrock", 0x303030, texture: "bedrock");
    static final Block FLOWING_WATER = new BlockWater._internal(8, "minecraft:flowing_water", "water_flow");
    static final Block WATER = new BlockWater._internal(9, "minecraft:water", "water_still");
    static final Block FLOWING_LAVA = new Block._internal(10, "minecraft:flowing_lava", 0xC34509, texture: "lava_flow");
    static final Block LAVA = new Block._internal(11, "minecraft:lava", 0xC34509, texture: "lava_still");
    static final Block SAND = new Block._internal(12, "minecraft:sand", 0xCAC391, texture: "sand");
    static final Block GRAVEL = new Block._internal(13, "minecraft:gravel", 0x000000, texture: "gravel");
    static final Block GOLD_ORE = new Block._internal(14, "minecraft:gold_ore", 0x000000, texture: "gold_ore");
    static final Block IRON_ORE = new Block._internal(15, "minecraft:iron_ore", 0x000000, texture: "iron_ore");
    static final Block COAL_ORE = new Block._internal(16, "minecraft:coal_ore", 0x000000, texture: "coal_ore");
    static final Block LOG = new Block._internal(17, "minecraft:log", 0x000000, texture: "log_oak");
    static final Block LEAVES = new Block._internal(18, "minecraft:leaves", 0x000000, texture: "leaves_oak", solid: false);
    static final Block SPONGE = new Block._internal(19, "minecraft:sponge", 0x000000, texture: "sponge");
    static final Block GLASS = new Block._internal(20, "minecraft:glass", 0x000000, texture: "glass", solid: false);
    static final Block LAPIS_ORE = new Block._internal(21, "minecraft:lapis_ore", 0x000000, texture: "lapis_ore");
    static final Block LAPIS_BLOCK = new Block._internal(22, "minecraft:lapis_block", 0x000000, texture: "lapis_block");
    static final Block DISPENSER = new BlockSidedTextures._internal(22, "minecraft:dispenser", 0x000000)
                        ..textures = {
                            BlockFace.FRONT: "dispenser_front_horizontal",
                            BlockFace.TOP: "furnace_top",
                            BlockFace.BOTTOM: "furnace_top",
                            BlockFace.BACK: "furnace_side",
                            BlockFace.LEFT: "furnace_side",
                            BlockFace.RIGHT: "furnace_side"
                        };

    static Map<int, Block> _blocksLegacy = new Map();

    static Map<String, Block> _blocks = new Map();

    static var _allBlocks = [AIR, STONE, GRASS, DIRT, COBBLESTONE, PLANKS, SAPLINGS, BEDROCK, FLOWING_WATER, WATER,
        FLOWING_LAVA, LAVA, SAND, GRAVEL, GOLD_ORE, COAL_ORE, LOG, LEAVES, SPONGE, GLASS, LAPIS_ORE, LAPIS_BLOCK,
        DISPENSER];

    static Block blockFromName(String name) {
        return _blocks[name];
    }

    ///**Warning:** this will be dropped in future versions of Minecraft
    @deprecated
    static Block blockFromLegacyId(int id) {
        return _blocksLegacy[id];
    }

    ///**Warning:** this will be dropped in future versions of Minecraft
    @deprecated
    int legacyId;

    String name;

    bool renderable = true;
    bool transparent = false;

    bool solid = true;

    int _colour = 0xFFFFFF;
    String texture;

    Block._internal(int _legacyId, String _name, int _colour,
                    {String texture: "stone", bool solid: true, bool transparent : false}) {
        legacyId = _legacyId;
        name = _name;
        colour = _colour;
        this.texture = texture;
        _blocks[name] = this;
        _blocksLegacy[legacyId] = this;
        this.solid = solid;
        this.transparent = transparent;
    }

    factory Block._airBlock() {
        Block air = new Block._internal(0, "minecraft:air", 0xFFFFFF);
        air.renderable = false;
        air.solid = false;
        return air;
    }

    shouldRenderAgainst(Block block) => !block.solid;

    render(BlockBuilder builder, int x, int y, int z, Chunk chunk) {
        //TODO:
        int r = 255; //(colour >> 16) & 0xFF;
        int g = 255; //(colour >> 8) & 0xFF;
        int b = 255; //colour & 0xFF;

        if (shouldRenderAgainst(chunk.world.getBlock(leftShift(chunk.x, 4) + x, y + 1, leftShift(chunk.z, 4) + z))) {
            double topRight = 1.0 - (_numBlocksRegion(chunk, x - 1, y + 1, z - 1, x + 1, y + 2, z + 1) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, x, y + 1, z - 1, x + 2, y + 2, z + 1) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, x, y + 1, z, x + 2, y + 2, z + 2) / 4);
            double bottomRight = 1.0 - (_numBlocksRegion(chunk, x - 1, y + 1, z, x + 1, y + 2, z + 2) / 4);

            TextureInfo texture = getTexture(BlockFace.TOP);

            builder
                ..position(x, y + 1, z)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(1, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y + 1, z)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(0, 0)
                ..texId(texture.start, texture.end)
                ..position(x, y + 1, z + 1)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(1, 1)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y + 1, z)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(0, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y + 1, z + 1)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(0, 1)
                ..texId(texture.start, texture.end)
                ..position(x, y + 1, z + 1)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(1, 1)
                ..texId(texture.start, texture.end);
        }

        //TODO: Bottom side

        if (shouldRenderAgainst(chunk.world.getBlock(leftShift(chunk.x, 4) + x + 1, y, leftShift(chunk.z, 4) + z))) {
            double topRight = 1.0 - (_numBlocksRegion(chunk, x + 1, y, z, x + 2, y + 2, z + 2) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, x + 1, y, z - 1, x + 2, y + 2, z + 1) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, x + 1, y - 1, z - 1, x + 2, y + 1, z + 1) / 4);
            double bottomRight = 1.0 - (_numBlocksRegion(chunk, x + 1, y - 1, z, x + 2, y + 1, z + 2) / 4);

            TextureInfo texture = getTexture(BlockFace.LEFT);

            builder
                ..position(x + 1, y, z)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(0, 1)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y, z + 1)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(1, 1)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y + 1, z)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(0, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y + 1, z + 1)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(1, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y + 1, z)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(0, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y, z + 1)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(1, 1)
                ..texId(texture.start, texture.end);
        }

        if (shouldRenderAgainst(chunk.world.getBlock(leftShift(chunk.x, 4) + x - 1, y, leftShift(chunk.z, 4) + z))) {
            double topRight = 1.0 - (_numBlocksRegion(chunk, x - 1, y, z - 1, x, y + 2, z + 1) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, x - 1, y, z, x, y + 2, z + 2) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, x - 1, y - 1, z, x, y + 1, z + 2) / 4);
            double bottomRight = 1.0 - (_numBlocksRegion(chunk, x - 1, y - 1, z - 1, x, y + 1, z + 1) / 4);

            TextureInfo texture = getTexture(BlockFace.RIGHT);

            builder
                ..position(x, y, z)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(1, 1)
                ..texId(texture.start, texture.end)
                ..position(x, y + 1, z)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(1, 0)
                ..texId(texture.start, texture.end)
                ..position(x, y, z + 1)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(0, 1)
                ..texId(texture.start, texture.end)
                ..position(x, y + 1, z)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(1, 0)
                ..texId(texture.start, texture.end)
                ..position(x, y + 1, z + 1)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(0, 0)
                ..texId(texture.start, texture.end)
                ..position(x, y, z + 1)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(0, 1)
                ..texId(texture.start, texture.end);
        }

        if (shouldRenderAgainst(chunk.world.getBlock(leftShift(chunk.x, 4) + x, y, leftShift(chunk.z, 4) + z + 1))) {
            double topRight = 1.0 - (_numBlocksRegion(chunk, x - 1, y, z + 1, x + 1, y + 2, z + 2) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, x, y, z + 1, x + 2, y + 2, z + 2) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, x, y - 1, z + 1, x + 2, y + 1, z + 2) / 4);
            double bottomRight = 1.0 - (_numBlocksRegion(chunk, x - 1, y - 1, z + 1, x + 1, y + 1, z + 2) / 4);

            TextureInfo texture = getTexture(BlockFace.FRONT);

            builder
                ..position(x, y, z + 1)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(0, 1)
                ..texId(texture.start, texture.end)
                ..position(x, y + 1, z + 1)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(0, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y, z + 1)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(1, 1)
                ..texId(texture.start, texture.end)
                ..position(x, y + 1, z + 1)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(0, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y + 1, z + 1)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(1, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y, z + 1)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(1, 1)
                ..texId(texture.start, texture.end);
        }

        if (shouldRenderAgainst(chunk.world.getBlock(leftShift(chunk.x, 4) + x, y, leftShift(chunk.z, 4) + z - 1))) {
            double topRight = 1.0 - (_numBlocksRegion(chunk, x, y, z - 1, x + 2, y + 2, z) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, x - 1, y, z - 1, x + 1, y + 2, z) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, x - 1, y - 1, z - 1, x + 1, y + 1, z) / 4);
            double bottomRight = 1.0 - (_numBlocksRegion(chunk, x, y - 1, z - 1, x + 2, y + 1, z) / 4);

            TextureInfo texture = getTexture(BlockFace.BACK);

            builder
                ..position(x, y, z)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(0, 1)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y, z)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(1, 1)
                ..texId(texture.start, texture.end)
                ..position(x, y + 1, z)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(0, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y + 1, z)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(1, 0)
                ..texId(texture.start, texture.end)
                ..position(x, y + 1, z)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(0, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y, z)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(1, 1)
                ..texId(texture.start, texture.end);
        }
    }

    static int _numBlocksRegion(Chunk chunk, int x1, int y1, int z1, int x2, int y2, int z2) {
        int count = 0;
        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                if (y < 0 || y > 255) continue;
                for (int z = z1; z < z2; z++) {
                    if (chunk.world.getBlock(leftShift(chunk.x, 4) + x, y, leftShift(chunk.z, 4) + z).solid) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    TextureInfo getTexture(BlockFace face) {
        return blockTextureInfo[texture];
    }

    set colour(int c) {
        _colour = c;
    }

    int get colour {
        return _colour;
    }
}

class BlockFace {

    static const TOP = const BlockFace(0);
    static const BOTTOM = const BlockFace(1);
    static const RIGHT = const BlockFace(2);
    static const LEFT = const BlockFace(3);
    static const BACK = const BlockFace(4);
    static const FRONT = const BlockFace(5);

    final int id;
    const BlockFace(this.id);
}

class TextureInfo {
    int start;
    int end;

    TextureInfo(this.start, this.end);
}