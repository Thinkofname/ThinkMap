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

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.ArrayBuffer;
import elemental.xml.XMLHttpRequest;
import uk.co.thinkofdeath.mapviewer.client.MapViewer;
import uk.co.thinkofdeath.mapviewer.shared.worker.ChunkLoadMessage;
import uk.co.thinkofdeath.mapviewer.shared.worker.ChunkUnloadMessage;
import uk.co.thinkofdeath.mapviewer.shared.world.Chunk;
import uk.co.thinkofdeath.mapviewer.shared.world.World;

import java.util.HashSet;
import java.util.Set;

public class ClientWorld extends World {

    private final MapViewer mapViewer;
    private boolean firstTick = true;
    private Set<String> loadingChunks = new HashSet<>();
    // Last chunk the camera was in
    private int lastChunkX = 0;
    private int lastChunkZ = 0;

    /**
     * Creates a client world
     *
     * @param mapViewer
     *         The map viewer for this world
     */
    public ClientWorld(MapViewer mapViewer) {
        super(mapViewer);
        this.mapViewer = mapViewer;
    }

    /**
     * Updates the worlds state
     */
    public void update() {
        boolean hasMoved = false;
        if (firstTick) {
            firstTick = false;
            hasMoved = true;
        }

        int cx = (int) mapViewer.getCamera().getX() >> 4;
        int cz = (int) mapViewer.getCamera().getZ() >> 4;
        if (cx != lastChunkX || cz != lastChunkZ) {
            hasMoved = true;
        }

        if (hasMoved) {
            for (int x = -MapViewer.VIEW_DISTANCE; x < MapViewer.VIEW_DISTANCE; x++) {
                for (int z = -MapViewer.VIEW_DISTANCE; z < MapViewer.VIEW_DISTANCE; z++) {
                    loadChunk(cx + x, cz + z);
                }
            }

            for (Chunk chunk : getChunks()) {
                if (chunk.getX() < cx - MapViewer.VIEW_DISTANCE
                        || chunk.getX() >= cx + MapViewer.VIEW_DISTANCE
                        || chunk.getZ() < cz - MapViewer.VIEW_DISTANCE
                        || chunk.getZ() >= cz + MapViewer.VIEW_DISTANCE) {
                    unloadChunk(chunk.getX(), chunk.getZ());
                }
            }

            lastChunkX = cx;
            lastChunkZ = cz;
        }
    }

    /**
     * Triggers an async request to load the chunk. The chunk is forwarded to all workers to be
     * processed before being returned to the client
     *
     * @param x
     *         The chunk x position
     * @param z
     *         The chunk z position
     */
    private void loadChunk(final int x, final int z) {
        final String key = chunkKey(x, z);
        if (loadingChunks.contains(key) || isLoaded(key)) {
            return;
        }
        final XMLHttpRequest xmlHttpRequest = Browser.getWindow().newXMLHttpRequest();
        xmlHttpRequest.open("POST", "http://" + mapViewer.getConnection().getAddress() + "/chunk", true);
        xmlHttpRequest.setResponseType("arraybuffer");
        xmlHttpRequest.setOnreadystatechange(new EventListener() {
            @Override
            public void handleEvent(Event evt) {
                if (xmlHttpRequest.getReadyState() != 4) return;
                if (xmlHttpRequest.getStatus() == 200) {
                    // Got the chunk successfully, move on
                    // to processing the chunk
                    ArrayBuffer data = (ArrayBuffer) xmlHttpRequest.getResponse();
                    // Hacky way of detecting a missing chunk. A 404
                    // error was used in the past but you can't always
                    // catch the error before the browser does.
                    // TODO: Support errors better end the sending
                    // format changes
                    if (data.getByteLength() == 15) {
                        loadingChunks.remove(key);
                    }
                    mapViewer.getWorkerPool().sendMessage("chunk:load",
                            ChunkLoadMessage.create(x, z, data), new Object[0], true);
                } else {
                    // Request failed (e.g. non-existing chunk)
                    // remove from the loadingChunks set so
                    // that it may be tried again
                    loadingChunks.remove(key);
                }
            }
        });
        xmlHttpRequest.send(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addChunk(Chunk chunk) {
        super.addChunk(chunk);
        loadingChunks.remove(chunkKey(chunk.getX(), chunk.getZ()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unloadChunk(int x, int z) {
        super.unloadChunk(x, z);
        mapViewer.getWorkerPool().sendMessage("chunk:unload",
                ChunkUnloadMessage.create(x, z),
                new Object[0],
                true);
    }
}
