part of mapViewer;

class Block {

    static final Block AIR = new Block._airBlock();

    static final Block STONE = new Block._internal(1, "minecraft:stone", 0x6D6D6D);
    static final Block GRASS = new Block._internal(2, "minecraft:grass", 0x609252);
    static final Block DIRT = new Block._internal(3, "minecraft:dirt", 0x715036, "dirt");
    static final Block COBBLESTONE = new Block._internal(4, "minecraft:cobblestone", 0x505050);
    static final Block PLANKS = new Block._internal(5, "minecraft:planks", 0xB08E5C);
    static final Block SAPLINGS = new Block._internal(6, "minecraft:sapling", 0x49CC25);
    static final Block BEDROCK = new Block._internal(7, "minecraft:bedrock", 0x303030);
    static final Block FLOWING_WATER = new Block._internal(8, "minecraft:flowing_water", 0x2838E2);
    static final Block WATER = new Block._internal(9, "minecraft:water", 0x2838E2);
    static final Block FLOWING_LAVA = new Block._internal(10, "minecraft:flowing_lava", 0xC34509);
    static final Block LAVA = new Block._internal(11, "minecraft:lava", 0xC34509);
    static final Block SAND = new Block._internal(12, "minecraft:sand", 0xCAC391);

    static Map<int, Block> _blocksLegacy = new Map();

    static Map<String, Block> _blocks = new Map();

    static var _allBlocks = [AIR, STONE, GRASS, DIRT, COBBLESTONE, PLANKS, SAPLINGS, BEDROCK, FLOWING_WATER, WATER, FLOWING_LAVA, LAVA, SAND];

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

    bool solid = true;

    int _colour = 0xFFFFFF;
    String texture;

    Block._internal(int _legacyId, String _name, int _colour, [String texture = "stone"]) {
        legacyId = _legacyId;
        name = _name;
        colour = _colour;
        this.texture = texture;
        _blocks[name] = this;
        _blocksLegacy[legacyId] = this;
    }

    factory Block._airBlock() {
        Block air = new Block._internal(0, "minecraft:air", 0xFFFFFF);
        air.renderable = false;
        air.solid = false;
        return air;
    }

    render(BlockBuilder builder, int x, int y, int z, Chunk chunk) {
        //TODO:
        int r = 255; //(colour >> 16) & 0xFF;
        int g = 255; //(colour >> 8) & 0xFF;
        int b = 255; //colour & 0xFF;

        if (!chunk.world.getBlock((chunk.x<<4) + x, y + 1, (chunk.z<<4) + z).solid) {
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

        if (!chunk.world.getBlock((chunk.x<<4) + x + 1, y, (chunk.z<<4) + z).solid) {
            double topRight = 1.0 - (_numBlocksRegion(chunk, x + 1, y, z, x + 2, y + 2, z + 2) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, x + 1, y, z - 1, x + 2, y + 2, z + 1) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, x + 1, y - 1, z - 1, x + 2, y + 1, z + 1) / 4);
            double bottomRight = 1.0 - (_numBlocksRegion(chunk, x + 1, y - 1, z, x + 2, y + 1, z + 2) / 4);
            builder
                ..position(x + 1, y, z)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(0, 0)
                ..texId(0, 0)
                ..position(x + 1, y, z + 1)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(0, 0)
                ..texId(0, 0)
                ..position(x + 1, y + 1, z)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(0, 0)
                ..texId(0, 0)
                ..position(x + 1, y + 1, z + 1)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(0, 0)
                ..texId(0, 0)
                ..position(x + 1, y + 1, z)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(0, 0)
                ..texId(0, 0)
                ..position(x + 1, y, z + 1)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(0, 0)
                ..texId(0, 0);
        }

        if (!chunk.world.getBlock((chunk.x<<4) + x - 1, y, (chunk.z<<4) + z).solid) {
            double topRight = 1.0 - (_numBlocksRegion(chunk, x - 1, y, z - 1, x, y + 2, z + 1) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, x - 1, y, z, x, y + 2, z + 2) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, x - 1, y - 1, z, x, y + 1, z + 2) / 4);
            double bottomRight = 1.0 - (_numBlocksRegion(chunk, x - 1, y - 1, z - 1, x, y + 1, z + 1) / 4);
            builder
                ..position(x, y, z)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(0, 0)
                ..texId(0, 0)
                ..position(x, y + 1, z)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(0, 0)
                ..texId(0, 0)
                ..position(x, y, z + 1)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(0, 0)
                ..texId(0, 0)
                ..position(x, y + 1, z)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(0, 0)
                ..texId(0, 0)
                ..position(x, y + 1, z + 1)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(0, 0)
                ..texId(0, 0)
                ..position(x, y, z + 1)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(0, 0)
                ..texId(0, 0);
        }

        if (!chunk.world.getBlock((chunk.x<<4) + x, y, (chunk.z<<4) + z + 1).solid) {
            double topRight = 1.0 - (_numBlocksRegion(chunk, x - 1, y, z + 1, x + 1, y + 2, z + 2) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, x, y, z + 1, x + 2, y + 2, z + 2) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, x, y - 1, z + 1, x + 2, y + 1, z + 2) / 4);
            double bottomRight = 1.0 - (_numBlocksRegion(chunk, x - 1, y - 1, z + 1, x + 1, y + 1, z + 2) / 4);
            builder
                ..position(x, y, z + 1)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(0, 0)
                ..texId(0, 0)
                ..position(x, y + 1, z + 1)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(0, 0)
                ..texId(0, 0)
                ..position(x + 1, y, z + 1)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(0, 0)
                ..texId(0, 0)
                ..position(x, y + 1, z + 1)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(0, 0)
                ..texId(0, 0)
                ..position(x + 1, y + 1, z + 1)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(0, 0)
                ..texId(0, 0)
                ..position(x + 1, y, z + 1)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(0, 0)
                ..texId(0, 0);
        }

        if (!chunk.world.getBlock((chunk.x<<4) + x, y, (chunk.z<<4) + z - 1).solid) {
            double topRight = 1.0 - (_numBlocksRegion(chunk, x, y, z - 1, x + 2, y + 2, z) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, x - 1, y, z - 1, x + 1, y + 2, z) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, x - 1, y - 1, z - 1, x + 1, y + 1, z) / 4);
            double bottomRight = 1.0 - (_numBlocksRegion(chunk, x, y - 1, z - 1, x + 2, y + 1, z) / 4);
            builder
                ..position(x, y, z)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(0, 0)
                ..texId(0, 0)
                ..position(x + 1, y, z)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(0, 0)
                ..texId(0, 0)
                ..position(x, y + 1, z)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(0, 0)
                ..texId(0, 0)
                ..position(x + 1, y + 1, z)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(0, 0)
                ..texId(0, 0)
                ..position(x, y + 1, z)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(0, 0)
                ..texId(0, 0)
                ..position(x + 1, y, z)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(0, 0)
                ..texId(0, 0);
        }
    }

    static int _numBlocksRegion(Chunk chunk, int x1, int y1, int z1, int x2, int y2, int z2) {
        int count = 0;
        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                for (int z = z1; z < z2; z++) {
                    if (chunk.world.getBlock((chunk.x<<4) + x, y, (chunk.z<<4) + z).solid) {
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