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

package uk.co.thinkofdeath.mapviewer.client.world;

import uk.co.thinkofdeath.mapviewer.client.render.ChunkRenderObject;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.support.TUint8Array;
import uk.co.thinkofdeath.mapviewer.shared.worker.ChunkBuildMessage;
import uk.co.thinkofdeath.mapviewer.shared.worker.ChunkLoadedMessage;
import uk.co.thinkofdeath.mapviewer.shared.world.Chunk;
import uk.co.thinkofdeath.mapviewer.shared.world.ChunkSection;

public class ClientChunk extends Chunk {

    private final ClientWorld world;
    // Sections of the chunk that need (re)building
    private final boolean[] outdatedSections = new boolean[16];
    private final int[] buildNumbers = new int[16];
    private final ChunkRenderObject[] renderObjects = new ChunkRenderObject[16];
    private int lastBuilderNumber = 0;

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

        for (int i = 0; i < 16; i++) {
            sections[i] = extractSection(chunkLoadedMessage, i);
            outdatedSections[i] = sections[i] != null;
        }

        extractChunk(chunkLoadedMessage);
    }

    /**
     * Updates the chunk's state
     */
    public void update() {
        // Check for sections that need rebuilding
        for (int i = 0; i < 16; i++) {
            if (sections[i] != null && outdatedSections[i]) {
                outdatedSections[i] = false;
                world.mapViewer.getWorkerPool().sendMessage("chunk:build",
                        ChunkBuildMessage.create(getX(), getZ(), i, ++lastBuilderNumber),
                        new Object[0]);
            }
        }
    }

    /**
     * Creates and fills this chunk's WebGL buffer
     *
     * @param buildNumber
     *         The build number
     * @param sectionNumber
     *         The section number
     * @param data
     *         The data
     */
    public void fillBuffer(int buildNumber, int sectionNumber, TUint8Array data) {
        if (buildNumber < buildNumbers[sectionNumber]) {
            return;
        }
        buildNumbers[sectionNumber] = buildNumber;
        if (data.length() == 0) {
            if (renderObjects[sectionNumber] != null) {
                world.mapViewer.getRenderer().removeChunkObject(renderObjects[sectionNumber]);
            }
        } else {
            if (renderObjects[sectionNumber] == null) {
                renderObjects[sectionNumber] = new ChunkRenderObject(getX(), sectionNumber, getZ());
                world.mapViewer.getRenderer().postChunkObject(renderObjects[sectionNumber]);
            }
            renderObjects[sectionNumber].load(data);
        }
    }

    /**
     * Flags the chunk for rebuilding
     */
    public void rebuild() {
        for (int i = 0; i < 16; i++) {
            outdatedSections[i] = sections[i] != null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unload() {
        super.unload();
        for (ChunkRenderObject renderObject : renderObjects) {
            if (renderObject != null) {
                world.mapViewer.getRenderer().removeChunkObject(renderObject);
            }
        }
    }

    private native void extractChunk(ChunkLoadedMessage chunkLoadedMessage)/*-{
        this.@uk.co.thinkofdeath.mapviewer.shared.world.Chunk::nextId = chunkLoadedMessage.nextId;

        var idmap = this.@uk.co.thinkofdeath.mapviewer.shared.world.Chunk::idBlockMap;
        for (var key in chunkLoadedMessage.idmap) {
            if (chunkLoadedMessage.idmap.hasOwnProperty(key)) {
                var k = parseInt(key);
                var block = this.@uk.co.thinkofdeath.mapviewer.client.world.ClientChunk::_js_toBlock(Ljava/lang/String;)(chunkLoadedMessage.idmap[k]);
                idmap[k] = block;
                idmap.$keys.push(k);
            }
        }

        var blockMap = this.@uk.co.thinkofdeath.mapviewer.shared.world.Chunk::blockIdMap;
        for (var key in chunkLoadedMessage.blockmap) {
            if (chunkLoadedMessage.blockmap.hasOwnProperty(key)) {
                var block = this.@uk.co.thinkofdeath.mapviewer.client.world.ClientChunk::_js_toBlock(Ljava/lang/String;)(key);
                blockMap.@java.util.Map::put(Ljava/lang/Object;Ljava/lang/Object;)(block, blockMap[k]);
            }
        }
    }-*/;

    // Short-cut method for JSNI code
    private Block _js_toBlock(String name) {
        return world.getMapViewer().getBlockRegistry().get(name);
    }

    private native ChunkSection extractSection(ChunkLoadedMessage message, int i)/*-{
        var jssection = message.sections[i];
        if (jssection == null) {
            return null;
        }
        var section = @uk.co.thinkofdeath.mapviewer.shared.world.ChunkSection::new(Luk/co/thinkofdeath/mapviewer/shared/support/TUint8Array;)(jssection.buffer);
        section.@uk.co.thinkofdeath.mapviewer.shared.world.ChunkSection::count = jssection.count;
        return section;
    }-*/;
}
