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

package uk.co.thinkofdeath.mapviewer.shared.world;

import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public abstract class Chunk {

    protected final ChunkSection[] sections = new ChunkSection[16];
    // TODO: Fix the leaking as the blocks are never removed
    // from these maps
    protected final Map<Block, Integer> blockIdMap = new HashMap<>();
    protected final Map<Integer, Block> idBlockMap = new HashMap<>();
    private final World world;
    protected int nextId = 1;
    private int x;
    private int z;
    private boolean unloaded;

    protected Chunk(World world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;

        // Always have air as the first block
        blockIdMap.put(Blocks.AIR, 0);
        idBlockMap.put(0, Blocks.AIR);
    }

    /**
     * Gets the chunks position along the x axis
     *
     * @return The position on the x axis
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the chunks position along the z axis
     *
     * @return The position on the z axis
     */
    public int getZ() {
        return z;
    }

    /**
     * Called when the chunk is unloaded by the world. Should be overridden to handle unloading of
     * resources.
     */
    public void unload() {
        unloaded = true;
    }

    /**
     * Returns whether the chunk is unloaded and shouldn't have a reference pointed to it
     *
     * @return Whether the chunk is unloaded
     */
    public boolean isUnloaded() {
        return unloaded;
    }

    /**
     * Returns the block at location given by the coordinates x, y, z relative to the chunk. The x
     * and z coordinates must be between 0 and 15. The y coordinate must be between 0 and 255.
     *
     * @param x
     *         The position on the x axis
     * @param y
     *         The position on the y axis
     * @param z
     *         The position on the z axis
     * @return The block at the location
     */
    public Block getBlock(int x, int y, int z) {
        ChunkSection section = sections[y >> 4];
        if (section == null) {
            return Blocks.AIR;
        }
        return idBlockMap.get(section.getBlocks().get(x | (z << 4) | ((y & 0xF) << 8)));
    }

    /**
     * Returns the emitted light level at location given by the coordinates x, y, z relative to the
     * chunk. The x and z coordinates must be between 0 and 15. The y coordinate must be between 0
     * and 255.
     *
     * @param x
     *         The position on the x axis
     * @param y
     *         The position on the y axis
     * @param z
     *         The position on the z axis
     * @return The emitted light at the location
     */
    public int getEmittedLight(int x, int y, int z) {
        ChunkSection section = sections[y >> 4];
        if (section == null) {
            return 0;
        }
        return section.getBlockLight().get(x | (z << 4) | ((y & 0xF) << 8));
    }

    /**
     * Returns the sky light level at location given by the coordinates x, y, z relative to the
     * chunk. The x and z coordinates must be between 0 and 15. The y coordinate must be between 0
     * and 255.
     *
     * @param x
     *         The position on the x axis
     * @param y
     *         The position on the y axis
     * @param z
     *         The position on the z axis
     * @return The sky light at the location
     */
    public int getSkyLight(int x, int y, int z) {
        ChunkSection section = sections[y >> 4];
        if (section == null) {
            return 15;
        }
        return section.getSkyLight().get(x | (z << 4) | ((y & 0xF) << 8));
    }

    /**
     * Returns the world this chunk belongs to
     *
     * @return The world that owns this chunk
     */
    public World getWorld() {
        return world;
    }
}
