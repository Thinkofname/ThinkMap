part of mapViewer;

class Block {

  static final Block AIR = new Block._airBlock();
  static final Block STONE = new Block._internal(1, "minecraft:stone", 0x6D6D6D);
  static final Block GRASS = new Block._internal(2, "minecraft:grass", 0x609252);
  static final Block DIRT = new Block._internal(3, "minecraft:dirt", 0x715036);
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

  static Block blockFromName(String name) {
    return _blocks[name];
  }

  ///**Warning:** this will be dropped in future versions of Minecraft
  static Block blockFromLegacyId(int id) {
    return _blocksLegacy[id];
  }

  ///**Warning:** this will be dropped in future versions of Minecraft
  int legacyId;
  String name;

  bool renderable = true;
  bool solid = true;
  int _colour = 0xFFFFFF;

  Block._internal(int _legacyId, String _name, int _colour) {
    legacyId = _legacyId;
    name = _name;
    colour = _colour;
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
    int r = (colour >> 16) & 0xFF;
    int g = (colour >> 8) & 0xFF;
    int b = colour & 0xFF;

    if (y + 1 <= 255 && !chunk.getBlock(x, y + 1, z).solid) {
      double topRight = 1.0 - (_numBlocksRegion(chunk, x - 1, y + 1, z - 1, x + 1, y + 2, z + 1) / 8);
      double topLeft = 1.0 - (_numBlocksRegion(chunk, x, y + 1, z - 1, x + 2, y + 2, z + 1) / 8);
      double bottomLeft = 1.0 - (_numBlocksRegion(chunk, x, y + 1, z, x + 2, y + 2, z + 2) / 8);
      double bottomRight = 1.0 - (_numBlocksRegion(chunk, x - 1, y + 1, z, x + 1, y + 2, z + 2) / 8);

      builder
        ..position(x, y + 1, z)
        ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
        ..position(x + 1, y + 1, z)
        ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
        ..position(x, y + 1, z + 1)
        ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
        ..position(x + 1, y + 1, z)
        ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
        ..position(x + 1, y + 1, z + 1)
        ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
        ..position(x, y + 1, z + 1)
        ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor());
    }

    //TODO: Bottom side

    if (x + 1 <= 15 && !chunk.getBlock(x + 1, y, z).solid) {
      double topRight = 1.0 - (_numBlocksRegion(chunk, x + 1, y, z, x + 2, y + 2, z + 2) / 8);
      double topLeft = 1.0 - (_numBlocksRegion(chunk, x + 1, y, z - 1, x + 2, y + 2, z + 1) / 8);
      double bottomLeft = 1.0 - (_numBlocksRegion(chunk, x + 1, y - 1, z - 1, x + 2, y + 1, z + 1) / 8);
      double bottomRight = 1.0 - (_numBlocksRegion(chunk, x + 1, y - 1, z, x + 2, y + 1, z + 2) / 8);
      builder
        ..position(x + 1, y, z)
        ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
        ..position(x + 1, y, z + 1)
        ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
        ..position(x + 1, y + 1, z)
        ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
        ..position(x + 1, y + 1, z + 1)
        ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
        ..position(x + 1, y + 1, z)
        ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
        ..position(x + 1, y, z + 1)
        ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor());
    }

    if (x - 1 >= 0 && !chunk.getBlock(x - 1, y, z).solid) {
      double topRight = 1.0 - (_numBlocksRegion(chunk, x - 2, y, z - 1, x - 1, y + 2, z + 1) / 8);
      double topLeft = 1.0 - (_numBlocksRegion(chunk, x - 2, y, z, x - 1, y + 2, z + 2) / 8);
      double bottomLeft = 1.0 - (_numBlocksRegion(chunk, x - 2, y - 1, z, x - 1, y + 1, z + 2) / 8);
      double bottomRight = 1.0 - (_numBlocksRegion(chunk, x - 2, y - 1, z - 1, x - 1, y + 1, z + 1) / 8);
      builder
        ..position(x, y, z)
        ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
        ..position(x, y + 1, z)
        ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
        ..position(x, y, z + 1)
        ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
        ..position(x, y + 1, z)
        ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
        ..position(x, y + 1, z + 1)
        ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
        ..position(x, y, z + 1)
        ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor());
    }

    if (z + 1 <= 15 && !chunk.getBlock(x, y, z + 1).solid) {
      double topRight = 1.0 - (_numBlocksRegion(chunk, x - 1, y, z + 1, x + 1, y + 2, z + 2) / 8);
      double topLeft = 1.0 - (_numBlocksRegion(chunk, x, y, z + 1, x + 2, y + 2, z + 2) / 8);
      double bottomLeft = 1.0 - (_numBlocksRegion(chunk, x, y - 1, z + 1, x + 2, y + 1, z + 2) / 8);
      double bottomRight = 1.0 - (_numBlocksRegion(chunk, x - 1, y - 1, z + 1, x + 1, y + 1, z + 2) / 8);
      builder
        ..position(x, y, z + 1)
        ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
        ..position(x, y + 1, z + 1)
        ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
        ..position(x + 1, y, z + 1)
        ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
        ..position(x, y + 1, z + 1)
        ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
        ..position(x + 1, y + 1, z + 1)
        ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
        ..position(x + 1, y, z + 1)
        ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor());
    }

    if (z - 1 >= 0 && !chunk.getBlock(x, y, z - 1).solid) {
      double topRight = 1.0 - (_numBlocksRegion(chunk, x, y, z - 1, x + 2, y + 2, z) / 8);
      double topLeft = 1.0 - (_numBlocksRegion(chunk, x - 1, y, z - 1, x + 1, y + 2, z) / 8);
      double bottomLeft = 1.0 - (_numBlocksRegion(chunk, x - 1, y - 1, z - 1, x + 1, y + 1, z) / 8);
      double bottomRight = 1.0 - (_numBlocksRegion(chunk, x, y - 1, z - 1, x + 2, y + 1, z) / 8);
      builder
        ..position(x, y, z)
        ..colour((r * bottomLeft).floor(), (g * bottomLeft).floor(), (b * bottomLeft).floor())
        ..position(x + 1, y, z)
        ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor())
        ..position(x, y + 1, z)
        ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
        ..position(x + 1, y + 1, z)
        ..colour((r * topRight).floor(), (g * topRight).floor(), (b * topRight).floor())
        ..position(x, y + 1, z)
        ..colour((r * topLeft).floor(), (g * topLeft).floor(), (b * topLeft).floor())
        ..position(x + 1, y, z)
        ..colour((r * bottomRight).floor(), (g * bottomRight).floor(), (b * bottomRight).floor());
    }
  }

  static int _numBlocksRegion(Chunk chunk, int x1, int y1, int z1, int x2, int y2, int z2) {
    int count = 0;
    for (int x = x1; x < x2; x++) {
      for (int y = y1; y < y2; y++) {
        for (int z = z1; z < z2; z++) {
          if (!(x < 0 || x > 15 || y < 0 || y > 255 || z < 0 || z > 15)) {
            if (chunk.getBlock(x, y, z).solid) {
              count++;
            }
          }
        }
      }
    }
    return count;
  }

  set colour(int c) {
    _colour = c;
  }

  int get colour {
    return _colour;
  }
}