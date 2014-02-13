part of mapViewer;

class Blocks {

    static final Block AIR = BlockRegistry.getByName("air");
    static final Block BEDROCK = BlockRegistry.getByName("bedrock");

    static final Block WATER = BlockRegistry.getByName("water");
    static final Block FLOWING_WATER = BlockRegistry.getByName("flowing_water");

    static final Block MISSING_BLOCK = BlockRegistry.getByName("webglmap:missing_block");
    static final Block NULL_BLOCK = BlockRegistry.getByName("webglmap:null");
}

class _BlockEntry {
    BlockRegistrationEntry block;
    _BlockEntry(this.block);

    BlockRegistrationEntry getBlock(int data) => block;
}

class _MultiBlockEntry implements _BlockEntry {
    List<BlockRegistrationEntry> byData = new List(16);

    BlockRegistrationEntry getBlock(int data) => byData[data];
}

class BlockRegistry {

    static bool _hasInit = false;
    static Map<String, Map<String, BlockRegistrationEntry>> _blocks = new Map();

    static Block getByName(String name) {
        if (!_hasInit) init();
        String plugin = "minecraft";
        if (name.contains(":")) {
            plugin = name.substring(0, name.indexOf(":"));
            name = name.substring(name.indexOf(":") + 1);
        }
        var ret = _blocks[plugin][name].block;
        if (ret == null) ret = Blocks.MISSING_BLOCK;
        return ret;
    }

    @Deprecated("Will be removed once minecraft drops it")
    static Map<int, _BlockEntry> _legacyMap = new Map();
    @Deprecated("Will be removed once minecraft drops it")
    static Block getByLegacy(int id, int data) {
        var val = _legacyMap[id];
        if (val == null) return Blocks.MISSING_BLOCK;
        var reg = val.getBlock(data);
        if (reg == null) return Blocks.MISSING_BLOCK;
        return reg.block;
    }

    static BlockRegistrationEntry registerBlock(String name, Block block, {String plugin : "minecraft"}) {
        if (!_blocks.containsKey(plugin)) {
            _blocks[plugin] = new Map();
        }
        if (_blocks[plugin].containsKey(name)) {
            throw "Tried to double register block $name";
        }
        var reg = new BlockRegistrationEntry(plugin, name, block);
        block._regBlock = reg;
        _blocks[plugin][name] = reg;
        print("Registed block: $reg");
        return reg;
    }

    static init() {
        _hasInit = true;

        // Vanilla blocks
        registerBlock("air", new Block()
            ..renderable = false
            ..solid = false
            ..collidable = false)
            ..legacyId(0)
            ..build();
        registerBlock("stone", new Block()
            ..texture = "stone")
            ..legacyId(1)
            ..build();
        registerBlock("grass", new BlockGrass()
            ..colour = 0xA7D389)
            ..legacyId(2)
            ..build();

        registerBlock("dirt", new Block()
            ..texture = "dirt")
            ..legacyId(3)
            ..dataValue(0)
            ..build();
        registerBlock("dirt_grassless", new Block()
            ..texture = "dirt")
            ..legacyId(3)
            ..dataValue(1)
            ..build();
        registerBlock("dirt_podzol", new BlockSidedTextures()
            ..textures = {
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

        registerBlock("cobblestone", new Block()
            ..texture = "cobblestone")
            ..legacyId(4)
            ..build();

        ({"oak":0, "spruce":1, "birch":2, "jungle":3, "acacia":4}).forEach((k, v) {
            registerBlock("planks_$k", new Block()
                ..texture = "planks_$k")
                ..legacyId(5)
                ..dataValue(v)
                ..build();
            registerBlock("sapling_$k", new BlockCross()
                ..texture = "sapling_$k"
                ..collidable = false)
                ..legacyId(6)
                ..dataValue(v)
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
        registerBlock("planks_big_oak", new Block()
            ..texture = "planks_big_oak")
            ..legacyId(5)
            ..dataValue(5)
            ..build();
        registerBlock("sapling_roofed_oak", new BlockCross()
            ..texture = "sapling_roofed_oak"
            ..collidable = false)
            ..legacyId(6)
            ..dataValue(5)
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

        registerBlock("bedrock", new Block()
            ..texture = "bedrock")
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
        registerBlock("flowing_lava", new Block()
            ..texture = "lava_flow")
            ..legacyId(10)
            ..build();
        registerBlock("lava", new Block()
            ..texture = "lava_still")
            ..legacyId(11)
            ..build();

        registerBlock("sand", new Block()
            ..texture = "sand")
            ..legacyId(12)
            ..dataValue(0)
            ..build();
        registerBlock("sand_red", new Block()
            ..texture = "red_sand")
            ..legacyId(12)
            ..dataValue(1)
            ..build();

        registerBlock("gravel", new Block()
            ..texture = "gravel")
            ..legacyId(13)
            ..build();
        registerBlock("gold_ore", new Block()
            ..texture = "gold_ore")
            ..legacyId(14)
            ..build();
        registerBlock("iron_ore", new Block()
            ..texture = "iron_ore")
            ..legacyId(15)
            ..build();
        registerBlock("coal_ore", new Block()
            ..texture = "coal_ore")
            ..legacyId(16)
            ..build();

        // Logs
        ({"oak":0, "spruce":1, "birch":2, "jungle":3}).forEach((k, v) {
            registerBlock("log_${k}_up", new BlockSidedTextures()
                ..textures = {
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
            registerBlock("log_${k}_east", new BlockSidedTextures()
                ..textures = {
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
            registerBlock("log_${k}_north", new BlockSidedTextures()
                ..textures = {
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
            registerBlock("log_${k}_all", new BlockSidedTextures()
                ..textures = {
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

        registerBlock("glowstone", new Block()
            ..texture = "glowstone")
            ..legacyId(89)
            ..build();

        BlockWallSign.register();
        BlockVines.register();

        // Custom Blocks
        registerBlock("missing_block", new Block()
            ..texture = "missing_texture",
            plugin: "webglmap")
            ..build();
        registerBlock("null", new Block()
            ..renderable = false
            ..shade = false,
            plugin: "webglmap")
            ..build();
    }
}

class BlockRegistrationEntry {

    // The plugin that owns this block
    String plugin;

    // The name of the block
    String name;

    // The block this entry is form
    Block block;

    @Deprecated("Will be removed once minecraft drops it")
    int _legacyId = -1;
    @Deprecated("Will be removed once minecraft drops it")
    bool _allDataValues = true;
    @Deprecated("Will be removed once minecraft drops it")
    int _dataValue;

    // Create a new block registry entry

    BlockRegistrationEntry(this.plugin, this.name, this.block);

    @override
    String toString() {
        return "$plugin:$name";
    }


    // Prevent Changing the values after build
    bool _hasBuild = false;

    /**
     * Should be called once all properties are set to their
     * desired values
     */

    build() {
        _hasBuild = true;


        // Handle legacy stuff
        // TODO: Remove once minecraft drops it
        if (_legacyId == -1) return; // Doesn't exist in the old system
        if (_allDataValues) {
            BlockRegistry._legacyMap[_legacyId] = new _BlockEntry(this);
        } else {
            if (!BlockRegistry._legacyMap.containsKey(_legacyId)) {
                BlockRegistry._legacyMap[_legacyId] = new _MultiBlockEntry();
            }
            (BlockRegistry._legacyMap[_legacyId] as _MultiBlockEntry).byData[_dataValue] = this;
        }
    }

    _checkBuild() {
        if (_hasBuild) {
            throw "Cannot change properties of a RegBlock after build";
        }
    }

    @Deprecated("Will be removed once minecraft drops it")
    legacyId(int id) {
        _checkBuild();
        _legacyId = id;
    }

    @Deprecated("Will be removed once minecraft drops it")
    dataValue(int val) {
        _checkBuild();
        _allDataValues = false;
        _dataValue = val;
    }
}