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

package uk.co.thinkofdeath.thinkcraft.html.client.world;

import uk.co.thinkofdeath.thinkcraft.html.client.render.ChunkRenderObject;
import uk.co.thinkofdeath.thinkcraft.html.client.render.SortableRenderObject;
import uk.co.thinkofdeath.thinkcraft.shared.block.Block;
import uk.co.thinkofdeath.thinkcraft.shared.model.PositionedModel;
import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.UByteBuffer;
import uk.co.thinkofdeath.thinkcraft.shared.worker.ChunkLoadedMessage;
import uk.co.thinkofdeath.thinkcraft.shared.world.Chunk;
import uk.co.thinkofdeath.thinkcraft.shared.world.ChunkSection;

import java.util.ArrayList;
import java.util.List;

public class ClientChunk extends Chunk {

    private final ClientWorld world;
    // Sections of the chunk that need (re)building
    private final boolean[] outdatedSections = new boolean[16];
    private final int[] buildNumbers = new int[16];
    private final ChunkRenderObject[] renderObjects = new ChunkRenderObject[16];
    private int lastBuildNumber = 0;
    private SortableRenderObject[] sortableRenderObjects = new SortableRenderObject[16];

    /**
     * Creates a client-side chunk
     *
     * @param world
     *         The world which owns this chunk
     * @param chunkLoadedMessage
     *         The message containing the data required to load the chunk
     */
    public ClientChunk(ClientWorld world, ChunkLoadedMessage chunkLoadedMessage) {
        super(world, chunkLoadedMessage.getX(), chunkLoadedMessage.getZ());
        this.world = world;

        ChunkLoadedMessage.Section[] sects = chunkLoadedMessage.getSections();
        for (int i = 0; i < 16; i++) {
            if (sects[i] == null) continue;
            ChunkSection section = sections[i] = new ChunkSection(sects[i].getBuffer());
            section.setCount(sects[i].getCount());
            outdatedSections[i] = true;
        }

        for (ChunkLoadedMessage.BlockMapping mapping : chunkLoadedMessage.getMappingList()) {
            Block block = world.getMapViewer().getBlockRegistry().get(mapping.getFullName(), mapping.getRawState());
            idBlockMap.put(mapping.getId(), block);
            blockIdMap.put(block, mapping.getId());
        }

        nextId = chunkLoadedMessage.getNextId();
        biomes = chunkLoadedMessage.getBiomes();
    }

    /**
     * Updates the chunk's state
     */
    public void update() {
        // Check for sections that need rebuilding
        for (int i = 0; i < 16; i++) {
            if (sections[i] != null && outdatedSections[i]) {
                outdatedSections[i] = false;
                world.requestBuild(this, i, ++lastBuildNumber);
            }
        }
    }

    public boolean checkAndSetBuildNumber(int buildNumber, int sectionNumber) {
        if (buildNumber < buildNumbers[sectionNumber] || isUnloaded()) {
            return false;
        }
        buildNumbers[sectionNumber] = buildNumber;
        return true;
    }

    public void setTransparentModels(int i, List<PositionedModel> trans, UByteBuffer transData, int sender) {
        if (sortableRenderObjects[i] != null) {
            sortableRenderObjects[i].setData(null);
        }
        if (sortableRenderObjects[i] != null && trans.size() == 0) {
            world.mapViewer.getRenderer().removeSortable(sortableRenderObjects[i]);
            sortableRenderObjects[i] = null;
            return;
        }
        if (trans.size() > 0) {
            if (sortableRenderObjects[i] == null) {
                sortableRenderObjects[i] = new SortableRenderObject(getX(), i, getZ());
                world.mapViewer.getRenderer().postSortable(sortableRenderObjects[i]);
            }
            ArrayList<PositionedModel> models = sortableRenderObjects[i].getModels();
            models.clear();
            models.addAll(trans);
            sortableRenderObjects[i].setData(transData);
        }
    }

    public void updateAccess(int sectionNumber, int[] accessData) {
        ChunkSection section = sections[sectionNumber];
        for (int j = 0; j < accessData.length; j++) {
            section.getSideAccess()[j] = accessData[j];
        }
    }

    /**
     * Creates and fills this chunk's WebGL buffer
     *
     * @param sectionNumber
     *         The section number
     * @param data
     *         The data
     */
    public void fillBuffer(int sectionNumber, UByteBuffer data, int sender) {
        if (data.size() == 0) {
            if (renderObjects[sectionNumber] != null) {
                world.mapViewer.getRenderer().removeChunkObject(renderObjects[sectionNumber]);
            }
        } else {
            if (renderObjects[sectionNumber] == null) {
                renderObjects[sectionNumber] = new ChunkRenderObject(this, getX(), sectionNumber, getZ());
            }
            world.mapViewer.getRenderer().updateChunkObject(renderObjects[sectionNumber], data);
        }
    }

    /**
     * Flags the chunk for rebuilding
     */
    public void rebuild() {
        for (int i = 0; i < 16; i++) {
            outdatedSections[i] = true;
        }
    }

    @Override
    public void unload() {
        super.unload();
        for (ChunkRenderObject renderObject : renderObjects) {
            if (renderObject != null) {
                world.mapViewer.getRenderer().removeChunkObject(renderObject);
            }
        }
        for (SortableRenderObject sortableRenderObject : sortableRenderObjects) {
            if (sortableRenderObject != null) {
                world.mapViewer.getRenderer().removeSortable(sortableRenderObject);
            }
        }
    }

    public ChunkRenderObject[] getRenderObjects() {
        return renderObjects;
    }
}
