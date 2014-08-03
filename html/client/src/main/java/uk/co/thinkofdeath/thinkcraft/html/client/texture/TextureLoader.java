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

package uk.co.thinkofdeath.thinkcraft.html.client.texture;

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.ImageElement;
import elemental.json.Json;
import elemental.xml.XMLHttpRequest;
import uk.co.thinkofdeath.thinkcraft.html.client.MapViewer;
import uk.co.thinkofdeath.thinkcraft.html.shared.serialize.JsObjectSerializer;
import uk.co.thinkofdeath.thinkcraft.shared.Texture;

import java.util.HashSet;

public class TextureLoader {

    private final HashSet<String> loaded = new HashSet<>();
    private final MapViewer mapviewer;

    public TextureLoader(MapViewer mapviewer) {
        this.mapviewer = mapviewer;
    }

    public void loadBlockTexture(final Texture texture) {
        if (loaded.contains(texture.getName())) {
            return;
        }
        loaded.add(texture.getName());
        String[] args = texture.getName().split(":");
        String plugin = args[0];
        String name = args[1];

        final ImageElement imageElement = (ImageElement) Browser.getDocument().createElement("img");
        imageElement.setOnload(new EventListener() {
            @Override
            public void handleEvent(Event event) {
                texture.setLoaded(true);
                if (imageElement.getWidth() != imageElement.getHeight()) {
                    VirtualTexture.TexturePosition[] positions = mapviewer.getRenderer().placeTextures(texture, imageElement);
                    loadAnimationInfo(texture, positions);
                }
                mapviewer.getRenderer().placeTexture(texture, imageElement);
            }
        });
        imageElement.setCrossOrigin("anonymous");
        imageElement.setSrc("http://" + mapviewer.getConfigAdddress() + "/resources/assets/" + plugin + "/textures/blocks/" + name + ".png");
    }

    private void loadAnimationInfo(final Texture texture, final VirtualTexture.TexturePosition[] positions) {
        String[] args = texture.getName().split(":");
        String plugin = args[0];
        String name = args[1];

        final XMLHttpRequest xhr = Browser.getWindow().newXMLHttpRequest();
        xhr.open("GET", "http://" + mapviewer.getConfigAdddress() + "/resources/assets/" + plugin + "/textures/blocks/" + name + ".png.mcmeta", true);
        xhr.setOnload(new EventListener() {
            @Override
            public void handleEvent(Event event) {
                JsObjectSerializer resp = JsObjectSerializer.from(Json.parse((String) xhr.getResponse()));
                TextureMetadata metadata = new TextureMetadata();
                metadata.deserialize(resp);
                mapviewer.getRenderer().addAnimatedTexture(
                        texture,
                        metadata,
                        positions
                );
            }
        });
        xhr.send();
    }
}
