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
import uk.co.thinkofdeath.mapviewer.shared.worker.ChunkBuildMessage;
import uk.co.thinkofdeath.mapviewer.shared.worker.ChunkLoadMessage;
import uk.co.thinkofdeath.mapviewer.shared.worker.ChunkUnloadMessage;
import uk.co.thinkofdeath.mapviewer.shared.world.Chunk;
import uk.co.thinkofdeath.mapviewer.shared.world.World;

import java.util.*;

public class ClientWorld extends World {

    private static final int MAX_WORKER_TASKS = 5;
    final MapViewer mapViewer;
    private boolean firstTick = true;
    private Set<String> loadingChunks = new HashSet<>();
    // Last chunk the camera was in
    private int lastChunkX = 0;
    private int lastChunkZ = 0;

    private Queue<BuildTask> taskList = new LinkedList<>();
    private Map<String, BuildTask> taskMap = new HashMap<>();

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
     * {@inheritDoc}
     */
    @Override
    protected void tick() {
        super.tick();

        for (Chunk chunk : getChunks()) {
            ((ClientChunk) chunk).update();
        }

        if (mapViewer.getWorkerPool().hasFreeWorker(MAX_WORKER_TASKS)) {
            while (!taskList.isEmpty()
                    && mapViewer.getWorkerPool().hasFreeWorker(MAX_WORKER_TASKS)) {
                BuildTask task = taskList.remove();
                taskMap.remove(task.getBuildKey());
                if (task.getChunk().isUnloaded()) {
                    continue;
                }
                mapViewer.getWorkerPool().sendMessage("chunk:build",
                        ChunkBuildMessage.create(task.getChunk().getX(), task.getChunk().getZ(),
                                task.getSectionNumber(),
                                task.getBuildNumber()),
                        new Object[0]
                );
            }
        }
    }

    /**
     * Requests that a section of a chunk is built as soon as possible
     *
     * @param chunk
     *         The chunk that owns the section
     * @param sectionNumber
     *         The position of the section
     * @param buildNumber
     *         The build number for this build
     */
    void requestBuild(ClientChunk chunk, int sectionNumber, int buildNumber) {
        if (mapViewer.getWorkerPool().hasFreeWorker(MAX_WORKER_TASKS)) {
            // Send straight away
            mapViewer.getWorkerPool().sendMessage("chunk:build",
                    ChunkBuildMessage.create(chunk.getX(), chunk.getZ(), sectionNumber,
                            buildNumber),
                    new Object[0]
            );
            return;
        }
        // Queue for later
        String buildKey = buildKey(chunk.getX(), chunk.getZ(), sectionNumber);
        if (taskMap.containsKey(buildKey)) {
            return; // Already queued
        }
        BuildTask task = new BuildTask(chunk, sectionNumber, buildNumber, buildKey);
        taskMap.put(buildKey, task);
        taskList.add(task);
    }

    private static String buildKey(int x, int z, int section) {
        return x + ":" + z + "@" + section;
    }

    /**
     * Updates the world's state
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
            ArrayList<int[]> toLoad = new ArrayList<>();
            for (int x = -MapViewer.VIEW_DISTANCE; x < MapViewer.VIEW_DISTANCE; x++) {
                for (int z = -MapViewer.VIEW_DISTANCE; z < MapViewer.VIEW_DISTANCE; z++) {
                    toLoad.add(new int[]{cx + x, cz + z});
                }
            }
            Collections.sort(toLoad, new ChunkArraySorter(mapViewer.getCamera()));
            for (int[] pos : toLoad) {
                loadChunk(pos[0], pos[1]);
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
        if (loadingChunks.contains(key) || isLoaded(x, z)) {
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
                    if (data.getByteLength() <= 15) {
                        loadingChunks.remove(key);
                        return;
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
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Chunk c = getChunk(chunk.getX() + x, chunk.getZ() + z);
                if (c != null) {
                    ((ClientChunk) c).rebuild();
                }
            }
        }
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
        for (int i = 0; i < 16; i++) {
            String buildKey = buildKey(x, z, i);
            if (taskMap.containsKey(buildKey)) {
                BuildTask task = taskMap.remove(buildKey);
                taskList.remove(task);
            }
        }
    }
}
