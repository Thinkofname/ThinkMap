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

package uk.co.thinkofdeath.thinkcraft.html.worker.world;

import com.google.gwt.core.client.JsArray;
import elemental.html.ArrayBuffer;
import uk.co.thinkofdeath.thinkcraft.shared.block.Block;
import uk.co.thinkofdeath.thinkcraft.shared.block.BlockRegistry;
import uk.co.thinkofdeath.thinkcraft.shared.block.Blocks;
import uk.co.thinkofdeath.thinkcraft.shared.building.ModelBuilder;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.PositionedModel;
import uk.co.thinkofdeath.thinkcraft.shared.support.DataStream;
import uk.co.thinkofdeath.thinkcraft.shared.support.TUint8Array;
import uk.co.thinkofdeath.thinkcraft.shared.util.IntMap;
import uk.co.thinkofdeath.thinkcraft.shared.worker.ChunkBuildReply;
import uk.co.thinkofdeath.thinkcraft.shared.worker.ChunkLoadedMessage;
import uk.co.thinkofdeath.thinkcraft.shared.worker.WorkerMessage;
import uk.co.thinkofdeath.thinkcraft.shared.world.Chunk;
import uk.co.thinkofdeath.thinkcraft.shared.world.ChunkSection;

import java.util.ArrayList;

public class WorkerChunk extends Chunk {

    private final WorkerWorld world;
    private final boolean reply;

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
        BlockRegistry blockRegistry = world.getMapViewer().getBlockRegistry();

        TUint8Array byteData = TUint8Array.create(data, 0, data.getByteLength());
        DataStream dataStream = DataStream.create(data);

        // Bit mask of what sections actually exist in the chunk
        int sectionMask = dataStream.getUint16(1);

        // Current offset into the buffer
        int offset = 0;

        int count = Integer.bitCount(sectionMask);

        int blockDataOffset = 16 * 16 * 16 * 2 * count;
        int skyDataOffset = blockDataOffset + 16 * 16 * 16 * count;

        for (int i = 0; i < 16; i++) {
            if ((sectionMask & (1 << i)) == 0) {
                continue;
            }
            ChunkSection chunkSection = sections[i] = new ChunkSection();
            int idx = 0;
            for (int oy = 0; oy < 16; oy++) {
                for (int oz = 0; oz < 16; oz++) {
                    for (int ox = 0; ox < 16; ox++) {
                        int id = dataStream.getUint16((offset << 1) + 3);
                        int light = byteData.get(blockDataOffset + offset + 3);
                        int sky = byteData.get(skyDataOffset + offset + 3);
                        offset++;

                        Block block = blockRegistry.get(id >> 4, id & 0xF);
                        if (block == null) {
                            block = Blocks.MISSING_BLOCK();
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

                        if (block != Blocks.AIR()) {
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
        this.reply = reply;
    }

    /**
     * Called after the chunk is added to the world
     */
    public void postAdd() {
        for (int i = 0; i < 16; i++) {
            if (sections[i] == null) {
                continue;
            }
            for (int oy = 0; oy < 16; oy++) {
                for (int oz = -1; oz < 17; oz++) {
                    for (int ox = -1; ox < 17; ox++) {
                        world.updateBlock((getX() << 4) + ox, (i << 4) + oy, (getZ() << 4) + oz);
                    }
                }
            }
        }
        if (reply) {
            sendChunk();
        } else {
            world.worker.postMessage(WorkerMessage.create("null", null, false));
        }
    }

    // Sends the chunk back to the requester
    private void sendChunk() {
        ChunkLoadedMessage message = ChunkLoadedMessage.create(getX(), getZ());
        ArrayList<Object> buffers = new ArrayList<>();
        // Copy sections
        for (int i = 0; i < 16; i++) {
            if (sections[i] != null) {
                TUint8Array data = TUint8Array.create(sections[i].getBuffer());
                buffers.add(data.getBuffer());
                message.setSection(i, sections[i].getCount(), data);
            }
        }

        message.setNextId(nextId);

        IntMap.IntIterator it = idBlockMap.intIterator();
        while (it.hasNext()) {
            int key = it.next();
            message.addIdBlockMapping(key, idBlockMap.get(key));
        }

        world.worker.postMessage(
                WorkerMessage.create("chunk:loaded", message, false),
                buffers.toArray(new Object[buffers.size()]));
    }

    /**
     * Builds the chunk section for rendering and sends it back to the client
     *
     * @param sectionNumber
     *         The section number of build
     * @param buildNumber
     *         The id for this build
     */
    public void build(int sectionNumber, int buildNumber) {
        ModelBuilder builder = new ModelBuilder();
        ModelBuilder transBuilder = new ModelBuilder();
        JsArray<PositionedModel> modelJsArray = (JsArray<PositionedModel>) JsArray.createArray();
        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    Block block = getBlock(x, (sectionNumber << 4) + y, z);
                    if (block.isRenderable()) {
                        Model model = block.getModel();
                        if (!block.isTransparent()) {
                            model.render(builder, x, (sectionNumber << 4) + y, z, this, block);
                        } else {
                            int start = transBuilder.getOffset();
                            model.render(transBuilder, x, (sectionNumber << 4) + y, z, this, block);
                            int length = transBuilder.getOffset() - start;
                            if (length > 0) {
                                modelJsArray.push(PositionedModel.create(
                                                x, (sectionNumber << 4) + y, z, start,
                                                length)
                                );
                            }
                        }
                    }
                }
            }
        }

        TUint8Array data = builder.toTypedArray();
        TUint8Array transData = transBuilder.toTypedArray();
        world.worker.postMessage(WorkerMessage.create("chunk:build",
                ChunkBuildReply.create(getX(), getZ(), sectionNumber, buildNumber, data,
                        transData, modelJsArray),
                false
        ), new Object[]{data.getBuffer(), transData.getBuffer()});
    }
}
