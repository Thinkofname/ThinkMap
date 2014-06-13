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

package uk.co.thinkofdeath.thinkcraft.html.client;

import com.google.gwt.core.client.EntryPoint;
import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.CanvasElement;
import elemental.html.ImageElement;
import elemental.js.util.Json;
import elemental.xml.XMLHttpRequest;
import uk.co.thinkofdeath.thinkcraft.html.client.feature.FeatureHandler;
import uk.co.thinkofdeath.thinkcraft.html.client.input.InputManager;
import uk.co.thinkofdeath.thinkcraft.html.client.network.Connection;
import uk.co.thinkofdeath.thinkcraft.html.client.network.ConnectionHandler;
import uk.co.thinkofdeath.thinkcraft.html.client.render.Camera;
import uk.co.thinkofdeath.thinkcraft.html.client.render.Renderer;
import uk.co.thinkofdeath.thinkcraft.html.client.worker.WorkerPool;
import uk.co.thinkofdeath.thinkcraft.html.client.world.ClientChunk;
import uk.co.thinkofdeath.thinkcraft.html.client.world.ClientWorld;
import uk.co.thinkofdeath.thinkcraft.html.shared.NativeLib;
import uk.co.thinkofdeath.thinkcraft.shared.ClientSettings;
import uk.co.thinkofdeath.thinkcraft.shared.IMapViewer;
import uk.co.thinkofdeath.thinkcraft.shared.Texture;
import uk.co.thinkofdeath.thinkcraft.shared.TextureMap;
import uk.co.thinkofdeath.thinkcraft.shared.block.BlockRegistry;
import uk.co.thinkofdeath.thinkcraft.shared.worker.ChunkBuildReply;
import uk.co.thinkofdeath.thinkcraft.shared.worker.ChunkLoadedMessage;
import uk.co.thinkofdeath.thinkcraft.shared.worker.WorkerMessage;
import uk.co.thinkofdeath.thinkcraft.shared.world.World;

import java.util.ArrayList;
import java.util.HashMap;

public class MapViewer implements EntryPoint, EventListener, ConnectionHandler, IMapViewer {

    /**
     * The max distance (in chunks) the client can see
     */
    public final static int VIEW_DISTANCE = 4;
    private static final int NUMBER_OF_WORKERS = 4;

    private final BlockRegistry blockRegistry = new BlockRegistry(this);
    private final WorkerPool workerPool = new WorkerPool(this, NUMBER_OF_WORKERS);
    private final InputManager inputManager = new InputManager(this);
    private final FeatureHandler featureHandler = new FeatureHandler();

    private ClientSettings clientSettings;

    private HashMap<String, Texture> textures = new HashMap<>();
    private XMLHttpRequest xhr;
    private int loaded = 0;
    private Connection connection;
    private Renderer renderer;
    private ClientWorld world;
    private boolean shouldUpdateWorld = false;

    private int noTextures;
    ArrayList<TextureLoadHandler> earlyTextures = new ArrayList<>();

    /**
     * Entry point to the program
     */
    public void onModuleLoad() {
        NativeLib.init();
        // Feature detection
        if (!featureHandler.detect()) return;
        // Atlas to look up position of textures
        xhr = Browser.getWindow().newXMLHttpRequest();
        xhr.open("GET", "http://" + getConfigAdddress() + "/resources/blocks.json", true);
        xhr.setOnload(this);
        xhr.send();
    }

    /**
     * Internal method
     *
     * @param event
     *         Event
     */
    @Override
    public void handleEvent(Event event) {
        loaded++;
        if (loaded == 1) {
            TextureMap tmap = Json.parse((String) xhr.getResponse());
            tmap.forEach(new TextureMap.Looper() {
                @Override
                public void forEach(String k, Texture v) {
                    textures.put(k, v);
                }
            });
            // Sync to workers
            getWorkerPool().sendMessage("textures", tmap, new Object[0], true);
            noTextures = tmap.getNumberOfImages();
            for (int i = 0; i < tmap.getNumberOfImages(); i++) {
                ImageElement texture = (ImageElement) Browser.getDocument().createElement("img");
                texture.setOnload(new TextureLoadHandler(this, i, texture));
                texture.setCrossOrigin("anonymous");
                texture.setSrc("http://" + getConfigAdddress() + "/resources/blocks_" + i + ".png");
            }
            inputManager.hook();

            connection = new Connection(getConfigAdddress(), this, null);
        }
    }

    public int getNumberOfTextures() {
        return noTextures;
    }

    private native String getConfigAdddress()/*-{
        if ($wnd.MapViewerConfig.serverAddress.indexOf("%SERVERPORT%") != -1) {
            // Running on a custom server but haven't changed the config
            $wnd.MapViewerConfig.serverAddress =
                $wnd.MapViewerConfig.serverAddress.replace("%SERVERPORT%", "23333");
        }
        return $wnd.MapViewerConfig.serverAddress;
    }-*/;

    /**
     * Called every frame by the renderer
     */
    public void tick(double delta) {
        if (shouldUpdateWorld) {
            inputManager.update(delta);
            world.update();
        }
    }

    /**
     * Returns the camera used by the renderer
     *
     * @return the camera
     * @see uk.co.thinkofdeath.thinkcraft.client.render.Renderer#getCamera()
     */
    public Camera getCamera() {
        return renderer.getCamera();
    }

    @Override
    public void onTimeUpdate(int currentTime) {
        getWorld().setTimeOfDay(currentTime);
    }

    @Override
    public void onSetPosition(int x, int y, int z) {
        Camera camera = getCamera();
        camera.setX(x);
        camera.setY(y + 2);
        camera.setZ(z);
        shouldUpdateWorld = true;
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Message: " + message);
        // TODO
    }

    @Override
    public void onSettings(ClientSettings settings) {
        clientSettings = settings;
        // Sync to workers
        getWorkerPool().sendMessage("settings", settings, new Object[0], true);
        handleSettings();

        getBlockRegistry().init();
        world = new ClientWorld(MapViewer.this);
        renderer = new Renderer(MapViewer.this, (CanvasElement) Browser.getDocument().getElementById("main"));
        for (TextureLoadHandler handler : earlyTextures) {
            handler.load();
        }
    }

    private void handleSettings() {
        if (clientSettings.areOresHidden()) {
            Texture replacement = textures.get("stone");
            textures.put("gold_ore", replacement);
            textures.put("iron_ore", replacement);
            textures.put("coal_ore", replacement);
            textures.put("lapis_ore", replacement);
            textures.put("diamond_ore", replacement);
            textures.put("redstone_ore", replacement);
            textures.put("emerald_ore", replacement);
            textures.put("quartz_ore", textures.get("netherrack"));
        }
    }

    @Override
    public BlockRegistry getBlockRegistry() {
        return blockRegistry;
    }

    @Override
    public Texture getTexture(String name) {
        if (!textures.containsKey(name)) {
            System.err.println("Texture not found: " + name);
            return textures.get("missing_texture");
        }
        return textures.get(name);
    }

    /**
     * Returns the connection used by the map viewer
     *
     * @return The connection used
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Returns the worker pool for the map viewer which contains several workers ready for processing data
     *
     * @return The worker pool
     */
    public WorkerPool getWorkerPool() {
        return workerPool;
    }

    /**
     * Returns the client's renderer
     *
     * @return The renderer
     */
    public Renderer getRenderer() {
        return renderer;
    }

    /**
     * Processes a message from a worker
     *
     * @param message
     *         The message to process
     */
    public void handleWorkerMessage(WorkerMessage message, int sender) {
        switch (message.getType()) {
            case "chunk:loaded":
                ChunkLoadedMessage chunkLoadedMessage = (ChunkLoadedMessage) message.getMessage();
                world.addChunk(new ClientChunk(world, chunkLoadedMessage));
                break;
            case "null":
                break;
            case "chunk:build":
                ChunkBuildReply chunkBuildReply = (ChunkBuildReply) message.getMessage();
                ClientChunk chunk = (ClientChunk) world.getChunk(chunkBuildReply.getX(), chunkBuildReply.getZ());
                if (chunk != null) {
                    if (chunk.checkAndSetBuildNumber(chunkBuildReply.getBuildNumber(),
                            chunkBuildReply.getSectionNumber())) {
                        chunk.fillBuffer(
                                chunkBuildReply.getSectionNumber(),
                                chunkBuildReply.getData(),
                                sender);

                        chunk.setTransparentModels(
                                chunkBuildReply.getSectionNumber(),
                                chunkBuildReply.getTrans(),
                                chunkBuildReply.getTransData(),
                                sender);
                    }
                }
                break;
            default:
                System.err.println("Unhandled message: " + message.getType());
                System.err.println(message.getMessage());
                break;
        }
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public ClientSettings getSettings() {
        return clientSettings;
    }
}
