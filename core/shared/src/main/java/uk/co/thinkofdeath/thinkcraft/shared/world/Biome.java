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

package uk.co.thinkofdeath.thinkcraft.shared.world;

import java.util.Arrays;

public enum Biome {
    // Snowy
    FROZEN_OCEAN(10, 0.0, 0.5),
    FROZEN_RIVER(11, 0.0, 0.5),
    ICE_PLAINS(12, 0.0, 0.5),
    ICE_PLAINS_SPIKES(140, 0.0, 0.5),
    COLD_BEACH(26, 0.05, 0.3),
    COLD_TAIGA(30, 0.0, 0.4),
    COLD_TAIGA_MOUNTAINS(158, 0.0, 0.4),
    // Cold
    EXTREME_HILLS(3, 0.2, 0.3),
    EXTREME_HILLS_MOUNTAINS(131, 0.2, 0.3),
    TAIGA(5, 0.25, 0.8),
    TAIGA_M(133, 0.25, 0.8),
    THE_END(9),
    MEGA_TAIGA(32, 0.3, 0.8),
    MEGA_SPRUCE_TAIGA(160),
    EXTREME_HILLS_PLUS(34, 0.2, 0.3),
    EXTREME_HILLS_PLUS_MOUNTAINS(162, 0.2, 0.3),
    STONE_BEACH(25, 0.2, 0.3),
    // Medium/Lush
    PLAINS(1),
    SUNFLOWER_PLAINS(129),
    FOREST(4),
    FLOWER_FOREST(132),
    SWAMPLAND(6, 0.8, 0.9),
    SWAMPLAND_MOUNTAINS(134, 0.8, 0.9),
    RIVER(7),
    MUSHROOM_ISLAND(14, 0.9, 1.0),
    MUSHROOM_ISLAND_SHORE(15, 0.9, 1.0),
    BEACH(16, 0.8, 0.4),
    JUNGLE(21, 0.95, 0.9),
    JUNGLE_MOUNTAINS(149, 0.95, 0.9),
    JUNGLE_EDGE(23, 0.95, 0.8),
    JUNGLE_EDGE_MOUNTAINS(151, 0.95, 0.8),
    BIRCH_FOREST(27),
    BIRCH_FOREST_MOUNTAINS(155),
    ROOFED_FOREST(29),
    ROOFED_FOREST_MOUNTAIN(157),
    // Dry/Warm
    DESERT(2, 1.0, 0.0),
    DESERT_MOUNTAIN(130, 1.0, 0.0),
    HELL(8, 1.0, 0.0),
    SAVANNA(35, 1.0, 0.0),
    SAVANNA_MOUNTAINS(163, 1.0, 0.0),
    MESA(37),
    MESA_BRYCE(165),
    SAVANNA_PLATEAU(36, 1.0, 0.0),
    MESA_PLATEAU_FOREST(38),
    MESA_PLATEAU(39),
    SAVANNA_PLATEAU_MOUNTAINS(164, 1.0, 0.0),
    MESA_PLATEAU_FOREST_MOUNTAINS(166),
    MESA_PLATEAU_MOUNTAINS(167),
    // Neutral
    OCEAN(0),
    DEEP_OCEAN(24),
    ICE_MOUNTAINS(13, 0.0, 0.5),
    DESERT_HILLS(17, 1.0, 0.0),
    FOREST_HILLS(18, 0.8, 0.9),
    TAIGA_HILLS(19, 0.25, 0.8),
    JUNGLE_HILLS(22, 0.95, 0.9),
    BIRCH_FOREST_HILLS(28),
    COLD_TAIGA_HILLS(31),
    MEGA_TAIGA_HILLS(33, 0.3, 0.8),
    BIRCH_FOREST_HILLS_MOUNTAINS(156),
    MEGA_SPRUCE_TAIGA_HILLS(161),
    // ThinkMap
    INVALID(255, 0.0, 0.0);
    private static final Biome[] ids = new Biome[256];
    private final int id;
    private final double temperature;
    private final double moisture;

    Biome(int id) {
        this(id, 0.5, 0.5);
    }

    Biome(int id, double temperature, double moisture) {
        this.id = id;
        this.temperature = temperature;
        this.moisture = moisture;
    }

    public int getId() {
        return id;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getMoisture() {
        return moisture;
    }

    public static Biome getById(int id) {
        return ids[id];
    }

    static {
        Arrays.fill(ids, INVALID);
        for (Biome biome : values()) {
            ids[biome.id] = biome;
        }
    }

    // Helper
    /*
    public static void main(String[] args) {
        HashSet<Integer> used = new HashSet<>();
        for (Biome biome : values()) {
            double moisture = biome.moisture * biome.temperature;
            int x = (int) ((1.0 - biome.temperature) * 255.0);
            int y = (int) ((1.0 - moisture) * 255.0);
            int idx = x | (y << 8);
            if (!used.contains(idx)) {
                used.add(idx);
                System.out.printf("add(%d | (%d << 8));\n", x, y);
            }
        }
    }
    */
}
