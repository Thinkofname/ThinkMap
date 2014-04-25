/*
 * Copyright 2014 Matthew Collins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.thinkofdeath.mapviewer.shared.block;

import uk.co.thinkofdeath.mapviewer.shared.IMapViewer;
import uk.co.thinkofdeath.mapviewer.shared.block.blocks.*;
import uk.co.thinkofdeath.mapviewer.shared.logging.Logger;
import uk.co.thinkofdeath.mapviewer.shared.support.IntMap;

import java.util.HashMap;
import java.util.Map;

public class BlockRegistry {

    private final Logger logger;
    private IMapViewer mapViewer;
    private Map<String, Block> blockMap = new HashMap<>();
    private Map<String, Map<StateMap, Block>> blockStateMap = new HashMap<>();
    private IntMap<Block> legacyMap = IntMap.create();

    /**
     * Creates a block registry which contains all the known blocks
     *
     * @param mapViewer
     *         The map viewer to use for access to other parts of the program
     */
    public BlockRegistry(IMapViewer mapViewer) {
        this.mapViewer = mapViewer;
        logger = mapViewer.getLoggerFactory().getLogger("BlockRegistry");
    }

    /**
     * Returns a block by its name.
     *
     * @param name
     *         The name of the block
     * @return The block or null
     */
    public Block get(String name) {
        return blockMap.get(name);
    }

    /**
     * Returns a block by its name.
     *
     * @param name
     *         The name of the block
     * @param state
     *         The state of the block
     * @return The block or null
     */
    public Block get(String name, StateMap state) {
        if (blockStateMap.containsKey(name)) {
            return blockStateMap.get(name).get(state);
        }
        return null;
    }

    /**
     * Returns a block by its name and plugin.
     *
     * @param plugin
     *         The plugin that owns the block
     * @param name
     *         The name of the block
     * @return The block or null
     */
    public Block get(String plugin, String name) {
        return blockMap.get(plugin + ":" + name);
    }

    /**
     * Returns a block by its legacy id and data value
     *
     * @param legacyId
     *         The old style id for the block
     * @param dataValue
     *         The data value for the block
     * @return The block or null
     */
    public Block get(int legacyId, int dataValue) {
        return legacyMap.get((legacyId << 4) | dataValue);
    }


    /**
     * Adds the block to this registry
     *
     * @param plugin
     *         The plugin the block belongs to
     * @param name
     *         The name of the block
     * @param blocks
     *         The blocks to add
     */
    private void register(String plugin, String name, BlockFactory blocks) {
        register(plugin, name, blocks, -1);
    }

    /**
     * Adds the block to this registry with a legacy id. Legacy ids are only valid for 'minecraft'
     * blocks
     *
     * @param plugin
     *         The plugin the block belongs to
     * @param name
     *         The name of the block
     * @param blocks
     *         The blocks to add
     * @param legacyId
     *         The old style id for this block
     */
    private void register(String plugin, String name, BlockFactory blocks, int legacyId) {
        if (legacyId == -1 && plugin.equals("minecraft")) {
            throw new IllegalArgumentException("Minecraft blocks must have legacy ids");
        } else if (legacyId != -1 && !plugin.equals("minecraft")) {
            throw new IllegalArgumentException("Legacy ids must only be used for Minecraft blocks");
        }
        String key = plugin + ":" + name;
        for (Block block : blocks.getBlocks()) {
            block.plugin = plugin;
            block.name = name;
            logger.debug(block.toString());
            blockMap.put(block.toString(), block);
            // State lookup
            if (!blockStateMap.containsKey(key)) {
                blockStateMap.put(key, new HashMap<StateMap, Block>());
            }
            blockStateMap.get(key).put(block.state, block);
            // Legacy support
            if (legacyId != -1) {
                int data = block.getLegacyData();
                if (data == -1) continue; // Virtual block
                int legacyVal = (legacyId << 4) | data;
                if (legacyMap.get(legacyVal) != null) {
                    throw new IllegalArgumentException(block.toString() + " tried to register a duplicate block id "
                            + legacyId + ":" + block.getLegacyData());
                }
                legacyMap.put(legacyVal, block);
            }
        }
    }

    /**
     * Loads all known blocks
     */
    public void init(IMapViewer mapViewer) {
        // Vanilla blocks
        register("minecraft", "air", new BlockBuilder(mapViewer)
                .renderable(false)
                .collidable(false)
                .solid(false).create(), 0);
        register("minecraft", "stone", new BlockBuilder(mapViewer)
                .texture("stone")
                .create(), 1);
        register("minecraft", "grass", new BlockBuilder(new BlockGrass(mapViewer))
                .create(), 2);
        register("minecraft", "dirt", new BlockBuilder(new BlockDirt(mapViewer))
                .create(), 3);
        register("minecraft", "cobblestone", new BlockBuilder(mapViewer)
                .texture("cobblestone")
                .create(), 4);
        register("minecraft", "planks", new BlockBuilder(new BlockPlanks(mapViewer))
                .create(), 5);
        register("minecraft", "sapling", new BlockBuilder(new BlockSapling(mapViewer))
                .solid(false)
                .collidable(false)
                .create(), 6);
        register("minecraft", "bedrock", new BlockBuilder(mapViewer)
                .texture("bedrock")
                .create(), 7);
        register("minecraft", "flowing_water", new BlockBuilder(new BlockLiquid(mapViewer))
                .texture("water_flow")
                .solid(false)
                .transparent(false /*TODO*/)
                .create(), 8);
        register("minecraft", "water", new BlockBuilder(new BlockLiquid(mapViewer))
                .texture("water_still")
                .solid(false)
                .transparent(false /*TODO*/)
                .create(), 9);
        register("minecraft", "flowing_lava", new BlockBuilder(new BlockLiquid(mapViewer))
                .texture("lava_flow")
                .solid(false)
                .create(), 10);
        register("minecraft", "lava", new BlockBuilder(new BlockLiquid(mapViewer))
                .texture("lava_still")
                .solid(false)
                .create(), 11);
        register("minecraft", "sand", new BlockBuilder(new BlockSand(mapViewer))
                .create(), 12);
        register("minecraft", "gravel", new BlockBuilder(mapViewer)
                .texture("gravel")
                .create(), 13);
        register("minecraft", "gold_ore", new BlockBuilder(mapViewer)
                .texture("gold_ore")
                .create(), 14);
        register("minecraft", "iron_ore", new BlockBuilder(mapViewer)
                .texture("iron_ore")
                .create(), 15);
        register("minecraft", "coal_ore", new BlockBuilder(mapViewer)
                .texture("coal_ore")
                .create(), 16);
        register("minecraft", "log", new BlockBuilder(new BlockLog(mapViewer))
                .create(), 17);
        register("minecraft", "leaves", new BlockBuilder(new BlockLeaves(mapViewer))
                .solid(false)
                .create(), 18);
        register("minecraft", "sponge", new BlockBuilder(mapViewer)
                .texture("sponge")
                .create(), 19);
        register("minecraft", "glass", new BlockBuilder(mapViewer)
                .texture("glass")
                .solid(false)
                .create(), 20);
        register("minecraft", "lapis_ore", new BlockBuilder(mapViewer)
                .texture("lapis_ore")
                .create(), 21);
        register("minecraft", "lapis_block", new BlockBuilder(mapViewer)
                .texture("lapis_block")
                .create(), 22);
        register("minecraft", "dispenser", new BlockBuilder(new BlockDispenser(mapViewer,
                "dispenser"))
                .create(), 23);
        register("minecraft", "sandstone", new BlockBuilder(new BlockSandstone(mapViewer))
                .create(), 24);
        register("minecraft", "noteblock", new BlockBuilder(mapViewer)
                .texture("noteblock")
                .create(), 25);
        register("minecraft", "bed", new BlockBuilder(new BlockBed(mapViewer))
                .solid(false)
                .create(), 26);
        register("minecraft", "golden_rail", new BlockBuilder(new BlockPoweredRail(mapViewer,
                "rail_golden"))
                .solid(false)
                .collidable(false)
                .create(), 27);
        register("minecraft", "detector_rail", new BlockBuilder(new BlockPoweredRail(mapViewer,
                "rail_detector"))
                .solid(false)
                .collidable(false)
                .create(), 28);
        register("minecraft", "sticky_piston", new BlockBuilder(new BlockPiston(mapViewer, "sticky"))
                .solid(false)
                .create(), 29);
        register("minecraft", "web", new BlockBuilder(mapViewer)
                .model(BlockModels.createCross(mapViewer.getTexture("web")))
                .solid(false)
                .collidable(false)
                .create(), 30);
        register("minecraft", "tallgrass", new BlockBuilder(new BlockTallGrass(mapViewer))
                .solid(false)
                .collidable(false)
                .create(), 31);
        register("minecraft", "deadbush", new BlockBuilder(mapViewer)
                .model(BlockModels.createCross(mapViewer.getTexture("deadbush")))
                .solid(false)
                .collidable(false)
                .create(), 32);
        register("minecraft", "piston", new BlockBuilder(new BlockPiston(mapViewer, "normal"))
                .solid(false)
                .create(), 33);
        register("minecraft", "piston_head", new BlockBuilder(new BlockPistonHead(mapViewer))
                .solid(false)
                .create(), 34);

        register("minecraft", "rail", new BlockBuilder(new BlockRail(mapViewer))
                .texture("rail_normal")
                .solid(false)
                .collidable(false)
                .create(), 66);

        register("minecraft", "vine", new BlockBuilder(new BlockVine(mapViewer))
                .solid(false)
                .create(), 106);

        register("minecraft", "emerald_ore", new BlockBuilder(mapViewer)
                .texture("emerald_ore")
                .create(), 129);

        register("minecraft", "quartz_ore", new BlockBuilder(mapViewer)
                .texture("quartz_ore")
                .create(), 153);

        register("minecraft", "activator_rail", new BlockBuilder(new BlockPoweredRail(mapViewer,
                "rail_activator"))
                .solid(false)
                .collidable(false)
                .create(), 157);
        register("minecraft", "dropper", new BlockBuilder(new BlockDispenser(mapViewer, "dropper"))
                .create(), 158);

        // ThinkMap blocks
        register("thinkmap", "missing_block", new BlockBuilder(mapViewer)
                .texture("missing_texture")
                .create());
        register("thinkmap", "null", new BlockBuilder(mapViewer)
                .renderable(false)
                .create());

        Blocks.init(this);
    }
}
