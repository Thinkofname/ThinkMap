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
import uk.co.thinkofdeath.mapviewer.shared.block.states.StateMap;
import uk.co.thinkofdeath.mapviewer.shared.support.IntMap;

import java.util.HashMap;
import java.util.Map;

public class BlockRegistry {

    private IMapViewer mapViewer;
    private Map<String, Block> blockMap = new HashMap<>();
    private Map<String, IntMap<Block>> blockStateMap = new HashMap<>();
    private IntMap<Block> legacyMap = IntMap.create();

    /**
     * Creates a block registry which contains all the known blocks
     *
     * @param mapViewer
     *         The map viewer to use for access to other parts of the program
     */
    public BlockRegistry(IMapViewer mapViewer) {
        this.mapViewer = mapViewer;
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
            return blockStateMap.get(name).get(state.asInt());
        }
        return null;
    }

    /**
     @deprecated Internal use only
     */
    @Deprecated
    public Block get(String name, int raw) {
        if (blockStateMap.containsKey(name)) {
            return blockStateMap.get(name).get(raw);
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
     * Adds the block to this registry with a legacy id. Legacy ids are only valid for 'minecraft' blocks
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
            block.fullName = key;
            blockMap.put(block.toString(), block);
            // State lookup
            if (!blockStateMap.containsKey(key)) {
                blockStateMap.put(key, IntMap.<Block>create());
            }
            blockStateMap.get(key).put(block.state.asInt(), block);
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
    public void init() {
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
                .transparent(true)
                .create(), 8);
        register("minecraft", "water", new BlockBuilder(new BlockLiquid(mapViewer))
                .texture("water_still")
                .solid(false)
                .transparent(true)
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
                .allowSelf(true)
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
        register("minecraft", "wool", new BlockBuilder(new BlockColoured(mapViewer, "wool_colored_"))
                .create(), 35);
        register("minecraft", "piston_extension", new BlockBuilder(mapViewer)
                .renderable(false)
                .solid(false)
                .collidable(false)
                .create(), 36);
        register("minecraft", "yellow_flower", new BlockBuilder(mapViewer)
                .model(BlockModels.createCross(mapViewer.getTexture("flower_dandelion")))
                .solid(false)
                .collidable(false)
                .create(), 37);
        register("minecraft", "red_flower", new BlockBuilder(new BlockFlowers(mapViewer))
                .solid(false)
                .collidable(false)
                .create(), 38);
        register("minecraft", "brown_mushroom", new BlockBuilder(mapViewer)
                .model(BlockModels.createCross(mapViewer.getTexture("mushroom_brown")))
                .solid(false)
                .collidable(false)
                .create(), 39);
        register("minecraft", "red_mushroom", new BlockBuilder(mapViewer)
                .model(BlockModels.createCross(mapViewer.getTexture("mushroom_red")))
                .solid(false)
                .collidable(false)
                .create(), 40);
        register("minecraft", "gold_block", new BlockBuilder(mapViewer)
                .texture("gold_block")
                .create(), 41);
        register("minecraft", "iron_block", new BlockBuilder(mapViewer)
                .texture("iron_block")
                .create(), 42);
        register("minecraft", "double_stone_slab", new BlockBuilder(new BlockDoubleSlab<>(mapViewer, BlockDoubleSlab.StoneSlab.class))
                .create(), 43);
        register("minecraft", "stone_slab", new BlockBuilder(new BlockSlab<>(mapViewer, BlockSlab.StoneSlab.class))
                .solid(false)
                .create(), 44);
        register("minecraft", "brick_block", new BlockBuilder(mapViewer)
                .texture("brick")
                .create(), 45);
        register("minecraft", "tnt", new BlockBuilder(new BlockMultiSide(mapViewer, "tnt_top",
                "tnt_bottom", "tnt_side", "tnt_side", "tnt_side", "tnt_side"))
                .create(), 46);
        register("minecraft", "bookshelf", new BlockBuilder(new BlockMultiSide(mapViewer,
                "planks_oak", "planks_oak", "bookshelf", "bookshelf", "bookshelf", "bookshelf"))
                .create(), 47);
        register("minecraft", "mossy_cobblestone", new BlockBuilder(mapViewer)
                .texture("cobblestone_mossy")
                .create(), 48);
        register("minecraft", "obsidian", new BlockBuilder(mapViewer)
                .texture("obsidian")
                .create(), 49);
        register("minecraft", "torch", new BlockBuilder(new BlockTorch(mapViewer, "torch_on"))
                .solid(false)
                .collidable(false)
                .create(), 50);
        register("minecraft", "fire", new BlockBuilder(new BlockFire(mapViewer))
                .solid(false)
                .collidable(false)
                .create(), 51);
        register("minecraft", "mob_spawner", new BlockBuilder(mapViewer)
                .texture("mob_spawner")
                .solid(false)
                .allowSelf(true)
                .create(), 52);

        register("minecraft", "oak_stairs", new BlockBuilder(new BlockStairs(mapViewer))
                .texture("planks_oak")
                .solid(false)
                .create(), 53);

        register("minecraft", "chest", new BlockBuilder(new BlockChest(mapViewer, "normal"))
                .solid(false)
                .create(), 54);
        register("minecraft", "redstone_wire", new BlockBuilder(new BlockRedstone(mapViewer))
                .solid(false)
                .collidable(false)
                .create(), 55);
        register("minecraft", "diamond_ore", new BlockBuilder(mapViewer)
                .texture("diamond_ore")
                .create(), 56);
        register("minecraft", "diamond_block", new BlockBuilder(mapViewer)
                .texture("diamond_block")
                .create(), 57);
        register("minecraft", "crafting_table", new BlockBuilder(new BlockMultiSide(
                mapViewer,
                "crafting_table_top", "planks_oak",
                "crafting_table_side", "crafting_table_side",
                "crafting_table_front", "crafting_table_front"
        ))
                .create(), 58);
        register("minecraft", "wheat", new BlockBuilder(new BlockCrop(mapViewer, "wheat", true))
                .solid(false)
                .collidable(false)
                .create(), 59);
        register("minecraft", "farmland", new BlockBuilder(new BlockFarmland(mapViewer))
                .solid(false)
                .create(), 60);
        register("minecraft", "furnace", new BlockBuilder(new BlockFurnace(mapViewer, false))
                .create(), 61);
        register("minecraft", "lit_furnace", new BlockBuilder(new BlockFurnace(mapViewer, true))
                .create(), 62);
        register("minecraft", "standing_sign", new BlockBuilder(new BlockFloorSign(mapViewer))
                .solid(false)
                .collidable(false)
                .create(), 63);
        register("minecraft", "wooden_door", new BlockBuilder(new BlockDoor(mapViewer, "door_wood"))
                .solid(false)
                .create(), 64);
        register("minecraft", "ladder", new BlockBuilder(new BlockLadder(mapViewer))
                .collidable(false)
                .solid(false)
                .create(), 65);
        register("minecraft", "rail", new BlockBuilder(new BlockRail(mapViewer))
                .texture("rail_normal")
                .solid(false)
                .collidable(false)
                .create(), 66);
        register("minecraft", "stone_stairs", new BlockBuilder(new BlockStairs(mapViewer))
                .texture("cobblestone")
                .solid(false)
                .create(), 67);
        register("minecraft", "wall_sign", new BlockBuilder(new BlockSign(mapViewer))
                .solid(false)
                .collidable(false)
                .create(), 68);
        register("minecraft", "lever", new BlockBuilder(new BlockLever(mapViewer))
                .solid(false)
                .collidable(false)
                .create(), 69);
        register("minecraft", "stone_pressure_plate", new BlockBuilder(
                new BlockPressurePlate(mapViewer, "stone"))
                .solid(false)
                .collidable(false)
                .create(), 70);
        register("minecraft", "iron_door", new BlockBuilder(new BlockDoor(mapViewer, "door_iron"))
                .solid(false)
                .create(), 71);
        register("minecraft", "wooden_pressure_plate", new BlockBuilder(
                new BlockPressurePlate(mapViewer, "planks_oak"))
                .solid(false)
                .collidable(false)
                .create(), 72);
        register("minecraft", "redstone_ore", new BlockBuilder(mapViewer)
                .texture("redstone_ore")
                .create(), 73);
        register("minecraft", "lit_redstone_ore", new BlockBuilder(mapViewer)
                .texture("redstone_ore")
                .create(), 74);
        register("minecraft", "unlit_redstone_torch", new BlockBuilder(new BlockTorch(mapViewer,
                "redstone_torch_off"))
                .solid(false)
                .collidable(false)
                .create(), 75);
        register("minecraft", "redstone_torch", new BlockBuilder(new BlockTorch(mapViewer,
                "redstone_torch_on"))
                .solid(false)
                .collidable(false)
                .create(), 76);
        register("minecraft", "stone_button", new BlockBuilder(new BlockButton(mapViewer, "stone"))
                .solid(false)
                .collidable(false)
                .create(), 77);
        register("minecraft", "snow_layer", new BlockBuilder(new BlockSnowLayer(mapViewer))
                .solid(false)
                .create(), 78);
        register("minecraft", "ice", new BlockBuilder(mapViewer)
                .texture("ice")
                .solid(false)
                .transparent(true)
                .create(), 79);
        register("minecraft", "snow", new BlockBuilder(mapViewer)
                .texture("snow")
                .create(), 80);
        register("minecraft", "cactus", new BlockBuilder(new BlockCactus(mapViewer))
                .solid(false)
                .create(), 81);
        register("minecraft", "clay", new BlockBuilder(mapViewer)
                .texture("clay")
                .create(), 82);
        register("minecraft", "reeds", new BlockBuilder(new BlockReeds(mapViewer))
                .solid(false)
                .collidable(false)
                .create(), 83);
        register("minecraft", "jukebox", new BlockBuilder(new BlockMultiSide(
                mapViewer,
                "jukebox_top", "noteblock",
                "jukebox_side", "jukebox_side",
                "jukebox_side", "jukebox_side"
        ))
                .create(), 84);
        register("minecraft", "fence", new BlockBuilder(new BlockFence(mapViewer))
                .solid(false)
                .texture("planks_oak")
                .create(), 85);
        register("minecraft", "pumpkin", new BlockBuilder(new BlockPumpkin(mapViewer, false))
                .create(), 86);
        register("minecraft", "netherrack", new BlockBuilder(mapViewer)
                .texture("netherrack")
                .create(), 87);
        register("minecraft", "soul_sand", new BlockBuilder(mapViewer)
                .texture("soul_sand")
                .create(), 88);
        register("minecraft", "glowstone", new BlockBuilder(mapViewer)
                .texture("glowstone")
                .create(), 89);
        register("minecraft", "portal", new BlockBuilder(new BlockPortal(mapViewer))
                .solid(false)
                .collidable(false)
                .transparent(true)
                .create(), 90);
        register("minecraft", "lit_pumpkin", new BlockBuilder(new BlockPumpkin(mapViewer, true))
                .create(), 91);
        register("minecraft", "cake", new BlockBuilder(new BlockCake(mapViewer))
                .solid(false)
                .create(), 92);
        register("minecraft", "unpowered_repeater", new BlockBuilder(new BlockRepeater(mapViewer, false))
                .solid(false)
                .create(), 93);
        register("minecraft", "powered_repeater", new BlockBuilder(new BlockRepeater(mapViewer, true))
                .solid(false)
                .create(), 94);
        register("minecraft", "stained_glass", new BlockBuilder(new BlockColoured(mapViewer, "glass_"))
                .solid(false)
                .transparent(true)
                .create(), 95);

        // TODO: trapdoor
        // TODO: monster_egg
        // TODO: stonebrick
        // TODO: brown_mushroom_block
        // TODO: red_mushroom_block
        // TODO: iron_bars
        // TODO: glass_pane
        // TODO: melon_block
        // TODO: pumpkin_stem
        // TODO: melon_stem

        register("minecraft", "vine", new BlockBuilder(new BlockVine(mapViewer))
                .solid(false)
                .create(), 106);

        // TODO: fence_gate

        register("minecraft", "brick_stairs", new BlockBuilder(new BlockStairs(mapViewer))
                .texture("brick")
                .solid(false)
                .create(), 108);
        register("minecraft", "stone_brick_stairs", new BlockBuilder(new BlockStairs(mapViewer))
                .texture("stonebrick")
                .solid(false)
                .create(), 109);
        register("minecraft", "mycelium", new BlockBuilder(new BlockMycelium(mapViewer))
                .create(), 110);

        // TODO: waterlily

        register("minecraft", "nether_brick", new BlockBuilder(mapViewer)
                .texture("nether_brick")
                .create(), 112);
        register("minecraft", "nether_brick_fence", new BlockBuilder(new BlockFence(mapViewer))
                .solid(false)
                .texture("nether_brick")
                .create(), 113);
        register("minecraft", "nether_brick_stairs", new BlockBuilder(new BlockStairs(mapViewer))
                .texture("nether_brick")
                .solid(false)
                .create(), 114);

        // TODO: nether_wart
        // TODO: enchanting_table
        // TODO: brewing_stand
        // TODO: cauldron
        // TODO: end_portal
        // TODO: end_portal_frame

        register("minecraft", "end_stone", new BlockBuilder(mapViewer)
                .texture("end_stone")
                .create(), 121);

        // TODO: dragon_egg

        register("minecraft", "redstone_lamp", new BlockBuilder(mapViewer)
                .texture("redstone_lamp_off")
                .create(), 123);
        register("minecraft", "lit_redstone_lamp", new BlockBuilder(mapViewer)
                .texture("redstone_lamp_on")
                .create(), 124);
        register("minecraft", "double_wooden_slab", new BlockBuilder(new BlockDoubleSlab<>(mapViewer, BlockDoubleSlab.WoodenSlab.class))
                .create(), 125);
        register("minecraft", "wooden_slab", new BlockBuilder(new BlockSlab<>(mapViewer, BlockSlab.WoodenSlab.class))
                .solid(false)
                .create(), 126);

        // TODO: cocoa

        register("minecraft", "sandstone_stairs", new BlockBuilder(new BlockStairs(mapViewer))
                .texture("sandstone_normal")
                .solid(false)
                .create(), 128);
        register("minecraft", "emerald_ore", new BlockBuilder(mapViewer)
                .texture("emerald_ore")
                .create(), 129);
        register("minecraft", "ender_chest", new BlockBuilder(new BlockChest(mapViewer, "ender"))
                .solid(false)
                .create(), 130);

        // TODO: tripwire_hook
        // TODO: tripwire

        register("minecraft", "emerald_block", new BlockBuilder(mapViewer)
                .texture("emerald_block")
                .create(), 133);
        register("minecraft", "spruce_stairs", new BlockBuilder(new BlockStairs(mapViewer))
                .texture("planks_spruce")
                .solid(false)
                .create(), 134);
        register("minecraft", "birch_stairs", new BlockBuilder(new BlockStairs(mapViewer))
                .texture("planks_birch")
                .solid(false)
                .create(), 135);
        register("minecraft", "jungle_stairs", new BlockBuilder(new BlockStairs(mapViewer))
                .texture("planks_jungle")
                .solid(false)
                .create(), 136);
        register("minecraft", "command_block", new BlockBuilder(mapViewer)
                .texture("command_block")
                .create(), 137);

        // TODO: beacon
        // TODO: cobblestone_wall
        // TODO: flower_pot

        register("minecraft", "carrots", new BlockBuilder(new BlockCrop(mapViewer, "carrots", false))
                .solid(false)
                .collidable(false)
                .create(), 141);
        register("minecraft", "potatoes", new BlockBuilder(new BlockCrop(mapViewer, "potatoes", false))
                .solid(false)
                .collidable(false)
                .create(), 142);
        register("minecraft", "wooden_button", new BlockBuilder(new BlockButton(mapViewer, "planks_oak"))
                .solid(false)
                .collidable(false)
                .create(), 143);


        // TODO: skull
        // TODO: anvil

        register("minecraft", "trapped_chest", new BlockBuilder(new BlockChest(mapViewer, "trapped"))
                .solid(false)
                .create(), 146);
        register("minecraft", "light_weighted_pressure_plate", new BlockBuilder(
                new BlockPressurePlate(mapViewer, "gold_block"))
                .solid(false)
                .collidable(false)
                .create(), 147);
        register("minecraft", "heavy_weighted_pressure_plate", new BlockBuilder(
                new BlockPressurePlate(mapViewer, "iron_block"))
                .solid(false)
                .collidable(false)
                .create(), 148);

        // TODO: unpowered_comparator
        // TODO: powered_comparator
        // TODO: daylight_detector

        register("minecraft", "redstone_block", new BlockBuilder(mapViewer)
                .texture("redstone_block")
                .create(), 152);
        register("minecraft", "quartz_ore", new BlockBuilder(mapViewer)
                .texture("quartz_ore")
                .create(), 153);
        register("minecraft", "hopper", new BlockBuilder(new BlockHopper(mapViewer))
                .solid(false)
                .create(), 154);
        register("minecraft", "quartz_block", new BlockBuilder(new BlockQuartz(mapViewer))
                .create(), 155);
        register("minecraft", "quartz_stairs", new BlockBuilder(new BlockStairs(mapViewer))
                .texture("quartz_block_top")
                .solid(false)
                .create(), 156);
        register("minecraft", "activator_rail", new BlockBuilder(new BlockPoweredRail(mapViewer,
                "rail_activator"))
                .solid(false)
                .collidable(false)
                .create(), 157);
        register("minecraft", "dropper", new BlockBuilder(new BlockDispenser(mapViewer, "dropper"))
                .create(), 158);
        register("minecraft", "stained_hardened_clay", new BlockBuilder(new BlockColoured(mapViewer, "hardened_clay_stained_"))
                .create(), 159);

        // TODO: stained_glass_pane
        // TODO: leaves2
        // TODO: log2

        register("minecraft", "acacia_stairs", new BlockBuilder(new BlockStairs(mapViewer))
                .texture("planks_acacia")
                .solid(false)
                .create(), 163);
        register("minecraft", "dark_oak_stairs", new BlockBuilder(new BlockStairs(mapViewer))
                .texture("planks_big_oak")
                .solid(false)
                .create(), 164);

        // TODO: hay_block
        // TODO: carpet

        register("minecraft", "hardened_clay", new BlockBuilder(mapViewer)
                .texture("hardened_clay")
                .create(), 172);
        register("minecraft", "coal_block", new BlockBuilder(mapViewer)
                .texture("coal_block")
                .create(), 173);
        register("minecraft", "packed_ice", new BlockBuilder(mapViewer)
                .texture("ice_packed")
                .create(), 174);

        // TODO: double_plant

        // ThinkMap blocks
        register("thinkmap", "missing_block", new BlockBuilder(mapViewer)
                .texture("missing_texture")
                .create());
        register("thinkmap", "null", new BlockBuilder(mapViewer)
                .renderable(false)
                .create());

        Blocks.init(this);

        System.out.println("Blocks registered: " + blockMap.size() + " (" + blockStateMap.size()
                + " excluding states)");
    }
}
