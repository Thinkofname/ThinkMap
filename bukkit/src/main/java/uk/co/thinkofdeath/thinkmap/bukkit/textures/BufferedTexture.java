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

package uk.co.thinkofdeath.thinkmap.bukkit.textures;

import uk.co.thinkofdeath.thinkmap.textures.Texture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class BufferedTexture implements Texture {

    private final BufferedImage image;

    public BufferedTexture(InputStream inputStream) {
        try {
            image = ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedTexture(int w, int h) {
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public int[] getPixels(int x, int y, int w, int h) {
        return image.getRGB(x, y, w, h, null, 0, w);
    }

    @Override
    public void setPixels(int[] data, int x, int y, int w, int h) {
        image.setRGB(x, y, w, h, data, 0, w);
    }

    public BufferedImage getImage() {
        return image;
    }
}
