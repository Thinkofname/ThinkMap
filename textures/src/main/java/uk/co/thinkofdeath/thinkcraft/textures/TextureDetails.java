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

package uk.co.thinkofdeath.thinkcraft.textures;

/**
 * Stores information about the name/location of a texture
 */
public class TextureDetails {

    private final String name;
    private final int posX;
    private final int posY;
    private final int size;
    private final int width;
    private final int frameCount;
    private final int[] frames;
    private final int frameTime;
    private final TextureStitcher.Position virtualPosition;

    public TextureDetails(String name, int posX, int posY, int size, int width,
                          int frameCount, int[] frames, int frameTime, TextureStitcher.Position virtualPosition) {
        this.name = name;
        this.posX = posX;
        this.posY = posY;
        this.size = size;
        this.width = width;
        this.frameCount = frameCount;
        this.frames = frames;
        this.frameTime = frameTime;
        this.virtualPosition = virtualPosition;
    }

    public String getName() {
        return name;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getSize() {
        return size;
    }

    public int getWidth() {
        return width;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public int[] getFrames() {
        return frames;
    }

    public int getFrameTime() {
        return frameTime;
    }

    public TextureStitcher.Position getVirtualPosition() {
        return virtualPosition;
    }
}
