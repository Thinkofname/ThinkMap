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

import uk.co.thinkofdeath.thinkcraft.shared.IMapViewer;
import uk.co.thinkofdeath.thinkcraft.shared.block.Block;
import uk.co.thinkofdeath.thinkcraft.shared.block.Blocks;
import uk.co.thinkofdeath.thinkcraft.shared.platform.Platform;
import uk.co.thinkofdeath.thinkcraft.shared.util.PositionMap;

public abstract class World {

    private final IMapViewer mapViewer;
    private int timeOfDay = 6000;
    private PositionMap<Chunk> chunks = new PositionMap<>();

    protected World(IMapViewer mapViewer) {
        this.mapViewer = mapViewer;
        Platform.runRepeated(new Runnable() {
            @Override
            public void run() {
                tick();
            }
        }, 1000 / 20);
    }

    /**
     * Returns a String that can be used to identify a chunk
     *
     * @param x
     *         The position of the chunk on the x axis
     * @param z
     *         The position of the chunk on the z axis
     * @return The string key
     */
    public static String chunkKey(int x, int z) {
        return x + ":" + z;
    }

    /**
     * Tick the world and its chunks. Should be called at most 20 times a second
     */
    protected void tick() {
        timeOfDay = (timeOfDay + 1) % 24000;
    }

    /**
     * Adds the chunk to the world if it isn't already loaded
     *
     * @param chunk
     *         The chunk to add
     */
    public void addChunk(Chunk chunk) {
        if (chunks.contains(chunk.getX(), chunk.getZ())) {
            return;
        }
        chunks.put(chunk.getX(), chunk.getZ(), chunk);
    }

    /**
     * Returns the number of loaded chunks in this world
     *
     * @return The number of loaded chunks
     */
    public int numberOfLoadedChunks() {
        return chunks.size();
    }

    /**
     * Returns a list of every chunk in this world
     *
     * @return Every chunk in the world
     */
    public PositionMap<Chunk> getChunks() {
        return chunks;
    }

    /**
     * Returns whether the chunk at the position x, z (chunk coordinates) is loaded or not
     *
     * @param x
     *         The position of the chunk on the x axis
     * @param z
     *         The position of the chunk on the z axis
     * @return Whether the chunk is loaded
     */
    public boolean isLoaded(int x, int z) {
        return chunks.contains(x, z);
    }

    /**
     * Unloads the chunk at the position x, z (chunk coordinates)
     *
     * @param x
     *         The position of the chunk on the x axis
     * @param z
     *         The position of the chunk on the z axis
     */
    public void unloadChunk(int x, int z) {
        Chunk chunk = chunks.remove(x, z);
        if (chunk != null) {
            chunk.unload();
        }
    }

    // The cache helps with the slow performance of maps in javascript
    private int cachedChunkX;
    private int cachedChunkZ;
    private Chunk cachedChunk;

    /**
     * Returns the chunk at the coordinates x, z (chunk coordinates)
     *
     * @param x
     *         The position of the chunk on the x axis
     * @param z
     *         The position of the chunk on the z axis
     * @return The chunk or null
     */
    public Chunk getChunk(int x, int z) {
        if (cachedChunk != null
                && !cachedChunk.isUnloaded()
                && cachedChunkX == x
                && cachedChunkZ == z) {
            return cachedChunk;
        }
        // Replace the cache
        cachedChunkX = x;
        cachedChunkZ = z;
        return cachedChunk = chunks.get(x, z);
    }

    /**
     * Updates the block at the location in the world
     *
     * @param x
     *         The position of the block on the x axis
     * @param y
     *         The position of the block on the y axis
     * @param z
     *         The position of the block on the z axis
     */
    public void updateBlock(int x, int y, int z) {
        if (y < 0 || y > 255) {
            return;
        }
        Chunk chunk = getChunk(x >> 4, z >> 4);
        if (chunk == null) return;
        chunk.updateBlock(x & 0xF, y, z & 0xF);
    }

    /**
     * Returns the block at the location in the world
     *
     * @param x
     *         The position of the block on the x axis
     * @param y
     *         The position of the block on the y axis
     * @param z
     *         The position of the block on the z axis
     * @return The block at the location
     */
    public Block getBlock(int x, int y, int z) {
        if (y < 0 || y > 255) {
            return Blocks.AIR();
        }
        Chunk chunk = getChunk(x >> 4, z >> 4);
        if (chunk == null) return Blocks.NULL_BLOCK();
        return chunk.getBlock(x & 0xF, y, z & 0xF);
    }

    /**
     * Returns the emitted light at the location in the world
     *
     * @param x
     *         The position of the block on the x axis
     * @param y
     *         The position of the block on the y axis
     * @param z
     *         The position of the block on the z axis
     * @return The emitted light at the location
     */
    public int getEmittedLight(int x, int y, int z) {
        if (y < 0 || y > 255) {
            return 0;
        }
        Chunk chunk = getChunk(x >> 4, z >> 4);
        if (chunk == null) return 0;
        return chunk.getEmittedLight(x & 0xF, y, z & 0xF);
    }


    /**
     * Returns the sky light at the location in the world
     *
     * @param x
     *         The position of the block on the x axis
     * @param y
     *         The position of the block on the y axis
     * @param z
     *         The position of the block on the z axis
     * @return The sky light at the location
     */
    public int getSkyLight(int x, int y, int z) {
        if (y < 0 || y > 255) {
            return 15;
        }
        Chunk chunk = getChunk(x >> 4, z >> 4);
        if (chunk == null) return 15;
        return chunk.getSkyLight(x & 0xF, y, z & 0xF);
    }


    /**
     * Returns the biome at the location in the world
     *
     * @param x
     *         The position of the block on the x axis
     * @param z
     *         The position of the block on the z axis
     * @return The biome at the location
     */
    public Biome getBiome(int x, int z) {
        Chunk chunk = getChunk(x >> 4, z >> 4);
        if (chunk == null) return Biome.INVALID;
        return chunk.getBiome(x & 0xF, z & 0xF);
    }

    /**
     * Sets the time of day for this world
     *
     * @param timeOfDay
     *         The time of day
     */
    public void setTimeOfDay(int timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    /**
     * Returns the time of day for this world
     *
     * @return The time of day
     */
    public int getTimeOfDay() {
        return timeOfDay;
    }

    /**
     * Returns this chunks map viewer
     *
     * @return The map viewer
     */
    public IMapViewer getMapViewer() {
        return mapViewer;
    }
}
