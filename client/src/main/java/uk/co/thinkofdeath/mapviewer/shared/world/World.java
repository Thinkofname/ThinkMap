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

import elemental.util.Timer;
import uk.co.thinkofdeath.mapviewer.shared.IMapViewer;

import java.util.HashMap;

public abstract class World {

    private int timeOfDay = 6000;
    private HashMap<String, Chunk> chunks = new HashMap<>();
    private final IMapViewer mapViewer;

    protected World(IMapViewer mapViewer) {
        this.mapViewer = mapViewer;
        new Timer() {
            @Override
            public void run() {
                tick();
            }
        }.scheduleRepeating(1000 / 20);
    }

    /**
     * Returns a String that can be used to identify a chunk
     *
     * @param x The position of the chunk on the x axis
     * @param z The position of the chunk on the z axis
     * @return The string key
     */
    public static String chunkKey(int x, int z) {
        return x + ":" + z;
    }

    /**
     * Tick the world and its chunks. Should be called at most
     * 20 times a second
     */
    private void tick() {
        timeOfDay = (timeOfDay + 1) % 24000;
    }

    /**
     * Adds the chunk to the world if it isn't already loaded
     *
     * @param chunk The chunk to add
     */
    public void addChunk(Chunk chunk) {
        String key = chunkKey(chunk.getX(), chunk.getZ());
        if (chunks.containsKey(key)) {
            return;
        }
        chunks.put(key, chunk);
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
     * Returns this chunks map viewer
     * @return The map viewer
     */
    public IMapViewer getMapViewer() {
        return mapViewer;
    }
}
