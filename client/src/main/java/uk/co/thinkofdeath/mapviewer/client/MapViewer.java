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

package uk.co.thinkofdeath.mapviewer.client;

import com.google.gwt.core.client.EntryPoint;
import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.CanvasElement;
import elemental.html.ImageElement;
import elemental.js.util.Json;
import elemental.xml.XMLHttpRequest;
import uk.co.thinkofdeath.mapviewer.client.input.InputManager;
import uk.co.thinkofdeath.mapviewer.client.network.Connection;
import uk.co.thinkofdeath.mapviewer.client.network.ConnectionHandler;
import uk.co.thinkofdeath.mapviewer.client.render.Camera;
import uk.co.thinkofdeath.mapviewer.client.render.Renderer;
import uk.co.thinkofdeath.mapviewer.client.worker.WorkerPool;
import uk.co.thinkofdeath.mapviewer.client.world.ClientChunk;
import uk.co.thinkofdeath.mapviewer.client.world.ClientWorld;
import uk.co.thinkofdeath.mapviewer.shared.IMapViewer;
import uk.co.thinkofdeath.mapviewer.shared.TextureMap;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockRegistry;
import uk.co.thinkofdeath.mapviewer.shared.logging.LoggerFactory;
import uk.co.thinkofdeath.mapviewer.shared.worker.ChunkBuildReply;
import uk.co.thinkofdeath.mapviewer.shared.worker.ChunkLoadedMessage;
import uk.co.thinkofdeath.mapviewer.shared.worker.WorkerMessage;

import java.util.HashMap;

public class MapViewer implements EntryPoint, EventListener, ConnectionHandler, IMapViewer {

    public final static int VIEW_DISTANCE = 4;

    private final LoggerFactory loggerFactory = new ClientLogger(ClientLogger.DEBUG);
    private final BlockRegistry blockRegistry = new BlockRegistry(this);
    private final WorkerPool workerPool = new WorkerPool(this, 4);
    private final InputManager inputManager = new InputManager(this);
    private ImageElement texture;
    private HashMap<String, TextureMap.Texture> textures = new HashMap<>();
    private XMLHttpRequest xhr;
    private int loaded = 0;
    private Connection connection;
    private Renderer renderer;
    private ClientWorld world;
    private boolean shouldUpdateWorld = false;

    /**
     * Entry point to the program
     */
    public void onModuleLoad() {
        // Texture
        texture = (ImageElement) Browser.getDocument().createElement("img");
        texture.setOnload(this);
        texture.setSrc("../block_images/blocks.png");
        // Atlas to look up position of textures in the above image
        xhr = Browser.getWindow().newXMLHttpRequest();
        xhr.open("GET", "../block_images/blocks.json", true);
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
        if (loaded != 2) return;

        TextureMap tmap = Json.parse((String) xhr.getResponse());
        tmap.forEach(new TextureMap.Looper() {
            @Override
            public void forEach(String k, TextureMap.Texture v) {
                textures.put(k, v);
            }
        });
        // Sync to workers
        getWorkerPool().sendMessage("textures", tmap, new Object[0], true);

        getBlockRegistry().init();
        inputManager.hook();

        String host = getConfigHost();
        String port = getConfigPort();
        connection = new Connection(getLoggerFactory().getLogger("Server Connection"),
                host + ":" + port,
                this, new Runnable() {
            @Override
            public void run() {
                renderer = new Renderer(MapViewer.this, (CanvasElement) Browser.getDocument().getElementById("main"));
                world = new ClientWorld(MapViewer.this);
            }
        }
        );
    }

    private native String getConfigHost()/*-{
        return $wnd.MapViewerConfig.hostname;
    }-*/;

    private native String getConfigPort()/*-{
        return $wnd.MapViewerConfig.port == "%SERVERPORT%" ? "23333" : $wnd.MapViewerConfig.port;
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
     * Returns the ImageElement containing the texture for blocks
     *
     * @return The block texture element
     */
    public ImageElement getBlockTexture() {
        return texture;
    }


    /**
     * Returns the camera used by the renderer
     *
     * @return the camera
     * @see uk.co.thinkofdeath.mapviewer.client.render.Renderer#getCamera()
     */
    public Camera getCamera() {
        return renderer.getCamera();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTimeUpdate(int currentTime) {
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSetPosition(int x, int y, int z) {
        Camera camera = getCamera();
        camera.setX(x);
        camera.setY(y + 2);
        camera.setZ(z);
        shouldUpdateWorld = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessage(String message) {
        System.out.println("Message: " + message);
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BlockRegistry getBlockRegistry() {
        return blockRegistry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TextureMap.Texture getTexture(String name) {
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
     * Returns the worker pool for the map viewer which contains several workers ready for
     * processing data
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
    public void handleWorkerMessage(WorkerMessage message) {
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
                    chunk.fillBuffer(chunkBuildReply.getBuildNumber(),
                            chunkBuildReply.getSectionNumber(), chunkBuildReply.getData());
                }
                break;
            default:
                getLoggerFactory().getLogger("WorkerMessage").debug("Unhandled message: " +
                        message.getType());
                getLoggerFactory().getLogger("WorkerMessage").debug(message.getMessage());
                break;
        }
    }
}
