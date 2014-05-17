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

import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.ImageElement;

class TextureLoadHandler implements EventListener {
    private final MapViewer mapViewer;
    private final int id;
    private final ImageElement imageElement;

    public TextureLoadHandler(MapViewer mapViewer, int id, ImageElement imageElement) {
        this.mapViewer = mapViewer;
        this.id = id;
        this.imageElement = imageElement;
    }

    @Override
    public void handleEvent(Event event) {
        if (mapViewer.getRenderer() == null) {
            mapViewer.earlyTextures.add(this);
        } else {
            load();
        }
    }

    public void load() {
        mapViewer.getRenderer().loadBlockTexture(id, imageElement);
    }
}
