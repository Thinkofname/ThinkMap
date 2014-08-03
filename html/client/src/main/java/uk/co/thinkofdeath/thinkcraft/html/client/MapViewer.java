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

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.EntryPoint;
import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.CanvasElement;
import elemental.js.util.Json;
import elemental.xml.XMLHttpRequest;
import uk.co.thinkofdeath.thinkcraft.html.client.feature.FeatureHandler;
import uk.co.thinkofdeath.thinkcraft.html.client.input.InputManager;
import uk.co.thinkofdeath.thinkcraft.html.client.network.Connection;
import uk.co.thinkofdeath.thinkcraft.html.client.render.Camera;
import uk.co.thinkofdeath.thinkcraft.html.client.render.Renderer;
import uk.co.thinkofdeath.thinkcraft.html.client.texture.TextureLoader;
import uk.co.thinkofdeath.thinkcraft.html.client.worker.WorkerPool;
import uk.co.thinkofdeath.thinkcraft.html.client.world.ClientWorld;
import uk.co.thinkofdeath.thinkcraft.html.shared.JavascriptLib;
import uk.co.thinkofdeath.thinkcraft.html.shared.serialize.JsObjectSerializer;
import uk.co.thinkofdeath.thinkcraft.html.shared.settings.ClientSettings;
import uk.co.thinkofdeath.thinkcraft.protocol.ServerPacketHandler;
import uk.co.thinkofdeath.thinkcraft.protocol.packets.KeepAlive;
import uk.co.thinkofdeath.thinkcraft.protocol.packets.ServerSettings;
import uk.co.thinkofdeath.thinkcraft.protocol.packets.SpawnPosition;
import uk.co.thinkofdeath.thinkcraft.protocol.packets.TimeUpdate;
import uk.co.thinkofdeath.thinkcraft.shared.IMapViewer;
import uk.co.thinkofdeath.thinkcraft.shared.Texture;
import uk.co.thinkofdeath.thinkcraft.shared.TextureMap;
import uk.co.thinkofdeath.thinkcraft.shared.block.BlockRegistry;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.platform.Platform;
import uk.co.thinkofdeath.thinkcraft.shared.worker.ClientSettingsMessage;
import uk.co.thinkofdeath.thinkcraft.shared.worker.MessageHandler;
import uk.co.thinkofdeath.thinkcraft.shared.worker.TextureMessage;
import uk.co.thinkofdeath.thinkcraft.shared.world.World;

import java.util.Collection;
import java.util.HashMap;

public class MapViewer implements EntryPoint, EventListener, ServerPacketHandler, IMapViewer {

    /**
     * The max distance (in chunks) the client can see
     */
    public final static int VIEW_DISTANCE = 6;
    private static final int NUMBER_OF_WORKERS = 4;

    private final BlockRegistry blockRegistry = new BlockRegistry(this);
    private final WorkerPool workerPool = new WorkerPool(this, NUMBER_OF_WORKERS);
    private final InputManager inputManager = new InputManager(this);
    private final FeatureHandler featureHandler = new FeatureHandler();
    private final MessageHandler messageHandler = new WorkerMessageHandler(this);
    private final TextureLoader textureLoader = new TextureLoader(this);

    private ClientSettings clientSettings;

    private HashMap<String, Texture> textures = new HashMap<>();
    private int nextTextureId;

    private XMLHttpRequest xhr;
    private int loaded = 0;
    private Connection connection;
    private Renderer renderer;
    private ClientWorld world;
    private boolean shouldUpdateWorld = false;
    private double lastKeepAlive = Duration.currentTimeMillis();

    /**
     * Entry point to the program
     */
    public void onModuleLoad() {
        JavascriptLib.init();
        // Feature detection
        if (!featureHandler.detect()) return;
        // Atlas to look up position of textures
        xhr = Browser.getWindow().newXMLHttpRequest();
        xhr.open("GET", "http://" + getConfigAdddress() + "/resources/blocks.json", true);
        xhr.setOnload(this);
        xhr.send();

        Platform.runRepeated(new Runnable() {
            @Override
            public void run() {
                if (connection == null) return;
                if (Duration.currentTimeMillis() - lastKeepAlive > 1000) {
                    lastKeepAlive = Duration.currentTimeMillis();
                    connection.send(new KeepAlive());
                }
            }
        }, 1000);
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
            // Pre-load models
            Browser.getWindow().getConsole().time("Pre-load");
            getBlockRegistry().init();
            Browser.getWindow().getConsole().timeEnd("Pre-load", null);

            TextureMap tmap = new TextureMap();
            tmap.deserialize(JsObjectSerializer.from(Json.parse((String) xhr.getResponse())));
            tmap.copyGrassColormap(Model.grassBiomeColors);
            tmap.copyFoliageColormap(Model.foliageBiomeColors);
            tmap.addTextures(textures.values());
            // Sync to workers
            getWorkerPool().sendMessage(new TextureMessage(tmap), true);

            inputManager.hook();

            connection = new Connection(getConfigAdddress(), this, null);
        }
    }

    public native String getConfigAdddress()/*-{
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
     * @see uk.co.thinkofdeath.thinkcraft.html.client.render.Renderer#getCamera()
     */
    public Camera getCamera() {
        return renderer.getCamera();
    }

    @Override
    public void handle(ServerSettings serverSettings) {
        clientSettings = ClientSettings.create(serverSettings.areOresHidden());

        // Sync to workers
        getWorkerPool().sendMessage(new ClientSettingsMessage(clientSettings.areOresHidden()), true);
        handleSettings();

        world = new ClientWorld(MapViewer.this);
        renderer = new Renderer(MapViewer.this, (CanvasElement) Browser.getDocument().getElementById("main"));

        textureLoader.loadBlockTexture(getBlockTexture("thinkmap:missing_texture"));
    }

    @Override
    public void handle(SpawnPosition spawnPosition) {
        Camera camera = getCamera();
        camera.setX(spawnPosition.getX());
        camera.setY(spawnPosition.getY() + 2);
        camera.setZ(spawnPosition.getZ());
        shouldUpdateWorld = true;
    }

    @Override
    public void handle(TimeUpdate timeUpdate) {
        if (getWorld() == null) return;
        getWorld().setTimeOfDay(timeUpdate.getCurrentTime());
    }

    private void handleSettings() {
        if (clientSettings.areOresHidden()) {
            Texture replacement = textures.get("minecraft:stone");
            textures.put("minecraft:gold_ore", replacement);
            textures.put("minecraft:iron_ore", replacement);
            textures.put("minecraft:coal_ore", replacement);
            textures.put("minecraft:lapis_ore", replacement);
            textures.put("minecraft:diamond_ore", replacement);
            textures.put("minecraft:redstone_ore", replacement);
            textures.put("minecraft:emerald_ore", replacement);
            textures.put("minecraft:quartz_ore", textures.get("minecraft:netherrack"));
        }
    }

    @Override
    public BlockRegistry getBlockRegistry() {
        return blockRegistry;
    }

    @Override
    public Texture getBlockTexture(String name) {
        if (!textures.containsKey(name)) {
            Texture texture = new Texture(name, nextTextureId++);
            textures.put(name, texture);
        }
        return textures.get(name);
    }

    @Override
    public void markTextureAsUsed(String name) {

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

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    @Override
    public World getWorld() {
        return world;
    }

    public ClientSettings getSettings() {
        return clientSettings;
    }

    public Collection<Texture> getBlocktextures() {
        return textures.values();
    }

    public TextureLoader getTextureLoader() {
        return textureLoader;
    }
}
