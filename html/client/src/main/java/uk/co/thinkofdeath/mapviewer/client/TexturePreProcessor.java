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

import elemental.client.Browser;
import elemental.html.CanvasElement;
import elemental.html.CanvasRenderingContext2D;
import elemental.html.ImageData;
import elemental.html.ImageElement;
import uk.co.thinkofdeath.mapviewer.shared.Texture;
import uk.co.thinkofdeath.mapviewer.shared.support.TUint8Array;

public class TexturePreProcessor {

    private static TextureLoadHandler grassOverlay;
    private static TextureLoadHandler grass;

    public static void process(MapViewer mapViewer, int id, ImageElement imageElement, TextureLoadHandler textureLoadHandler) {
        // Grass
        if ((mapViewer.getTexture("grass_side_overlay").getPosY() / 1024) == id) {
            grassOverlay = textureLoadHandler;
        }
        if ((mapViewer.getTexture("grass_side").getPosY() / 1024) == id) {
            grass = textureLoadHandler;
            if (grassOverlay == null) {
                return;
            }
        }
        if (grassOverlay != null && grass != null) {
            System.out.println("Processing");
            CanvasElement canvasElement = Browser.getDocument().createCanvasElement();
            canvasElement.setWidth(1024);
            canvasElement.setHeight(1024);
            CanvasRenderingContext2D ctx = (CanvasRenderingContext2D) canvasElement.getContext("2d");
            ctx.drawImage(grassOverlay.imageElement, 0, 0);

            Texture overlay = mapViewer.getTexture("grass_side_overlay");
            Texture g = mapViewer.getTexture("grass_side");

            ImageData overlayData = ctx.getImageData(
                    overlay.getPosX(),
                    overlay.getPosY() - grassOverlay.id * 1024,
                    overlay.getSize(),
                    overlay.getSize());
            ctx.drawImage(grass.imageElement, 0, 0);

            ImageData data = ctx.getImageData(
                    g.getPosX(),
                    g.getPosY() - grass.id * 1024,
                    g.getSize(),
                    g.getSize());

            TUint8Array darr = (TUint8Array) data.getData();

            for (int x = 0; x < g.getSize(); x++) {
                for (int y = 0; y < g.getSize(); y++) {
                    int o = overlayData.getData().intAt((x + y * overlay.getSize()) * 4 + 3);
                    if (o != 0) {
                        darr.set((x + y * overlay.getSize()) * 4 + 3, 0);
                    }
                }
            }
            ctx.putImageData(
                    data,
                    g.getPosX(),
                    g.getPosY() - grass.id * 1024);

            mapViewer.getRenderer().loadBlockTexture(grass.id, (ImageElement) canvasElement);
            if (grass != grassOverlay) {
                mapViewer.getRenderer().loadBlockTexture(grassOverlay.id, grassOverlay.imageElement);
            }
            grass = null;
            grassOverlay = null;
            return;
        }
        mapViewer.getRenderer().loadBlockTexture(id, imageElement);
    }
}
