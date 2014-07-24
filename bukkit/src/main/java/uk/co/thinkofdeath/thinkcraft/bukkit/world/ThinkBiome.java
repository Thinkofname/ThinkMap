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

package uk.co.thinkofdeath.thinkcraft.bukkit.world;

import org.bukkit.block.Biome;

import java.util.Arrays;

public enum ThinkBiome {
    // Snowy
    FROZEN_RIVER(Biome.FROZEN_RIVER, 11),
    ICE_PLAINS(Biome.ICE_PLAINS, 12),
    ICE_PLAINS_SPIKES(Biome.ICE_PLAINS_SPIKES, 140),
    COLD_BEACH(Biome.COLD_BEACH, 26),
    COLD_TAIGA(Biome.COLD_TAIGA, 30),
    COLD_TAIGA_MOUNTAINS(Biome.COLD_TAIGA_MOUNTAINS, 158),
    // Cold
    EXTREME_HILLS(Biome.EXTREME_HILLS, 3),
    EXTREME_HILLS_MOUNTAINS(Biome.EXTREME_HILLS_MOUNTAINS, 131),
    TAIGA(Biome.TAIGA, 5),
    TAIGA_M(Biome.TAIGA_MOUNTAINS, 133),
    THE_END(Biome.SKY, 9),
    MEGA_TAIGA(Biome.MEGA_TAIGA, 32),
    MEGA_SPRUCE_TAIGA(Biome.MEGA_SPRUCE_TAIGA, 160),
    EXTREME_HILLS_PLUS(Biome.EXTREME_HILLS_PLUS, 34),
    EXTREME_HILLS_PLUS_MOUNTAINS(Biome.EXTREME_HILLS_MOUNTAINS, 162),
    STONE_BEACH(Biome.STONE_BEACH, 25),
    // Medium/Lush
    PLAINS(Biome.PLAINS, 1),
    SUNFLOWER_PLAINS(Biome.SUNFLOWER_PLAINS, 129),
    FOREST(Biome.FOREST, 4),
    FLOWER_FOREST(Biome.FLOWER_FOREST, 132),
    SWAMPLAND(Biome.SWAMPLAND, 6),
    SWAMPLAND_MOUNTAINS(Biome.SWAMPLAND_MOUNTAINS, 134),
    RIVER(Biome.RIVER, 7),
    MUSHROOM_ISLAND(Biome.MUSHROOM_ISLAND, 14),
    MUSHROOM_ISLAND_SHORE(Biome.MUSHROOM_SHORE, 15),
    BEACH(Biome.BEACH, 16),
    JUNGLE(Biome.JUNGLE, 21),
    JUNGLE_MOUNTAINS(Biome.JUNGLE_MOUNTAINS, 149),
    JUNGLE_EDGE(Biome.JUNGLE_EDGE, 23),
    JUNGLE_EDGE_MOUNTAINS(Biome.JUNGLE_EDGE_MOUNTAINS, 151),
    BIRCH_FOREST(Biome.BIRCH_FOREST, 27),
    BIRCH_FOREST_MOUNTAINS(Biome.BIRCH_FOREST_MOUNTAINS, 155),
    ROOFED_FOREST(Biome.ROOFED_FOREST, 29),
    ROOFED_FOREST_MOUNTAIN(Biome.ROOFED_FOREST_MOUNTAINS, 157),
    // Dry/Warm
    DESERT(Biome.DESERT, 2),
    DESERT_MOUNTAIN(Biome.DESERT_MOUNTAINS, 130),
    HELL(Biome.HELL, 8),
    SAVANNA(Biome.SAVANNA, 35),
    SAVANNA_MOUNTAINS(Biome.SAVANNA_MOUNTAINS, 163),
    MESA(Biome.MESA, 37),
    MESA_BRYCE(Biome.MESA_BRYCE, 165),
    SAVANNA_PLATEAU(Biome.SAVANNA_PLATEAU, 36),
    MESA_PLATEAU_FOREST(Biome.MESA_PLATEAU_FOREST, 38),
    MESA_PLATEAU(Biome.MESA_PLATEAU, 39),
    SAVANNA_PLATEAU_MOUNTAINS(Biome.SAVANNA_PLATEAU_MOUNTAINS, 164),
    MESA_PLATEAU_FOREST_MOUNTAINS(Biome.MESA_PLATEAU_FOREST_MOUNTAINS, 166),
    MESA_PLATEAU_MOUNTAINS(Biome.MESA_PLATEAU_MOUNTAINS, 167),
    // Neutral
    OCEAN(Biome.OCEAN, 0),
    DEEP_OCEAN(Biome.DEEP_OCEAN, 24),
    ICE_MOUNTAINS(Biome.ICE_MOUNTAINS, 13),
    DESERT_HILLS(Biome.DESERT_HILLS, 17),
    FOREST_HILLS(Biome.FOREST_HILLS, 18),
    TAIGA_HILLS(Biome.TAIGA_HILLS, 19),
    JUNGLE_HILLS(Biome.JUNGLE_HILLS, 22),
    BIRCH_FOREST_HILLS(Biome.BIRCH_FOREST_HILLS, 28),
    COLD_TAIGA_HILLS(Biome.COLD_TAIGA_HILLS, 31),
    MEGA_TAIGA_HILLS(Biome.MEGA_TAIGA_HILLS, 33),
    BIRCH_FOREST_HILLS_MOUNTAINS(Biome.BIRCH_FOREST_HILLS_MOUNTAINS, 156),
    MEGA_SPRUCE_TAIGA_HILLS(Biome.MEGA_SPRUCE_TAIGA_HILLS, 161),;
    private static final int[] ids = new int[Biome.values().length];
    private final Biome biome;
    private final int id;

    ThinkBiome(Biome biome, int id) {
        this.biome = biome;
        this.id = id;
    }

    public static int bukkitToId(Biome biome) {
        return ids[biome.ordinal()];
    }

    static {
        Arrays.fill(ids, 255);
        for (ThinkBiome biome : values()) {
            ids[biome.biome.ordinal()] = biome.id;
        }
    }
}
