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

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.ArrayBuffer;
import elemental.xml.XMLHttpRequest;
import uk.co.thinkofdeath.thinkcraft.html.client.MapViewer;
import uk.co.thinkofdeath.thinkcraft.shared.support.DataStream;
import uk.co.thinkofdeath.thinkcraft.shared.worker.ChunkBuildMessage;
import uk.co.thinkofdeath.thinkcraft.shared.worker.ChunkLoadMessage;
import uk.co.thinkofdeath.thinkcraft.shared.worker.ChunkUnloadMessage;
import uk.co.thinkofdeath.thinkcraft.shared.world.Chunk;
import uk.co.thinkofdeath.thinkcraft.shared.world.World;

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
                mapViewer.getWorkerPool().sendMessage(
                        new ChunkBuildMessage(task.getChunk().getX(), task.getChunk().getZ(),
                                task.getSectionNumber(),
                                task.getBuildNumber()),
                        false);
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
            mapViewer.getWorkerPool().sendMessage(
                    new ChunkBuildMessage(chunk.getX(), chunk.getZ(), sectionNumber, buildNumber),
                    false);
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

            ArrayList<Chunk> toUnload = new ArrayList<>();
            for (Chunk chunk : getChunks()) {
                if (chunk.getX() < cx - MapViewer.VIEW_DISTANCE
                        || chunk.getX() >= cx + MapViewer.VIEW_DISTANCE
                        || chunk.getZ() < cz - MapViewer.VIEW_DISTANCE
                        || chunk.getZ() >= cz + MapViewer.VIEW_DISTANCE) {
                    toUnload.add(chunk);
                }
            }
            for (Chunk chunk : toUnload) {
                unloadChunk(chunk.getX(), chunk.getZ());
            }

            lastChunkX = cx;
            lastChunkZ = cz;
        }
    }

    /**
     * Triggers an async request to load the chunk. The chunk is forwarded to all workers to be processed before being returned to the client
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
        xmlHttpRequest.open("POST", "http://" + mapViewer.getConnection().getAddress() + "/server/chunk", true);
        xmlHttpRequest.setResponseType("arraybuffer");
        xmlHttpRequest.setOnreadystatechange(new EventListener() {
            @Override
            public void handleEvent(Event evt) {
                if (xmlHttpRequest.getReadyState() != 4) return;
                if (xmlHttpRequest.getStatus() == 200) {
                    // Got the chunk successfully, move on
                    // to processing the chunk
                    ArrayBuffer data = (ArrayBuffer) xmlHttpRequest.getResponse();
                    DataStream dataStream = DataStream.create(data);
                    if (dataStream.getInt8(0) == 0) {
                        loadingChunks.remove(key);
                        return;
                    }
                    mapViewer.getWorkerPool().sendMessage(new ChunkLoadMessage(x, z, data), true);
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
        // Update surrounding chunks
        Chunk other;
        Chunk other2;
        if ((other = getChunk(chunk.getX() - 1, chunk.getZ())) != null
                | (other2 = getChunk(chunk.getX() + 1, chunk.getZ())) != null) {
            for (int z = 0; z < 16; z++) {
                for (int i = 0; i < 16; i++) {
                    if (chunk.hasSection(i)) {
                        for (int y = 0; y < 16; y++) {
                            if (other != null) other.updateBlock(15, (i << 4) + y, z);
                            if (other2 != null) other2.updateBlock(0, (i << 4) + y, z);
                        }
                    }
                }
            }
        }
        if ((other = getChunk(chunk.getX(), chunk.getZ() - 1)) != null
                | (other2 = getChunk(chunk.getX(), chunk.getZ() + 1)) != null) {
            for (int x = 0; x < 16; x++) {
                for (int i = 0; i < 16; i++) {
                    if (chunk.hasSection(i)) {
                        for (int y = 0; y < 16; y++) {
                            if (other != null) other.updateBlock(x, (i << 4) + y, 15);
                            if (other2 != null) other2.updateBlock(x, (i << 4) + y, 0);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void unloadChunk(int x, int z) {
        super.unloadChunk(x, z);
        mapViewer.getWorkerPool().sendMessage(new ChunkUnloadMessage(x, z), true);
        for (int i = 0; i < 16; i++) {
            String buildKey = buildKey(x, z, i);
            if (taskMap.containsKey(buildKey)) {
                BuildTask task = taskMap.remove(buildKey);
                taskList.remove(task);
            }
        }
    }
}
