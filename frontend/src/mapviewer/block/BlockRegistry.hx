package mapviewer.block;

import mapviewer.block.BlockRegistry.BlockRegistrationEntry;
import mapviewer.logging.Logger;

class BlockRegistry {
    public static var logger : Logger = new Logger('BlockRegistry');

    /// Whether the blocks have been registered yet
    private static var hasInit : Bool = false;
    /// A map of plugin to blocks name to blocks
    private static var blocks : Map<String, Map<String, BlockRegistrationEntry>> = new Map();

    /**
     * Returns a block by its name.
     *
     * The format of the name of 'plugin:block'. If the format
     * 'block' is used then the plugin is assumed to be 'minecraft'.
     *
     * If the block doesn't exist then the block 'webglmap:missing_block'
     * is returned.
     */
    public static function get(name : String) : Block {
        if (!hasInit) init();
        var plugin = "minecraft";
        var index : Int = 0;
        if ((index = name.indexOf(":")) != -1) {
            plugin = name.substring(0, index);
            name = name.substring(index + 1);
        }
        if (blocks[plugin] == null) return Blocks.MISSING_BLOCK;
        var ret : BlockRegistrationEntry = blocks[plugin][name];
        if (ret == null) return Blocks.MISSING_BLOCK;
        return ret.block;
    }

    /// A map of legacy blocks ids to blocks
    public static var legacyMap : Array<BlockEntry> = new Array();

    /**
     * Using the id and data from legacy block id system
     * this will look up the block and return the new system
     * version of the block.
     *
     * If the block doesn't exist then the block 'webglmap:missing_block'
     */
    public static function getByLegacy(id : Int, data : Int) : Block {
        if (id == 0) return Blocks.AIR;
        var val = legacyMap[id];
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
    public static function registerBlock(name : String, block : Block,
                                         ?plugin : String = "minecraft") : BlockRegistrationEntry {
        if (!blocks.exists(plugin)) {
            blocks[plugin] = new Map();
        }
        if (blocks[plugin].exists(name)) {
            throw 'Tried to double register block $name';
        }
        var reg = new BlockRegistrationEntry(plugin, name, block);
		block.regBlock = reg;
        blocks[plugin][name] = reg;
        logger.info('Registed block: $reg');
        return reg;
    }

    /**
     * Register all blocks
     */
    public static function init() {
        if (hasInit) return;
        hasInit = true;
		
		// Vanilla blocks
		registerBlock("air", new Block().chain()
			.renderable(false)
			.solid(false)
			.collidable(false)
			.ret())
			.legacyId(0).build();
		registerBlock("stone", new Block().chain()
			.texture("stone").ret())
			.legacyId(1).build();
		registerBlock("vines", new Block().chain()
			.renderable(false)
			.solid(false)
			.collidable(false)
			.ret())
			.legacyId(106).build();

		// Custom blocks
        registerBlock("missing_block", new Block().chain()
            .texture("missing_texture")
            .ret(), "webglmap")
            .build();
        registerBlock("null", new Block().chain()
            .renderable(false)
            .shade(false)
            .ret(), "webglmap").build();
    }
}

class BlockRegistrationEntry {
    public var plugin : String;
    public var name : String;
    public var block : Block;

    // Legacy Support
    private var _legacyId : Int = -1;
    private var allDataValues : Bool = true;
    private var _dataValue : Int;

    public function new(plugin : String, name : String, block : Block) {
        this.plugin = plugin;
        this.name = name;
        this.block = block;
    }

    public function toString() : String {
        return '$plugin:$name';
    }

    // Builder things

    private var hasBuilt : Bool = false;

    public function build() {
        hasBuilt = true;

        // Handle legacy stuff
        // TODO: Remove once minecraft drops it
        if (_legacyId == -1) return; // Doesn't exist in the old system
        if (allDataValues) {
            if (plugin != "minecraft") BlockRegistry.logger.warn('$this is using legacy block ids ($_legacyId)');
            BlockRegistry.legacyMap[_legacyId] = new SingleBlockEntry(this);
        } else {
            if (plugin != "minecraft") BlockRegistry.logger.warn('$this is using legacy block ids ($_legacyId:$_dataValue)');
            if (BlockRegistry.legacyMap[_legacyId] == null) {
                BlockRegistry.legacyMap[_legacyId] = new MultiBlockEntry();
            }
            var map : MultiBlockEntry = cast BlockRegistry.legacyMap[_legacyId];
            map.byData[_dataValue] = this;
        }
    }

    private function checkBuild() {
        if (hasBuilt) throw "Cannot changeproperties of a BlockRegistrationEntry after build";
    }

    public function legacyId(id : Int) : BlockRegistrationEntry {
        checkBuild();
        _legacyId = id;
        return this;
    }

    public function dataValue(val : Int) : BlockRegistrationEntry {
        checkBuild();
        allDataValues = false;
        _dataValue = val;
        return this;
    }
}

/**
 * An entry for a block that can convert Minecraft data value
 * into a registered block
 */
private interface BlockEntry {

    /**
     * Returns a registered block based on the data value
     */
    public function getBlock(data : Int) : BlockRegistrationEntry;
}

/**
 * A BlockEntry that always returns the same block
 */
private class SingleBlockEntry implements BlockEntry {

    private var block : BlockRegistrationEntry;

    public function new(block : BlockRegistrationEntry) {
        this.block = block;
    }

    public function getBlock(data : Int) : BlockRegistrationEntry {
        return block;
    }
}

/**
 * A BlockEntry that will return 1 of 16 blocks based on
 * passed data value
 */
private class MultiBlockEntry implements BlockEntry {

    public var byData : Array<BlockRegistrationEntry>;

    public function new() {
        byData = new Array();
    }

    public function getBlock(data : Int) : BlockRegistrationEntry {
        return byData[data];
    }
}