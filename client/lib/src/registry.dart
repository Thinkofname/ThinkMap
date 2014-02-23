part of map_viewer;

/**
 * Contains static references to blocks needed by the map viewer
 */
class Blocks {

  /// Air - Pretty much what you would expect it to be
  static final Block AIR = BlockRegistry.getByName("air");
  /// Bedrock - Unbreakable block (unless Mojang messes up... again)
  static final Block BEDROCK = BlockRegistry.getByName("bedrock");
  /// Water - Blue swimmable stuff, strangely not drinkable
  static final Block WATER = BlockRegistry.getByName("water");
  /// Flowing Water - Blue swimmable stuff that is flowing in a direction
  static final Block FLOWING_WATER = BlockRegistry.getByName("flowing_water");

  /**
   * Missing Block - If you see this block I messed up somewhere (or you have some sort of
   * mod on the server)
   */
  static final Block MISSING_BLOCK = BlockRegistry.getByName(
      "webglmap:missing_block");
  /// Null Block - Solid air
  static final Block NULL_BLOCK = BlockRegistry.getByName("webglmap:null");
}

/**
 * Access to every block that the map viewer knows about
 */
class BlockRegistry {

  /// Whether the blocks have been registered yet
  static bool _hasInit = false;
  /// A map of plugin to blocks names to blocks
  static Map<String, Map<String, BlockRegistrationEntry>> _blocks = new Map();

  static final Logger logger = new Logger("BlockRegistry");

  /**
     * Returns a block by its [name].
     *
     * The format of the name is 'plugin:block'. If the format
     * 'block' is used then the plugin is assumed to be 'minecraft'.
     *
     * If the block doesn't exist then the block 'webglmap:missing_block'
     * is returned
     */
  static Block getByName(String name) {
    if (!_hasInit) init();
    String plugin = "minecraft";
    if (name.contains(":")) { // Check for plugin name
      plugin = name.substring(0, name.indexOf(":"));
      name = name.substring(name.indexOf(":") + 1);
    }
    var ret = _blocks[plugin][name].block;
    if (ret == null) ret = Blocks.MISSING_BLOCK;
    return ret;
  }

  // A map (list) of legacy block ids to new blocks
  @Deprecated("Will be removed once minecraft drops it")
  static List<_BlockEntry> _legacyMap = new List(0xFF);

  /**
     * Using [id] and [data] from legacy block id system
     * this will look up the block and return the new system
     * version of the block.
     *
     * If the block doesn't exist then the block 'webglmap:missing_block'
     */
  @Deprecated("Will be removed once minecraft drops it")
  static Block getByLegacy(int id, int data) {
    if (id == 0) return Blocks.AIR;
    var val = _legacyMap[id];
    if (val == null) return Blocks.MISSING_BLOCK;
    var reg = val.getBlock(data);
    if (reg == null) return Blocks.MISSING_BLOCK;
    return reg.block;
  }

  /**
     * Registers the block using the [name] and [plugin] (which defaults
     * to 'minecraft').
     *
     * This returns a builder BlockRegistrationEntry which can be used to
     * set properties before committing to the registry with 'build'
     */
  static BlockRegistrationEntry registerBlock(String name, Block block, {String
      plugin: "minecraft"}) {
    if (!_blocks.containsKey(plugin)) {
      // New plugin set up the map
      _blocks[plugin] = new Map();
    }
    if (_blocks[plugin].containsKey(name)) {
      logger.error("Tried to double register block $name");
      throw "";
    }
    var reg = new BlockRegistrationEntry(plugin, name, block);
    block._regBlock = reg;
    _blocks[plugin][name] = reg;
    logger.info("Registed block: $reg");
    return reg;
  }

  /**
     * Register all blocks
     */
  static void init() {
    if (_hasInit) return; // Prevent double init
    _hasInit = true;

    Stopwatch initTimer = new Stopwatch()..start();

    // Vanilla blocks
    registerBlock("air", new Block()
        ..renderable = false
        ..solid = false
        ..collidable = false)
        ..legacyId(0)
        ..build();
    registerBlock("stone", new Block()..texture = "stone")
        ..legacyId(1)
        ..build();
    registerBlock("grass", new BlockGrass()..colour = 0xA7D389)
        ..legacyId(2)
        ..build();

    registerBlock("dirt", new Block()..texture = "dirt")
        ..legacyId(3)
        ..dataValue(0)
        ..build();
    registerBlock("dirt_grassless", new Block()..texture = "dirt")
        ..legacyId(3)
        ..dataValue(1)
        ..build();
    registerBlock("dirt_podzol", new BlockSidedTextures()..textures = {
          BlockFace.FRONT: "dirt_podzol_side",
          BlockFace.TOP: "dirt_podzol_top",
          BlockFace.BOTTOM: "dirt",
          BlockFace.BACK: "dirt_podzol_side",
          BlockFace.LEFT: "dirt_podzol_side",
          BlockFace.RIGHT: "dirt_podzol_side"
        })
        ..legacyId(3)
        ..dataValue(2)
        ..build();

    registerBlock("cobblestone", new Block()..texture = "cobblestone")
        ..legacyId(4)
        ..build();

    ({
      "oak": 0,
      "spruce": 1,
      "birch": 2,
      "jungle": 3,
      "acacia": 4
    }).forEach((k, v) {
      registerBlock("planks_$k", new Block()..texture = "planks_$k")
          ..legacyId(5)
          ..dataValue(v)
          ..build();
      registerBlock("sapling_$k", new BlockCross()
          ..texture = "sapling_$k"
          ..collidable = false
          ..solid = false)
          ..legacyId(6)
          ..dataValue(v)
          ..build();
      registerBlock("sapling_${k}_ticked", new BlockCross()
        ..texture = "sapling_$k"
        ..collidable = false
        ..solid = false)
        ..legacyId(6)
        ..dataValue(v | 8)
        ..build();
      registerBlock("leaves_$k", new Block()
          ..texture = "leaves_$k"
          ..solid = false
          ..allowSelf = true
          ..colour = 0xA7D389
          ..forceColour = true)
          ..legacyId(18)
          ..dataValue(v)
          ..build();
    });
    registerBlock("planks_big_oak", new Block()..texture = "planks_big_oak")
        ..legacyId(5)
        ..dataValue(5)
        ..build();
    registerBlock("sapling_roofed_oak", new BlockCross()
        ..texture = "sapling_roofed_oak"
        ..collidable = false
        ..solid = false)
        ..legacyId(6)
        ..dataValue(5)
        ..build();
    registerBlock("sapling_roofed_oak_ticked", new BlockCross()
      ..texture = "sapling_roofed_oak"
      ..collidable = false
      ..solid = false)
      ..legacyId(6)
      ..dataValue(5 | 8)
      ..build();
    registerBlock("leaves_big_oak", new Block()
        ..texture = "leaves_big_oak"
        ..solid = false
        ..allowSelf = true
        ..colour = 0xA7D389
        ..forceColour = true)
        ..legacyId(18)
        ..dataValue(5)
        ..build();

    registerBlock("bedrock", new Block()..texture = "bedrock")
        ..legacyId(7)
        ..build();

    registerBlock("flowing_water", new BlockWater()
        ..texture = "water_flow"
        ..solid = false
        ..transparent = true)
        ..legacyId(8)
        ..build();
    registerBlock("water", new BlockWater()
        ..texture = "water_still"
        ..solid = false
        ..transparent = true)
        ..legacyId(9)
        ..build();
    registerBlock("flowing_lava", new Block()..texture = "lava_flow")
        ..legacyId(10)
        ..build();
    registerBlock("lava", new Block()..texture = "lava_still")
        ..legacyId(11)
        ..build();

    registerBlock("sand", new Block()..texture = "sand")
        ..legacyId(12)
        ..dataValue(0)
        ..build();
    registerBlock("sand_red", new Block()..texture = "red_sand")
        ..legacyId(12)
        ..dataValue(1)
        ..build();

    registerBlock("gravel", new Block()..texture = "gravel")
        ..legacyId(13)
        ..build();
    registerBlock("gold_ore", new Block()..texture = "gold_ore")
        ..legacyId(14)
        ..build();
    registerBlock("iron_ore", new Block()..texture = "iron_ore")
        ..legacyId(15)
        ..build();
    registerBlock("coal_ore", new Block()..texture = "coal_ore")
        ..legacyId(16)
        ..build();

    // Logs
    ({
      "oak": 0,
      "spruce": 1,
      "birch": 2,
      "jungle": 3
    }).forEach((k, v) {
      registerBlock("log_${k}_up", new BlockSidedTextures()..textures = {
            BlockFace.TOP: "log_${k}_top",
            BlockFace.BOTTOM: "log_${k}_top",
            BlockFace.FRONT: "log_$k",
            BlockFace.BACK: "log_$k",
            BlockFace.LEFT: "log_$k",
            BlockFace.RIGHT: "log_$k"
          })
          ..legacyId(17)
          ..dataValue(v)
          ..build();
      registerBlock("log_${k}_east", new BlockSidedTextures()..textures = {
            BlockFace.TOP: "log_$k",
            BlockFace.BOTTOM: "log_$k",
            BlockFace.FRONT: "log_$k",
            BlockFace.BACK: "log_$k",
            BlockFace.LEFT: "log_${k}_top",
            BlockFace.RIGHT: "log_${k}_top"
          })
          ..legacyId(17)
          ..dataValue(4 + v)
          ..build();
      registerBlock("log_${k}_north", new BlockSidedTextures()..textures = {
            BlockFace.TOP: "log_$k",
            BlockFace.BOTTOM: "log_$k",
            BlockFace.FRONT: "log_${k}_top",
            BlockFace.BACK: "log_${k}_top",
            BlockFace.LEFT: "log_$k",
            BlockFace.RIGHT: "log_$k"
          })
          ..legacyId(17)
          ..dataValue(8 + v)
          ..build();
      registerBlock("log_${k}_all", new BlockSidedTextures()..textures = {
            BlockFace.TOP: "log_$k",
            BlockFace.BOTTOM: "log_$k",
            BlockFace.FRONT: "log_$k",
            BlockFace.BACK: "log_$k",
            BlockFace.LEFT: "log_$k",
            BlockFace.RIGHT: "log_$k"
          })
          ..legacyId(17)
          ..dataValue(12 + v)
          ..build();
    });

    registerBlock("glowstone", new Block()..texture = "glowstone")
        ..legacyId(89)
        ..build();

    for (int deg in [0, 1, 2, 3]) {
      for (int dam in [0, 1, 2]) {
        registerBlock("anvil_${deg}_$dam", new Block()..solid = false
            ..model = models["anvil"].rotate(deg*90).clone((t) => t == "anvil_top_damaged_0" ? "anvil_top_damaged_$dam" : t))
            ..legacyId(145)
            ..dataValue(dam << 2 | (deg))
            ..build();
      }
    }

    BlockWallSign.register();
    BlockVines.register();

    // Custom Blocks
    registerBlock("missing_block", new Block()..texture = "missing_texture",
        plugin: "webglmap")..build();
    registerBlock("null", new Block()
        ..renderable = false
        ..shade = false, plugin: "webglmap")..build();

    initTimer.stop();
    logger.info("Registered blocks in ${initTimer.elapsedMilliseconds}ms");
  }
}

/**
 * A builder used to set up a block registration
 */
class BlockRegistrationEntry {

  /// The plugin that owns this block
  String plugin;

  /// The name of the block
  String name;

  /// The block this entry is form
  Block block;

  @Deprecated("Will be removed once minecraft drops it")
  int _legacyId = -1;
  @Deprecated("Will be removed once minecraft drops it")
  bool _allDataValues = true;
  @Deprecated("Will be removed once minecraft drops it")
  int _dataValue;

  /// Create a new block registry entry
  BlockRegistrationEntry(this.plugin, this.name, this.block);

  @override
  String toString() {
    return "$plugin:$name";
  }


  //. Prevent Changing the values after build
  bool _hasBuild = false;

  /**
     * Should be called once all properties are set to their
     * desired values
     */
  void build() {
    _hasBuild = true;


    // Handle legacy stuff
    // TODO: Remove once minecraft drops it
    if (_legacyId == -1) return; // Doesn't exist in the old system
    if (_allDataValues) {
      if (plugin != "minecraft") BlockRegistry.logger.warn("$this is using legacy block ids ($_legacyId)");
      // Data values don't matter for this block
      BlockRegistry._legacyMap[_legacyId] = new _SingleBlockEntry(this);
    } else {
      if (plugin != "minecraft") BlockRegistry.logger.warn("$this is using legacy block ids ($_legacyId:$_dataValue)");
      if (BlockRegistry._legacyMap[_legacyId] == null) {
        BlockRegistry._legacyMap[_legacyId] = new _MultiBlockEntry();
      }
      (BlockRegistry._legacyMap[_legacyId] as
          _MultiBlockEntry).byData[_dataValue] = this;
    }
  }

  /**
    * Called by builder methods to prevent changing properties after
   * building
    */
  void _checkBuild() {
    if (_hasBuild) {
      throw "Cannot change properties of a RegBlock after build";
    }
  }

  /**
   * Sets the legacy id of the block entry.
   *
   * This will be used when loading Minecraft maps
   */
  @Deprecated("Will be removed once minecraft drops it")
  void legacyId(int id) {
    _checkBuild();
    _legacyId = id;
  }

  /**
   * Sets the data value of the block entry
   *
   * This will be used when loading Minecraft maps. This ties
   * the block to the specific data value (for its legacy id).
   */
  @Deprecated("Will be removed once minecraft drops it")
  void dataValue(int val) {
    _checkBuild();
    _allDataValues = false;
    _dataValue = val;
  }
}

/**
 * A entry for a block that can convert a Minecraft data value
 * into a registered block
 */
@Deprecated("Will be removed once minecraft drops it")
abstract class _BlockEntry {

  /**
   * Returns a registered block based on [data]
   */
  BlockRegistrationEntry getBlock(int data);
}

/**
 * A BlockEntry that always returns the same block
 */
@Deprecated("Will be removed once minecraft drops it")
class _SingleBlockEntry implements _BlockEntry {

  BlockRegistrationEntry _block;

  _SingleBlockEntry(this._block);

  @override
  BlockRegistrationEntry getBlock(int data) => _block;
}

/**
 * A BlockEntry that will return one of 16 blocks based on
 * the passed data value
 */
@Deprecated("Will be removed once minecraft drops it")
class _MultiBlockEntry implements _BlockEntry {
  List<BlockRegistrationEntry> byData = new List(16);

  @override
  BlockRegistrationEntry getBlock(int data) => byData[data];
}
