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

    protected Chunk(World world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;

        // Always have air as the first block
        blockIdMap.put(world.getMapViewer().getBlockRegistry().get("minecraft:air"), 0);
        idBlockMap.put(0, world.getMapViewer().getBlockRegistry().get("minecraft:air"));
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
     * Called when the chunk is unloaded by the world. May be overridden to handle unloading of
     * resources.
     */
    public void unload() {

    }
}
