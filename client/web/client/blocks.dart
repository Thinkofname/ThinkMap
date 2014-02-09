part of mapViewer;

class Block {

    static final Block AIR = new Block._airBlock();
    static final Block STONE = new Block._internal(1, "minecraft:stone", 0x6D6D6D);
    static final Block GRASS = new BlockGrass._internal(2, "minecraft:grass", 0xA7D389);
    static final Block DIRT = new Block._internal(3, "minecraft:dirt", 0x715036, texture: "dirt");
    static final Block COBBLESTONE = new Block._internal(4, "minecraft:cobblestone", 0x505050, texture: "cobblestone");
    static final Block PLANKS = new Block._internal(5, "minecraft:planks", 0xB08E5C, texture: "planks_oak");
    static final Block SAPLING = new BlockCross._internal(6, "minecraft:sapling")..collidable = false;
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
    static final Block LEAVES = new Block._internal(18, "minecraft:leaves", 0xA7D389, texture: "leaves_oak", solid: false, forceColour: true)..allowSelf = true;
    static final Block SPONGE = new Block._internal(19, "minecraft:sponge", 0x000000, texture: "sponge");
    static final Block GLASS = new Block._internal(20, "minecraft:glass", 0x000000, texture: "glass", solid: false);
    static final Block LAPIS_ORE = new Block._internal(21, "minecraft:lapis_ore", 0x000000, texture: "lapis_ore");
    static final Block LAPIS_BLOCK = new Block._internal(22, "minecraft:lapis_block", 0x000000, texture: "lapis_block");
    static final Block DISPENSER = new BlockSidedTextures._internal(23, "minecraft:dispenser", 0x000000)
                        ..textures = {
                            BlockFace.FRONT: "dispenser_front_horizontal",
                            BlockFace.TOP: "furnace_top",
                            BlockFace.BOTTOM: "furnace_top",
                            BlockFace.BACK: "furnace_side",
                            BlockFace.LEFT: "furnace_side",
                            BlockFace.RIGHT: "furnace_side"
                        };
    static final Block SANDSTONE = new BlockSidedTextures._internal(24, "minecraft:sandstone", 0x000000)
        ..textures = {
        BlockFace.FRONT: "sandstone_normal",
        BlockFace.TOP: "sandstone_top",
        BlockFace.BOTTOM: "sandstone_bottom",
        BlockFace.BACK: "sandstone_normal",
        BlockFace.LEFT: "sandstone_normal",
        BlockFace.RIGHT: "sandstone_normal"
    };
    static final Block NOTEBLOCK = new Block._internal(25, "minecraft:noteblock", 0x000000, texture: "noteblock");
    static final Block BED = new BlockBed._internal(26, "minecraft:bed");
    static final Block GOLDEN_RAIL = new BlockFlat._internal(27, "minecraft:golden_rail", "rail_golden");
    static final Block DETECTOR_RAIL = new BlockFlat._internal(28, "minecraft:detector_rail", "rail_detector");

    static final Block TALL_GRASS = new BlockCross._internal(31, "minecraft:tallgrass", texture: "tallgrass", forceColour: true, colour: 0xA7D389)
        ..collidable = false;

    static final Block GLOWSTONE = new Block._internal(89, "minecraft:glowstone", 0x000000, texture: "glowstone");

    static final Block VINES = new BlockVines._internal(106, "minecraft:vine", 0x426B27)..collidable = false;


    //TODO: Remove
    static final Block MISSING_BLOCK = new Block._internal(0xFF, "thinkofdeath:messed_up", 0x000000, texture: "missing_texture");

    static Map<int, Block> _blocksLegacy = new Map();

    static Map<String, Block> _blocks = new Map();

    static var _allBlocks = [AIR, STONE, GRASS, DIRT, COBBLESTONE, PLANKS, SAPLING, BEDROCK, FLOWING_WATER, WATER,
        FLOWING_LAVA, LAVA, SAND, GRAVEL, GOLD_ORE, COAL_ORE, LOG, LEAVES, SPONGE, GLASS, LAPIS_ORE, LAPIS_BLOCK,
        DISPENSER, SANDSTONE, NOTEBLOCK, BED, GOLDEN_RAIL, DETECTOR_RAIL, TALL_GRASS, GLOWSTONE, VINES, MISSING_BLOCK];

    static Block blockFromName(String name) {
        Block ret = _blocks[name];
        if (ret == null) ret = Block.MISSING_BLOCK;
        return ret;
    }

    ///**Warning:** this will be dropped in future versions of Minecraft
    @deprecated
    static Block blockFromLegacyId(int id) {
        Block ret = _blocksLegacy[id];
        if (ret == null) ret = Block.MISSING_BLOCK;
        return ret;
    }

    ///**Warning:** this will be dropped in future versions of Minecraft
    @deprecated
    int legacyId;

    String name;

    bool renderable = true;
    bool transparent = false;

    bool solid = true;
    bool collidable = true;

    int _colour = 0xFFFFFF;
    bool forceColour;
    String texture;
    bool allowSelf = true;

    Block._internal(int _legacyId, String _name, int _colour,
                    {String texture: "stone", bool solid: true, bool transparent : false, bool forceColour : false}) {
        legacyId = _legacyId;
        name = _name;
        colour = _colour;
        this.texture = texture;
        _blocks[name] = this;
        _blocksLegacy[legacyId] = this;
        this.solid = solid;
        this.transparent = transparent;
        this.forceColour = forceColour;
    }

    factory Block._airBlock() {
        Block air = new Block._internal(0, "minecraft:air", 0xFFFFFF);
        air.renderable = false;
        air.solid = false;
        air.collidable = false;
        return air;
    }

    bool collidesWith(Box box, int x, int y, int z) {
        if (!collidable) return false;
        return box.checkBox(x.toDouble(), y.toDouble(), z.toDouble(), 1.0, 1.0, 1.0);
    }

    shouldRenderAgainst(Block block) => !block.solid && (allowSelf || block != this);

    renderCanvas(Uint8ClampedList data, int width, int x, int y, int z, int ry, CanvasChunk chunk) {

        int gr = (colour >> 16) & 0xFF;
        int gg = (colour >> 8) & 0xFF;
        int gb = colour & 0xFF;

        if (!forceColour) {
            gr = 255;
            gg = 255;
            gb = 255;
        }

        // Left side
        int offsetX = x * 16 + z * 16;
        int offsetY = x * 8 + (15-z) * 8 + ((15-y) * 16 + 8);

        bool renderLeft = shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, ry, (chunk.z * 16) + z - 1));
        bool renderRight = shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x + 1, ry, (chunk.z * 16) + z));
        if (renderLeft || renderRight) {
            TextureInfo texture = getTexture(BlockFace.LEFT);
            Uint8ClampedList textureData = (renderer as CanvasRenderer).blockRawData[texture.start].data;
            int textureDataWidth = (renderer as CanvasRenderer).blockRawData[texture.start].width;

            double bottomLeft = renderLeft ? 1.0 - (_numBlocksRegion(chunk, this, x, ry, z - 1, x + 2, ry + 2, z) / 4) : 0.0;
            double bottomRight = renderLeft ? 1.0 - (_numBlocksRegion(chunk, this, x - 1, ry, z - 1, x + 1, ry + 2, z) / 4) : 0.0;
            double topRight = renderLeft ? 1.0 - (_numBlocksRegion(chunk, this, x - 1, ry - 1, z - 1, x + 1, ry + 1, z) / 4) : 0.0;
            double topLeft = renderLeft ? 1.0 - (_numBlocksRegion(chunk, this, x, ry - 1, z - 1, x + 2, ry + 1, z) / 4) : 0.0;

            TextureInfo textureRight = getTexture(BlockFace.FRONT);
            Uint8ClampedList textureDataRight = (renderer as CanvasRenderer).blockRawData[textureRight.start].data;

            double bottomLeftRight = renderRight ? 1.0 - (_numBlocksRegion(chunk, this, x + 1, ry, z, x + 2, ry + 2, z + 2) / 4) : 0.0;
            double bottomRightRight = renderRight ? 1.0 - (_numBlocksRegion(chunk, this, x + 1, ry, z - 1, x + 2, ry + 2, z + 1) / 4) : 0.0;
            double topRightRight = renderRight ? 1.0 - (_numBlocksRegion(chunk, this, x + 1, ry - 1, z - 1, x + 2, ry + 1, z + 1) / 4) : 0.0;
            double topLeftRight = renderRight ? 1.0 - (_numBlocksRegion(chunk, this, x + 1, ry - 1, z, x + 2, ry + 1, z + 2) / 4) : 0.0;

            for (int tx = 0; tx < 16; tx++) {
                for (int ty = 0; ty < 16; ty++) {
                    int i = tx + ty * textureDataWidth;
                    i *= 4;

                    int r = (textureData[i] * (gr/255)).toInt();
                    int g = (textureData[i + 1] * (gg/255)).toInt();
                    int b = (textureData[i + 2] * (gb/255)).toInt();
                    int a = textureData[i + 3];

                    if (renderLeft) {
                        double modi = ((topLeft * (tx/16) + topRight * ((15-tx)/16))*(ty/16))
                        + ((bottomLeft * (tx/16) + bottomRight * ((15-tx)/16))*((15-ty)/16));

                        putPixel(data, width, (offsetX + tx).toInt(), (offsetY + ty + tx*0.5).toInt(),
                        (r * modi).toInt(), (g * modi).toInt(), (b * modi).toInt(), a);
                    }

                    r = (textureDataRight[i] * (gr/255)).toInt();
                    g = (textureDataRight[i + 1] * (gg/255)).toInt();
                    b = (textureDataRight[i + 2] * (gb/255)).toInt();
                    a = textureDataRight[i + 3];

                    if (renderRight) {
                        double modi = ((topLeftRight * (tx/16) + topRightRight * ((15-tx)/16))*(ty/16))
                        + ((bottomLeftRight * (tx/16) + bottomRightRight * ((15-tx)/16))*((15-ty)/16));

                        putPixel(data, width, (offsetX + 16 + tx).toInt(), (offsetY + ty + 8 - tx*0.5).toInt(),
                        (r * modi).toInt(), (g * modi).toInt(), (b * modi).toInt(), a);
                    }
                }
            }
        }
        //Top side
        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, ry + 1, (chunk.z * 16) + z))) {
            TextureInfo texture = getTexture(BlockFace.TOP);
            Uint8ClampedList textureData = (renderer as CanvasRenderer).blockRawData[texture.start].data;
            int textureDataWidth = (renderer as CanvasRenderer).blockRawData[texture.start].width;

            double bottomRight = 1.0 - (_numBlocksRegion(chunk, this, x - 1, ry + 1, z - 1, x + 1, ry + 2, z + 1) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, this, x, ry + 1, z - 1, x + 2, ry + 2, z + 1) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, this, x, ry + 1, z, x + 2, ry + 2, z + 2) / 4);
            double topRight = 1.0 - (_numBlocksRegion(chunk, this, x - 1, ry + 1, z, x + 1, ry + 2, z + 2) / 4);

            //Overdraw to fix white lines
            for (int ttx = -1; ttx < 17; ttx++) {
                for (int tty = -1; tty < 16; tty++) {
                    int tx = ttx.clamp(0, 15);
                    int ty = tty.clamp(0, 15);
                    int i = tx + ty * textureDataWidth;
                    i *= 4;
                    int r = (textureData[i] * (gr/255)).toInt();
                    int g = (textureData[i + 1] * (gg/255)).toInt();
                    int b = (textureData[i + 2] * (gb/255)).toInt();
                    int a = textureData[i + 3];

                    double modi = ((topLeft * (tx/16) + topRight * ((15-tx)/16))*(ty/16))
                    + ((bottomLeft * (tx/16) + bottomRight * ((15-tx)/16))*((15-ty)/16));

                    putPixel(data, width, (offsetX + 1.5 + ttx + tty).toInt(), (offsetY + ttx * 0.5 - 0.75 - tty * 0.5).toInt(),
                    (r * modi).toInt(), (g * modi).toInt(), (b * modi).toInt(), a);
                }
            }
        }
    }

    renderFloat(BlockBuilder builder, FloatBlockBuilder fBulider, int x, int y, int z, Chunk chunk) {
        render(builder, x, y, z, chunk);
    }

    render(BlockBuilder builder, int x, int y, int z, Chunk chunk) {
        int r = 255;
        int g = 255;
        int b = 255;
        if (forceColour) {
            r = (colour >> 16) & 0xFF;
            g = (colour >> 8) & 0xFF;
            b = colour & 0xFF;
        }

        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, y + 1, (chunk.z * 16) + z))) {
            double topRight = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y + 1, z - 1, x + 1, y + 2, z + 1) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, this, x, y + 1, z - 1, x + 2, y + 2, z + 1) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, this, x, y + 1, z, x + 2, y + 2, z + 2) / 4);
            double bottomRight = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y + 1, z, x + 1, y + 2, z + 2) / 4);

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


        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, y - 1, (chunk.z * 16) + z))) {
            double topRight = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y - 1, z - 1, x + 1, y, z + 1) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, this, x, y - 1, z - 1, x + 2, y, z + 1) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, this, x, y - 1, z, x + 2, y, z + 2) / 4);
            double bottomRight = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y - 1, z, x + 1, y, z + 2) / 4);

            TextureInfo texture = getTexture(BlockFace.BOTTOM);

            builder
                ..position(x, y, z)
                ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
                ..tex(1, 0)
                ..texId(texture.start, texture.end)
                ..position(x, y, z + 1)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(1, 1)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y, z)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(0, 0)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y, z)
                ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
                ..tex(0, 0)
                ..texId(texture.start, texture.end)
                ..position(x, y, z + 1)
                ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
                ..tex(1, 1)
                ..texId(texture.start, texture.end)
                ..position(x + 1, y, z + 1)
                ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
                ..tex(0, 1)
                ..texId(texture.start, texture.end);
        }

        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x + 1, y, (chunk.z * 16) + z))) {
            double topRight = 1.0 - (_numBlocksRegion(chunk, this, x + 1, y, z, x + 2, y + 2, z + 2) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, this, x + 1, y, z - 1, x + 2, y + 2, z + 1) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, this, x + 1, y - 1, z - 1, x + 2, y + 1, z + 1) / 4);
            double bottomRight = 1.0 - (_numBlocksRegion(chunk, this, x + 1, y - 1, z, x + 2, y + 1, z + 2) / 4);

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

        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x - 1, y, (chunk.z * 16) + z))) {
            double topRight = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y, z - 1, x, y + 2, z + 1) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y, z, x, y + 2, z + 2) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y - 1, z, x, y + 1, z + 2) / 4);
            double bottomRight = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y - 1, z - 1, x, y + 1, z + 1) / 4);

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

        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, y, (chunk.z * 16) + z + 1))) {
            double topRight = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y, z + 1, x + 1, y + 2, z + 2) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, this, x, y, z + 1, x + 2, y + 2, z + 2) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, this, x, y - 1, z + 1, x + 2, y + 1, z + 2) / 4);
            double bottomRight = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y - 1, z + 1, x + 1, y + 1, z + 2) / 4);

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

        if (shouldRenderAgainst(chunk.world.getBlock((chunk.x * 16) + x, y, (chunk.z * 16) + z - 1))) {
            double topRight = 1.0 - (_numBlocksRegion(chunk, this, x, y, z - 1, x + 2, y + 2, z) / 4);
            double topLeft = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y, z - 1, x + 1, y + 2, z) / 4);
            double bottomLeft = 1.0 - (_numBlocksRegion(chunk, this, x - 1, y - 1, z - 1, x + 1, y + 1, z) / 4);
            double bottomRight = 1.0 - (_numBlocksRegion(chunk, this, x, y - 1, z - 1, x + 2, y + 1, z) / 4);

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

    static double _numBlocksRegion(Chunk chunk, Block self, int x1, int y1, int z1, int x2, int y2, int z2) {
        double count = 0.0;
        for (int y = y1; y < y2; y++) {
            if (y < 0 || y > 255) continue;
            for (int x = x1; x < x2; x++) {
                for (int z = z1; z < z2; z++) {
                    double val = pow(0.9,
                        max(chunk.world.getSky((chunk.x * 16) + x, y, (chunk.z * 16) + z), chunk.world.getLight((chunk.x * 16) + x, y, (chunk.z * 16) + z)) + 1);

                    Block block = chunk.world.getBlock((chunk.x * 16) + x, y, (chunk.z * 16) + z);
                    if (block.solid || block == self) {
                        val += (13/17) * (1.0 - val);
                    }
                    count += min(1.0, val);
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

typedef TextureInfo TextureGetter(BlockFace);

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