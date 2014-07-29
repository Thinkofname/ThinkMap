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
import com.google.gwt.core.client.JsArrayInteger;
import elemental.html.ArrayBuffer;
import uk.co.thinkofdeath.thinkcraft.html.shared.buffer.JavascriptUByteBuffer;
import uk.co.thinkofdeath.thinkcraft.shared.Face;
import uk.co.thinkofdeath.thinkcraft.shared.block.Block;
import uk.co.thinkofdeath.thinkcraft.shared.block.BlockRegistry;
import uk.co.thinkofdeath.thinkcraft.shared.block.Blocks;
import uk.co.thinkofdeath.thinkcraft.shared.building.ModelBuilder;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.PositionedModel;
import uk.co.thinkofdeath.thinkcraft.shared.platform.Platform;
import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.Buffer;
import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.UByteBuffer;
import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.ViewBuffer;
import uk.co.thinkofdeath.thinkcraft.shared.util.IntMap;
import uk.co.thinkofdeath.thinkcraft.shared.worker.ChunkBuildReply;
import uk.co.thinkofdeath.thinkcraft.shared.worker.ChunkLoadedMessage;
import uk.co.thinkofdeath.thinkcraft.shared.worker.Messages;
import uk.co.thinkofdeath.thinkcraft.shared.world.Biome;
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

        UByteBuffer byteData = JavascriptUByteBuffer.create(data, 0, data.getByteLength());
        ViewBuffer dataStream = Platform.alloc().viewBuffer(byteData, false, 0, byteData.byteSize());

        // Bit mask of what sections actually exist in the chunk
        int sectionMask = dataStream.getUInt16(1);

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
                        int id = dataStream.getUInt16((offset << 1) + 3);
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
        for (int bx = 0; bx < 16; bx++) {
            for (int bz = 0; bz < 16; bz++) {
                setBiome(bx, bz, Biome.getById(byteData.get(skyDataOffset + offset + 3 + bx + bz * 16)));
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
            world.worker.sendMessage(Messages.NULL, false);
        }
    }

    // Sends the chunk back to the requester
    private void sendChunk() {
        ChunkLoadedMessage message = new ChunkLoadedMessage(getX(), getZ(), biomes);
        ArrayList<Buffer> buffers = new ArrayList<>();
        // Copy sections
        for (int i = 0; i < 16; i++) {
            if (sections[i] != null) {
                UByteBuffer data = Platform.alloc().ubyteBuffer(sections[i].getBuffer());
                buffers.add(data);
                message.setSection(i, sections[i].getCount(), data);
            }
        }

        message.setNextId(nextId);

        IntMap.IntIterator it = idBlockMap.intIterator();
        while (it.hasNext()) {
            int key = it.next();
            message.addIdBlockMapping(key, idBlockMap.get(key));
        }

        world.worker.sendMessage(message, false, buffers.toArray(new Buffer[buffers.size()]));
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

        // Compute face access
        updateSideAccess(sectionNumber);

        UByteBuffer data = builder.toTypedArray();
        UByteBuffer transData = transBuilder.toTypedArray();
        JsArrayInteger accessData = (JsArrayInteger) JsArrayInteger.createArray();
        int[] ad = sections[sectionNumber].getSideAccess();
        for (int i = 0; i < ad.length; i++) {
            accessData.set(i, ad[i]);
        }
        world.worker.sendMessage(
                new ChunkBuildReply(
                        getX(), getZ(), sectionNumber, buildNumber,
                        accessData, data,
                        transData, modelJsArray
                ), false, data, transData);
    }

    private void updateSideAccess(int sectionNumber) {
        ChunkSection section = sections[sectionNumber];
        Face[] faces = Face.values();
        for (int i = 0; i < faces.length; i++) {
            section.getSideAccess()[i] = 0;
        }
        boolean[] checked = new boolean[16 * 16 * 16];
        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    int key = keyBlockSection(x, y, z);
                    if (checked[key]) {
                        continue;
                    }
                    Block block = getBlock(x, (sectionNumber << 4) + y, z);
                    if (!block.isRenderable() || !block.isSolid()) {
                        int[] toCheck = new int[16 * 16 * 16];
                        int pointer = 0;
                        toCheck[pointer++] = key;
                        checked[key] = true;
                        boolean[] visitedFaces = new boolean[faces.length];
                        while (pointer > 0) {
                            int val = toCheck[--pointer];
                            int bx = val & 0xF;
                            int bz = (val >> 4) & 0xF;
                            int by = val >> 8;

                            if (bx == 0) {
                                visitedFaces[Face.RIGHT.ordinal()] = true;
                            } else if (bx == 15) {
                                visitedFaces[Face.LEFT.ordinal()] = true;
                            }
                            if (by == 0) {
                                visitedFaces[Face.BOTTOM.ordinal()] = true;
                            } else if (by == 15) {
                                visitedFaces[Face.TOP.ordinal()] = true;
                            }
                            if (bz == 0) {
                                visitedFaces[Face.BACK.ordinal()] = true;
                            } else if (bz == 15) {
                                visitedFaces[Face.FRONT.ordinal()] = true;
                            }

                            int nKey;
                            // X
                            if (bx > 0 && !checked[nKey = keyBlockSection(bx - 1, by, bz)]) {
                                Block b = getBlock(bx - 1, (sectionNumber << 4) + by, bz);
                                if (!b.isRenderable() || !b.isSolid()) {
                                    toCheck[pointer++] = nKey;
                                }
                                checked[nKey] = true;
                            }
                            if (bx < 15 && !checked[nKey = keyBlockSection(bx + 1, by, bz)]) {
                                Block b = getBlock(bx + 1, (sectionNumber << 4) + by, bz);
                                if (!b.isRenderable() || !b.isSolid()) {
                                    checked[nKey] = true;
                                    toCheck[pointer++] = nKey;
                                }
                                checked[nKey] = true;
                            }
                            // Y
                            if (by > 0 && !checked[nKey = keyBlockSection(bx, by - 1, bz)]) {
                                Block b = getBlock(bx, (sectionNumber << 4) + by - 1, bz);
                                if (!b.isRenderable() || !b.isSolid()) {
                                    toCheck[pointer++] = nKey;
                                }
                                checked[nKey] = true;
                            }
                            if (by < 15 && !checked[nKey = keyBlockSection(bx, by + 1, bz)]) {
                                Block b = getBlock(bx, (sectionNumber << 4) + by + 1, bz);
                                if (!b.isRenderable() || !b.isSolid()) {
                                    checked[nKey] = true;
                                    toCheck[pointer++] = nKey;
                                }
                                checked[nKey] = true;
                            }
                            // Z
                            if (bz > 0 && !checked[nKey = keyBlockSection(bx, by, bz - 1)]) {
                                Block b = getBlock(bx, (sectionNumber << 4) + by, bz - 1);
                                if (!b.isRenderable() || !b.isSolid()) {
                                    toCheck[pointer++] = nKey;
                                }
                                checked[nKey] = true;
                            }
                            if (bz < 15 && !checked[nKey = keyBlockSection(bx, by, bz + 1)]) {
                                Block b = getBlock(bx, (sectionNumber << 4) + by, bz + 1);
                                if (!b.isRenderable() || !b.isSolid()) {
                                    checked[nKey] = true;
                                    toCheck[pointer++] = nKey;
                                }
                                checked[nKey] = true;
                            }
                        }
                        for (int i = 0; i < visitedFaces.length; i++) {
                            if (visitedFaces[i]) {
                                Face face = faces[i];
                                for (int j = 0; j < visitedFaces.length; j++) {
                                    if (visitedFaces[j]) {
                                        Face other = faces[j];
                                        section.setSideAccess(face, other, true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static int keyBlockSection(int x, int y, int z) {
        return x | (z << 4) | (y << 8);
    }
}
