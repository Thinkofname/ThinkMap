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
import uk.co.thinkofdeath.mapviewer.client.network.Connection;
import uk.co.thinkofdeath.mapviewer.client.network.ConnectionHandler;
import uk.co.thinkofdeath.mapviewer.client.render.Camera;
import uk.co.thinkofdeath.mapviewer.client.render.Renderer;
import uk.co.thinkofdeath.mapviewer.shared.IMapViewer;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockRegistry;
import uk.co.thinkofdeath.mapviewer.shared.logging.LoggerFactory;

import java.util.HashMap;

public class MapViewer implements EntryPoint, EventListener, ConnectionHandler, IMapViewer {

    private ImageElement texture;
    private HashMap<String, TextureMap.Texture> textures = new HashMap<>();
    private XMLHttpRequest xhr;
    private Connection connection;
    private Renderer renderer;
    private int loaded = 0;
    private BlockRegistry blockRegistry = new BlockRegistry(this);

    /**
     * Entry point to the program
     */
    public void onModuleLoad() {
        // Texture
        texture = (ImageElement) Browser.getDocument().createElement("img");
        texture.setOnload(this);
        texture.setSrc("block_images/blocks.png");
        // Atlas to look up position of textures in the above image
        xhr = Browser.getWindow().newXMLHttpRequest();
        xhr.open("GET", "block_images/blocks.json", true);
        xhr.setOnload(this);
        xhr.send();
    }

    /**
     * Internal method
     *
     * @param event Event
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

        getBlockRegistry().init();

        // TODO: Use config
        connection = new Connection(getLoggerFactory().getLogger("Server Connection"), "localhost:23333", this, new Runnable() {
            @Override
            public void run() {
                renderer = new Renderer(MapViewer.this, (CanvasElement) Browser.getDocument().getElementById("main"));
            }
        });

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
        camera.setY(y);
        camera.setZ(z);
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

    @Override
    public LoggerFactory getLoggerFactory() {
        return null;
    }
}
