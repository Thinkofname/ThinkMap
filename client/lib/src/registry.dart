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

    // Dirt blocks
    {
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
    }

    registerBlock("cobblestone", new Block()..texture = "cobblestone")
        ..legacyId(4)
        ..build();

    // Leaves and saplings
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
    // ===========


    registerBlock("bedrock", new Block()..texture = "bedrock")
        ..legacyId(7)
        ..build();

    // Liquids

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
    registerBlock("flowing_lava", new Block()..texture = "lava_flow"
        ..solid = false)
        ..legacyId(10)
        ..build();
    registerBlock("lava", new Block()..texture = "lava_still"
        ..solid = false)
        ..legacyId(11)
        ..build();

    // Sand blocks
    {
      registerBlock("sand", new Block()..texture = "sand")
          ..legacyId(12)
          ..dataValue(0)
          ..build();
      registerBlock("sand_red", new Block()..texture = "red_sand")
          ..legacyId(12)
          ..dataValue(1)
          ..build();
    }

    registerBlock("gravel", new Block()..texture = "gravel")
        ..legacyId(13)
        ..build();

    // Ores
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

    registerBlock("sponge", new Block()..texture = "sponge")
      ..legacyId(19)..build();
    registerBlock("glass", new Block()..texture = "glass"
      ..solid = false)
      ..legacyId(20)..build();
    registerBlock("lapis_ore", new Block()..texture = "lapis_ore")
      ..legacyId(21)..build();
    registerBlock("lapis_block", new Block()..texture = "lapis_block")
      ..legacyId(21)..build();

    // Rotatable blocks
    {
      Model ladder = new Model()..faces.add(new ModelFace(BlockFace.FRONT)..moveZ(1)
        ..texture = "ladder");
      int i = 0;
      ({
        "north": 2,
        "south": 0,
        "west": 1,
        "east": 3
      }).forEach((k, v) {
        registerBlock("wall_sign_$k", new Block()
          ..collidable = false
          ..solid = false
          ..texture = "planks_oak"
          ..model = (BlockWallSign.model.clone()..rotateY(v * 90)))
          ..legacyId(68)
          ..dataValue(2 + i)
          ..build();
        registerBlock("ladder_$k", new Block()
          ..collidable = false
          ..solid = false
          ..model = (ladder.clone()..rotateY(v * 90)))
          ..legacyId(65)
          ..dataValue(2 + i)
          ..build();
        registerBlock("dispenser_$k", new BlockSidedTextures()
          ..textures = {
            BlockFace.TOP: "furnace_top",
            BlockFace.BOTTOM: "furnace_top",
            BlockFace.LEFT: v == 3 ? "dispenser_front_horizontal" : "furnace_side",
            BlockFace.RIGHT: v == 1 ? "dispenser_front_horizontal" : "furnace_side",
            BlockFace.FRONT: v == 0 ? "dispenser_front_horizontal" : "furnace_side",
            BlockFace.BACK: v == 2 ? "dispenser_front_horizontal" : "furnace_side"
          })
          ..legacyId(23)
          ..dataValue(2 + i)
          ..build();
        registerBlock("dropper_$k", new BlockSidedTextures()
          ..textures = {
            BlockFace.TOP: "furnace_top",
            BlockFace.BOTTOM: "furnace_top",
            BlockFace.LEFT: v == 3 ? "dropper_front_horizontal" : "furnace_side",
            BlockFace.RIGHT: v == 1 ? "dropper_front_horizontal" : "furnace_side",
            BlockFace.FRONT: v == 0 ? "dropper_front_horizontal" : "furnace_side",
            BlockFace.BACK: v == 2 ? "dropper_front_horizontal" : "furnace_side"
          })
          ..legacyId(158)
          ..dataValue(2 + i)
          ..build();
        registerBlock("furnace_$k", new BlockSidedTextures()
          ..textures = {
            BlockFace.TOP: "furnace_top",
            BlockFace.BOTTOM: "furnace_top",
            BlockFace.LEFT: v == 3 ? "furnace_front_off" : "furnace_side",
            BlockFace.RIGHT: v == 1 ? "furnace_front_off" : "furnace_side",
            BlockFace.FRONT: v == 0 ? "furnace_front_off" : "furnace_side",
            BlockFace.BACK: v == 2 ? "furnace_front_off" : "furnace_side"
          })
          ..legacyId(61)
          ..dataValue(2 + i)
          ..build();
        registerBlock("furnace_lit_$k", new BlockSidedTextures()
          ..textures = {
            BlockFace.TOP: "furnace_top",
            BlockFace.BOTTOM: "furnace_top",
            BlockFace.LEFT: v == 3 ? "furnace_front_on" : "furnace_side",
            BlockFace.RIGHT: v == 1 ? "furnace_front_on" : "furnace_side",
            BlockFace.FRONT: v == 0 ? "furnace_front_on" : "furnace_side",
            BlockFace.BACK: v == 2 ? "furnace_front_on" : "furnace_side"
          })
          ..legacyId(62)
          ..dataValue(2 + i)
          ..build();
        registerBlock("chest_$k", new Block()
          ..solid = false
          ..model = (BlockChest.model.clone()..rotateY(v * 90)))
          ..legacyId(54)
          ..dataValue(2 + i)
          ..build();
        registerBlock("hopper_$k", new Block()
          ..solid = false
          ..model = (BlockHopper.model.clone()..join(BlockHopper.spout, 6, 4, 12)..rotateY(v * 90)))
          ..legacyId(154)
          ..dataValue(2 + i)
          ..build();
        i++;
      });
    }

    // Up/Down blocks
    {
      ({
        "up": 1,
        "down": 0
      }).forEach((k, v) {
        registerBlock("dispenser_$k", new BlockSidedTextures()
          ..textures = {
          BlockFace.TOP: v == 1 ? "dispenser_front_vertical" : "furnace_top",
          BlockFace.BOTTOM: v == 0 ? "dispenser_front_vertical" : "furnace_top",
          BlockFace.LEFT: "furnace_top",
          BlockFace.RIGHT: "furnace_top",
          BlockFace.FRONT: "furnace_top",
          BlockFace.BACK: "furnace_top"
        })
          ..legacyId(23)
          ..dataValue(v)
          ..build();
        registerBlock("dropper_$k", new BlockSidedTextures()
          ..textures = {
          BlockFace.TOP: v == 1 ? "dropper_front_vertical" : "furnace_top",
          BlockFace.BOTTOM: v == 0 ? "dropper_front_vertical" : "furnace_top",
          BlockFace.LEFT: "furnace_top",
          BlockFace.RIGHT: "furnace_top",
          BlockFace.FRONT: "furnace_top",
          BlockFace.BACK: "furnace_top"
        })
          ..legacyId(158)
          ..dataValue(v)
          ..build();

        registerBlock("hopper_$k", new Block()
          ..solid = false
          ..model = (BlockHopper.model.clone()..join(BlockHopper.spout, 6, 0, 6)..rotateY(v * 90)))
          ..legacyId(154)
          ..dataValue(v)
          ..build();
      });
    }

    // Sandstone
    ({
      "normal": 0,
      "carved": 1,
      "smooth": 2
    }).forEach((k, v) {
      registerBlock("sandstone_$k", new BlockSidedTextures()
        ..textures = {
          BlockFace.TOP: "sandstone_top",
          BlockFace.BOTTOM: "sandstone_bottom",
          BlockFace.LEFT: "sandstone_$k",
        BlockFace.RIGHT: "sandstone_$k",
        BlockFace.FRONT: "sandstone_$k",
        BlockFace.BACK: "sandstone_$k"
        })
        ..legacyId(24)
        ..dataValue(v)
        ..build();
    });

    registerBlock("noteblock", new Block()..texture = "noteblock")
      ..legacyId(25)
      ..build();

    //TODO: (#26) Bed
    //TODO: (#27) Golden rail
    //TODO: (#28) Detector rail
    //TODO: (#29) Sticky piston

    registerBlock("web", new BlockCross()..texture = "web"
      ..solid = false
      ..collidable = false)
      ..legacyId(30)
      ..build();

    ({
      "tallgrass": 1,
      "fern": 2,
      "deadbush": 3
    }).forEach((k, v) {
      registerBlock("tallgrass_$k", new BlockCross()..texture = k
        ..solid = false
        ..collidable = false
        ..forceColour = true
        ..colour = 0xA7D389)
        ..legacyId(31)
        ..dataValue(v)
        ..build();
    });
    registerBlock("tallgrass_shrub", new BlockCross()..texture = "deadbush"
      ..solid = false
      ..collidable = false)
      ..legacyId(31)
      ..dataValue(0)
      ..build();

    registerBlock("deadbush", new BlockCross()..texture = "deadbush"
      ..solid = false
      ..collidable = false)
      ..legacyId(32)
      ..dataValue(0)
      ..build();

    //TODO: (#33) Piston
    //TODO: (#34) Piston head

    // Wool
    ({
      "white": 0,
      "orange": 1,
      "magenta": 2,
      "light_blue": 3,
      "yellow": 4,
      "lime": 5,
      "pink": 6,
      "gray": 7,
      "silver": 8,
      "cyan": 9,
      "purple": 10,
      "blue": 11,
      "brown": 12,
      "green": 13,
      "red": 14,
      "black": 15
    }).forEach((k, v) {
      registerBlock("wool_$k", new Block()..texture = "wool_colored_$k")
        ..legacyId(35)
        ..dataValue(v)
        ..build();
    });

    //TODO: (#36) Piston extension

    registerBlock("yellow_flower", new BlockCross()..texture = "dandelion"
      ..solid = false
      ..collidable = false)
      ..legacyId(37)
      ..build();

    ({
      "rose": 0,
      "blue_orchid": 1,
      "allium": 2,
      "houstonia": 3,
      "tulip_red": 4,
      "tulip_orange": 5,
      "tulip_white": 6,
      "tulip_pink": 7,
      "oxeye_daisy": 8
    }).forEach((k, v) {
      registerBlock("red_flower_$k", new BlockCross()..texture = "flower_$k"
        ..solid = false
        ..collidable = false)
        ..legacyId(38)
        ..dataValue(v)
        ..build();
    });

    registerBlock("brown_mushroom", new BlockCross()..texture = "mushroom_brown"
      ..solid = false
      ..collidable = false)
      ..legacyId(39)
      ..build();
    registerBlock("red_mushroom", new BlockCross()..texture = "mushroom_red"
      ..solid = false
      ..collidable = false)
      ..legacyId(40)
      ..build();
    registerBlock("gold_block", new Block()..texture = "gold_block")
      ..legacyId(41)
      ..build();
    registerBlock("iron_block", new Block()..texture = "iron_block")
      ..legacyId(42)
      ..build();

    //TODO: (#43) Double stone slab
    //TODO: (#44) Stone slab

    registerBlock("brick_block", new Block()..texture = "brick")
      ..legacyId(45)
      ..build();
    registerBlock("tnt", new BlockSidedTextures()
      ..textures = {
      BlockFace.TOP: "tnt_top",
      BlockFace.BOTTOM : "tnt_bottom",
      BlockFace.LEFT: "tnt_side",
      BlockFace.RIGHT: "tnt_side",
      BlockFace.FRONT: "tnt_side",
      BlockFace.BACK: "tnt_side",
    })
      ..legacyId(46)
      ..build();
    registerBlock("bookshelf", new BlockSidedTextures()
      ..textures = {
        BlockFace.TOP: "planks_oak",
        BlockFace.BOTTOM: "planks_oak",
        BlockFace.LEFT: "bookshelf",
        BlockFace.RIGHT: "bookshelf",
        BlockFace.FRONT: "bookshelf",
        BlockFace.BACK: "bookshelf"
      })
      ..legacyId(47)
      ..build();
    registerBlock("mossy_cobblestone", new Block()..texture = "cobblestone_mossy")
      ..legacyId(48)
      ..build();
    registerBlock("obsidian", new Block()..texture = "obsidian")
      ..legacyId(49)
      ..build();

    //TODO: (#50) Torch
    //TODO: (#51) Fire

    registerBlock("mob_spawner", new Block()..texture = "mob_spawner"
      ..solid = false
      ..allowSelf = true)
      ..legacyId(52)
      ..build();

    //TODO: (#53) Stairs

    //TODO: (#55) Redstone wire

    registerBlock("diamond_ore", new Block()..texture = "diamond_ore")
      ..legacyId(56)
      ..build();
    registerBlock("diamond_block", new Block()..texture = "diamond_block")
      ..legacyId(57)
      ..build();
    registerBlock("crafting_table", new BlockSidedTextures()
      ..textures = {
        BlockFace.TOP: "crafting_table_top",
        BlockFace.BOTTOM: "planks_oak",
        BlockFace.LEFT: "crafting_table_side",
        BlockFace.RIGHT: "crafting_table_side",
        BlockFace.FRONT: "crafting_table_front",
        BlockFace.BACK: "crafting_table_front",
    })
      ..legacyId(58)
      ..build();

    //TODO: (#59) Wheat
    //TODO: (#60) Farmland

    //TODO: (#63) Standing sign
    //TODO: (#64) Wooden door

    //TODO: (#66) Rail
    //TODO: (#67) Stone stairs

    //TODO: (#69) Lever
    //TODO: (#70) Stone pressure plate
    //TODO: (#71) Iron door
    //TODO: (#72) Wooden pressure plate

    registerBlock("redstone_ore", new Block()..texture = "redstone_ore")
      ..legacyId(73)
      ..build();
    registerBlock("lit_redstone_ore", new Block()..texture = "redstone_ore")
      ..legacyId(74)
      ..build();

    //TODO: (#75) Unlit redstone torch
    //TODO: (#76) Redstone torch
    //TODO: (#77) Stone button
    //TODO: (#78) Snow layer

    registerBlock("ice", new Block()..texture = "ice"
      ..solid = false
      ..transparent = true)
      ..legacyId(79)
      ..build();
    registerBlock("snow", new Block()..texture = "snow")
      ..legacyId(80)
      ..build();

    //TODO: (#81) Cactus

    registerBlock("clay", new Block()..texture = "clay")
      ..legacyId(82)
      ..build();
    registerBlock("reeds", new BlockCross()..texture = "reeds"
      ..solid = false)
      ..legacyId(83)
      ..build();
    registerBlock("jukebox", new BlockSidedTextures()
      ..textures = {
      BlockFace.TOP: "jukebox_top",
      BlockFace.BOTTOM: "jukebox_side",
      BlockFace.LEFT: "jukebox_side",
      BlockFace.RIGHT: "jukebox_side",
      BlockFace.BACK: "jukebox_side",
      BlockFace.FRONT: "jukebox_side"
    })
      ..legacyId(84)
      ..build();

    //TODO: (#85) Fence
    //TODO: (#86) Pumpkin

    registerBlock("netherrack", new Block()..texture = "netherrack")
      ..legacyId(87)
      ..build();
    registerBlock("soulsand", new Block()..texture = "soul_sand")
      ..legacyId(88)
      ..build();
    registerBlock("glowstone", new Block()..texture = "glowstone")
      ..legacyId(89)
      ..build();

    //TODO: (#90) Portal
    //TODO: (#91) Lit pumpkin
    //TODO: (#92) Cake
    //TODO: (#93) Unpowered repeater
    //TODO: (#94) Powered repeater
    //TODO: (#95) Stained glass
    //TODO: (#96) Trapdoor
    //TODO: (#97) Monster egg

    // Stone bricks
    {
      registerBlock("stonebrick", new Block()..texture = "stonebrick")
        ..legacyId(98)
        ..dataValue(0)
        ..build();
      registerBlock("stonebrick_mossy", new Block()..texture = "stonebrick_mossy")
        ..legacyId(98)
        ..dataValue(1)
        ..build();
      registerBlock("stonebrick_cracked", new Block()..texture = "stonebrick_cracked")
        ..legacyId(98)
        ..dataValue(2)
        ..build();
      registerBlock("stonebrick_carved", new Block()..texture = "stonebrick_carved")
        ..legacyId(98)
        ..dataValue(3)
        ..build();
    }

    //TODO: (#99) Brown mushroom block
    //TODO: (#100) Red mushroom block
    //TODO: (#101) Iron bars
    //TODO: (#102) Glass pane
    //TODO: (#103) Melon block
    //TODO: (#104) Pumpkin stem
    //TODO: (#105) Melon stem

    BlockVines.register();

    //TODO: (#107) Fence gate
    //TODO: (#108) Brick stairs
    //TODO: (#109) Stone brick stairs
    //TODO: (#110) Mycelium
    //TODO: (#111) Waterlily
    //TODO: (#112) Nether brick
    //TODO: (#113) Nether brick fence
    //TODO: (#114) Nether brick stairs
    //TODO: (#115) Nether wart
    //TODO: (#116) Enchanting table
    //TODO: (#117) Brewing stand
    //TODO: (#118) Cauldron
    //TODO: (#119) End portal
    //TODO: (#120) End portal frame
    //TODO: (#121) End stone
    //TODO: (#122) Dragon egg
    //TODO: (#123) Redstone lamp
    //TODO: (#124) Lit redstone lamp
    //TODO: (#125) Double wooden slab
    //TODO: (#126) Wooden slab
    //TODO: (#127) Cocoa
    //TODO: (#128) Sandstone stairs
    //TODO: (#129) Emerald ore
    //TODO: (#130) Ender chest
    //TODO: (#131) Tripwire hook
    //TODO: (#132) Tripwire
    //TODO: (#133) Emerald block
    //TODO: (#134) Spruce stairs
    //TODO: (#135) Birch stairs
    //TODO: (#136) Jungle stairs
    //TODO: (#137) Command block
    //TODO: (#138) Beacon
    //TODO: (#139) Cobblestone wall
    //TODO: (#140) Flower pot
    //TODO: (#141) Carrots
    //TODO: (#142) Potatoes
    //TODO: (#143) Wooden button
    //TODO: (#144) Skull

    // Anvils
    for (int deg in [0, 1, 2, 3]) {
      for (int dam in [0, 1, 2]) {
        registerBlock("anvil_${deg}_$dam", new Block()..solid = false
          ..model = (models["anvil"].clone((t) => t == "anvil_top_damaged_0" ? "anvil_top_damaged_$dam" : t)..rotateY(deg*90)))
          ..legacyId(145)
          ..dataValue(dam << 2 | (deg))
          ..build();
      }
    }

    //TODO: (#146) Trapped chest
    //TODO: (#147) Light weighted pressure plate
    //TODO: (#148) Heavy weighted pressure plate
    //TODO: (#149) Unpowered comparator
    //TODO: (#150) Powered comparator
    //TODO: (#151) Daylight detector
    //TODO: (#152) Redstone block
    //TODO: (#153) Quartz ore

    //TODO: (#155) Quartz block
    //TODO: (#156) Quartz stairs
    //TODO: (#157) Activator rail

    //TODO: (#159) Stained hardened clay
    //TODO: (#160) Stained glass pane
    //TODO: (#161) Leaves2
    //TODO: (#162) Log2
    //TODO: (#163) Acacia stairs
    //TODO: (#164) Dark oak stairs

    // Range (#165) -> (#169) unused currently

    //TODO: (#170) Hay block
    //TODO: (#171) Carpet
    //TODO: (#172) Hardened clay
    //TODO: (#173) Coal block
    //TODO: (#174) Packed ice
    //TODO: (#175) Double plant

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
