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

import elemental.client.Browser;
import elemental.html.CanvasElement;
import elemental.html.CanvasRenderingContext2D;
import elemental.html.ImageData;
import uk.co.thinkofdeath.thinkcraft.html.client.texture.VirtualTexture;
import uk.co.thinkofdeath.thinkcraft.shared.Texture;
import uk.co.thinkofdeath.thinkcraft.shared.support.TUint8Array;

public class TexturePreProcessor {

    private static TextureLoadHandler grassOverlay;
    private static TextureLoadHandler grass;

    public static void process(MapViewer mapViewer, int id, TextureLoadHandler textureLoadHandler) {
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
            canvasElement.setWidth(VirtualTexture.TEXTURE_SIZE);
            canvasElement.setHeight(VirtualTexture.TEXTURE_SIZE);
            CanvasRenderingContext2D ctx = (CanvasRenderingContext2D) canvasElement.getContext("2d");
            ctx.drawImage(grassOverlay.imageElement, 0, 0);

            Texture overlay = mapViewer.getTexture("grass_side_overlay");
            Texture g = mapViewer.getTexture("grass_side");

            ImageData overlayData = ctx.getImageData(
                    overlay.getPosX(),
                    overlay.getPosY() - grassOverlay.id * VirtualTexture.TEXTURE_SIZE,
                    overlay.getSize(),
                    overlay.getSize());
            ctx.drawImage(grass.imageElement, 0, 0);

            ImageData data = ctx.getImageData(
                    g.getPosX(),
                    g.getPosY() - grass.id * VirtualTexture.TEXTURE_SIZE,
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

            int vid = g.getVirtualY() / VirtualTexture.TEXTURE_SIZE;
            VirtualTexture virtualTexture = mapViewer.getVirtualTextures()[vid];
            virtualTexture.getCtx().putImageData(data, g.getVirtualX(), g.getVirtualY() - vid * VirtualTexture.TEXTURE_SIZE);

            grass = null;
            grassOverlay = null;
        }
    }
}
