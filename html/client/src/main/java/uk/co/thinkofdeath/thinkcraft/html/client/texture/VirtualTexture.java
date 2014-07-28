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
import uk.co.thinkofdeath.thinkcraft.html.client.MapViewer;
import uk.co.thinkofdeath.thinkcraft.shared.Texture;

import java.util.ArrayList;
import java.util.Map;

public class VirtualTexture {

    public static final int TEXTURE_SIZE = 512;

    private final MapViewer mapViewer;
    private final int id;
    private final CanvasElement canvas;
    private final CanvasRenderingContext2D ctx;
    private boolean dirty = true;
    private final Texture[] textures;
    private final Texture[] animatedTextures;
    private final int[] animatedTexturesLastFrame;

    public VirtualTexture(MapViewer mapViewer, int id) {
        this.mapViewer = mapViewer;
        this.id = id;
        canvas = Browser.getDocument().createCanvasElement();
        canvas.setWidth(TEXTURE_SIZE);
        canvas.setHeight(TEXTURE_SIZE);
        ctx = (CanvasRenderingContext2D) canvas.getContext("2d");

        ctx.setFillStyle("#FFFFFF");
        ctx.fillRect(0, 0, TEXTURE_SIZE, TEXTURE_SIZE);

        Map<String, Texture> textures = mapViewer.getTextures();
        final ArrayList<Texture> tx = new ArrayList<>();
        final ArrayList<Texture> animatedTextures = new ArrayList<>();
        // Find relevant textures
        for (Texture t : textures.values()) {
            if (t.getVirtualY() / TEXTURE_SIZE == id) {
                tx.add(t);
                if (t.getFrameCount() > 1) {
                    animatedTextures.add(t);
                }
            }
        }

        this.textures = tx.toArray(new Texture[tx.size()]);
        this.animatedTextures = animatedTextures.toArray(new Texture[animatedTextures.size()]);
        animatedTexturesLastFrame = new int[this.animatedTextures.length];
    }

    /**
     * Grabs textures from the newly loaded texture
     *
     * @param id
     *         The id of the loaded texture
     */
    public void loadTextures(int id) {
        ImageElement imageElement = mapViewer.getImageElements()[id];
        for (Texture texture : textures) {
            if (texture.getPosY() / TEXTURE_SIZE != id) {
                continue;
            }
            // Draw the first frame
            ctx.clearRect(texture.getVirtualX(), texture.getVirtualY() - this.id * TEXTURE_SIZE, texture.getSize(), texture.getSize());
            ctx.drawImage(imageElement, texture.getPosX(), texture.getPosY() - id * TEXTURE_SIZE, texture.getSize(), texture.getSize(),
                    texture.getVirtualX(), texture.getVirtualY() - this.id * TEXTURE_SIZE, texture.getSize(), texture.getSize());

            dirty = true;
        }
    }

    public CanvasElement getTexture() {
        return canvas;
    }

    public void update(int frame) {
        for (int i = 0; i < animatedTextures.length; i++) {
            Texture texture = animatedTextures[i];
            int currentFrame = frame % (texture.getFrames().length * texture.getFrameTime());
            currentFrame = texture.getFrames()[currentFrame / texture.getFrameTime()];
            if (currentFrame != animatedTexturesLastFrame[i]) {
                animatedTexturesLastFrame[i] = currentFrame;

                int total = currentFrame * texture.getSize();
                int posX = texture.getPosX() + (total % texture.getWidth());
                int posY = texture.getPosY() + (total / texture.getWidth()) * texture.getSize();
                int tid = posY / TEXTURE_SIZE;
                ImageElement imageElement = mapViewer.getImageElements()[tid];
                posY -= TEXTURE_SIZE * tid;

                ctx.clearRect(texture.getVirtualX(), texture.getVirtualY() - id * TEXTURE_SIZE, texture.getSize(), texture.getSize());
                ctx.drawImage(imageElement, posX, posY, texture.getSize(), texture.getSize(),
                        texture.getVirtualX(), texture.getVirtualY() - id * TEXTURE_SIZE, texture.getSize(), texture.getSize());
                dirty = true;
            }
        }
        if (dirty) {
            dirty = false;
            mapViewer.getRenderer().updateTexture(id);
        }
    }

    public CanvasRenderingContext2D getCtx() {
        return ctx;
    }
}
