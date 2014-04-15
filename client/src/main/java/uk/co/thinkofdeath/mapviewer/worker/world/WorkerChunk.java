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

package uk.co.thinkofdeath.mapviewer.worker.world;

import elemental.html.ArrayBuffer;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.support.DataReader;
import uk.co.thinkofdeath.mapviewer.shared.support.TUint8Array;
import uk.co.thinkofdeath.mapviewer.shared.worker.ChunkLoadedMessage;
import uk.co.thinkofdeath.mapviewer.shared.worker.WorkerMessage;
import uk.co.thinkofdeath.mapviewer.shared.world.Chunk;
import uk.co.thinkofdeath.mapviewer.shared.world.ChunkSection;
import uk.co.thinkofdeath.mapviewer.worker.Worker;

import java.util.Map;

public class WorkerChunk extends Chunk {

    private final WorkerWorld world;

    /**
     * Creates a chunk at the passed position
     *
     * @param x
     *         The x position
     * @param z
     *         The z position
     */
    public WorkerChunk(WorkerWorld world, int x, int z, ArrayBuffer data, boolean reply) {
        super(world, x, z);
        this.world = world;

        TUint8Array byteData = TUint8Array.create(data, 0, data.getByteLength());
        DataReader dataReader = DataReader.create(data);

        // TODO: Rewrite chunk format

        // First 8 bytes are two ints (x, z)
        // but since we already know which chunk
        // we requested we can ignore them

        // Bit mask of what sections actually exist in the chunk
        int sectionMask = dataReader.getUint16(8);

        // Current offset into the buffer
        int offset = 10;

        for (int i = 0; i < 16; i++) {
            if ((sectionMask & (1 << i)) == 0) {
                continue;
            }
            ChunkSection chunkSection = sections[i] = new ChunkSection();
            int idx = 0;
            for (int oy = 0; oy < 16; oy++) {
                for (int oz = 0; oz < 16; oz++) {
                    for (int ox = 0; ox < 16; ox++) {
                        int id = dataReader.getUint16(offset);
                        int dataVal = byteData.get(offset + 2);
                        int light = byteData.get(offset + 3);
                        int sky = byteData.get(offset + 4);
                        offset += 5;

                        Block block = world.getMapViewer().getBlockRegistry().get(id, dataVal);
                        if (block == null) {
                            block = world.getMapViewer().getBlockRegistry().get("thinkmap", "missing_block");
                        }

                        if (!blockIdMap.containsKey(block)) {
                            idBlockMap.put(nextId, block);
                            blockIdMap.put(block, nextId);
                            nextId++;
                        }
                        int chunkBlockId = blockIdMap.get(block);

                        chunkSection.getBlocks().set(idx, chunkBlockId);
                        chunkSection.getBlockLight().set(idx, light);
                        chunkSection.getSkyLight().set(idx, sky);
                        idx++;

                        if (block != world.getMapViewer().getBlockRegistry().get("minecraft:air")) {
                            chunkSection.increaseCount();
                        }
                        if (light != 0) {
                            chunkSection.increaseCount();
                        }
                        if (sky != 15) {
                            chunkSection.increaseCount();
                        }
                    }
                }
            }
        }
        if (reply) {
            sendChunk();
        }
    }

    // Sends the chunk back to the requester
    private void sendChunk() {
        ChunkLoadedMessage message = ChunkLoadedMessage.create(getX(), getZ());

        // Copy sections
        for (int i = 0; i < 16; i++) {
            if (sections[i] != null) {
                message.setSection(i, sections[i].getCount(), sections[i].getBuffer());
            }
        }

        message.setNextId(nextId);

        for (Map.Entry<Integer, Block> e : idBlockMap.entrySet()) {
            message.addIdBlockMapping(e.getKey(), e.getValue());
        }

        for (Map.Entry<Block, Integer> e : blockIdMap.entrySet()) {
            message.addBlockIdMapping(e.getKey(), e.getValue());
        }

        ((Worker) world.getMapViewer()).postMessage(WorkerMessage.create("chunk:loaded", message, false), new Object[0]);
    }
}
