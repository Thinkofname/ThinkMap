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

import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.worker.ChunkLoadedMessage;
import uk.co.thinkofdeath.mapviewer.shared.world.Chunk;
import uk.co.thinkofdeath.mapviewer.shared.world.ChunkSection;

public class ClientChunk extends Chunk {

    private final ClientWorld world;

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
        }

        extractChunk(chunkLoadedMessage);
    }

    // TODO: This is really really really ugly, rewrite at some point
    private native void extractChunk(ChunkLoadedMessage chunkLoadedMessage)/*-{
        this.@uk.co.thinkofdeath.mapviewer.shared.world.Chunk::nextId = chunkLoadedMessage.nextId;

        var idmap = this.@uk.co.thinkofdeath.mapviewer.shared.world.Chunk::idBlockMap;
        for (var key in chunkLoadedMessage.idmap) {
            if (chunkLoadedMessage.idmap.hasOwnProperty(key)) {
                var k = parseInt(key);
                var block = this.@uk.co.thinkofdeath.mapviewer.client.world.ClientChunk::_js_toBlock(Ljava/lang/String;)(chunkLoadedMessage.idmap[k]);
                idmap.@java.util.Map::put(Ljava/lang/Object;Ljava/lang/Object;)(k, block)
            }
        }

        var blockMap = this.@uk.co.thinkofdeath.mapviewer.shared.world.Chunk::blockIdMap;
        for (var key in chunkLoadedMessage.blockmap) {
            if (chunkLoadedMessage.blockmap.hasOwnProperty(key)) {
                var block = this.@uk.co.thinkofdeath.mapviewer.client.world.ClientChunk::_js_toBlock(Ljava/lang/String;)(key);
                blockMap.@java.util.Map::put(Ljava/lang/Object;Ljava/lang/Object;)(block, blockMap[k])
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
