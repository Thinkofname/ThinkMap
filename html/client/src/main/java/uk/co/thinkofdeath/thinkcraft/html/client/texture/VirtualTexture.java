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
import elemental.html.CanvasElement;
import elemental.html.CanvasRenderingContext2D;
import elemental.html.ImageElement;

public class VirtualTexture {

    public static final int TEXTURE_SIZE = 512;

    private final int id;
    private final CanvasElement canvas;
    private final CanvasRenderingContext2D ctx;
    private boolean dirty = true;

    private boolean[] usedLocations = new boolean[256 * 256];

    public VirtualTexture(int id) {
        this.id = id;
        canvas = Browser.getDocument().createCanvasElement();
        canvas.setWidth(TEXTURE_SIZE);
        canvas.setHeight(TEXTURE_SIZE);
        ctx = (CanvasRenderingContext2D) canvas.getContext("2d");

        ctx.setFillStyle("#FFFFFF");
        ctx.fillRect(0, 0, TEXTURE_SIZE, TEXTURE_SIZE);

        for (int y = 0; y < TEXTURE_SIZE; y++) {
            ctx.setFillStyle("#" + Integer.toHexString(y & 15) + "00");
            ctx.fillRect(0, y, TEXTURE_SIZE, 1);
        }
    }

    public int getId() {
        return id;
    }

    public CanvasElement getTexture() {
        return canvas;
    }

    public TexturePosition placeTexture(ImageElement imageElement, int px, int py, int size) {
        int rs = (size + 1) / 2;
        for (int x = 0; x < 256; x++) {
            l1:
            for (int y = 0; y < 256; y++) {
                if (usedLocations[x + y * 256]) continue;

                for (int ox = 0; ox < rs; ox++) {
                    for (int oy = 0; oy < rs; oy++) {
                        if (ox + x >= 256 || ox + y >= 256 || usedLocations[(ox + x) + (oy + y) * 256]) {
                            continue l1;
                        }
                    }
                }
                for (int ox = 0; ox < rs; ox++) {
                    for (int oy = 0; oy < rs; oy++) {
                        usedLocations[(ox + x) + (oy + y) * 256] = true;
                    }
                }
                ctx.clearRect(x * 2, y * 2, imageElement.getWidth(), imageElement.getHeight());
                ctx.drawImage(imageElement, px, py, size, size, x * 2, y * 2, size, size);
                return new TexturePosition(x * 2, y * 2, id);
            }
        }
        return null;
    }

    public static class TexturePosition {
        private final int x;
        private final int y;
        private final int id;

        public TexturePosition(int x, int y, int id) {
            this.x = x;
            this.y = y;
            this.id = id;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getId() {
            return id;
        }
    }
}
