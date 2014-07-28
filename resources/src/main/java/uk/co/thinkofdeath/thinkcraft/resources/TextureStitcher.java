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

package uk.co.thinkofdeath.thinkcraft.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class TextureStitcher {

    private static final int TEXTURE_SIZE = 512;

    private final ResourceProvider provider;
    private final TextureFactory textureFactory;
    private final ArrayList<StitchedTexture> textures = new ArrayList<>();
    private final ArrayList<StitchedTexture> virtualTextures = new ArrayList<>();

    public TextureStitcher(ResourceProvider provider, TextureFactory textureFactory) {
        this.provider = provider;
        this.textureFactory = textureFactory;
    }

    public StitchResult stitch() {
        StitchResult result = new StitchResult();

        String[] textureNames = provider.getTextures();
        Arrays.sort(textureNames, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                boolean o1m = provider.getMetadata(o1) != null;
                boolean o2m = provider.getMetadata(o2) != null;
                if (o1m && !o2m) {
                    return -1;
                } else if (!o1m && o2m) {
                    return 1;
                }
                return 0;
            }
        });

        for (String name : textureNames) {
            Texture texture = provider.getTexture(name);
            if (provider.getMetadata(name) != null) {
                TextureMetadata metadata = provider.getMetadata(name);

                int frames = texture.getHeight() / texture.getWidth();
                int size = texture.getWidth();

                Position position = null;
                TextureDetails details = null;
                int columns = 0;

                main:
                for (int t = 0; t <= 1; t++) {
                    for (columns = frames; columns > 0; columns--) {
                        int width = columns * size;
                        if (width > TEXTURE_SIZE) {
                            continue;
                        }
                        int height = (int) Math.ceil((float) frames / columns)
                                * size;
                        if (height > TEXTURE_SIZE) {
                            break;
                        }
                        position = getFree(width, height, t == 1);
                        if (position != null) {
                            int[] frs = metadata.getFrames();
                            if (frs == null) {
                                frs = new int[frames];
                                for (int i = 0; i < frames; i++) {
                                    frs[i] = i;
                                }
                            }
                            Position virtualPosition = getVirtual(size, size);
                            details = new TextureDetails(name, position.x, position.y, size,
                                    width, frames, frs, metadata.getFrameTime(), virtualPosition);
                            break main;
                        }
                    }
                }

                if (position == null) {
                    System.out.println("Failed to place texture: " + name);
                    continue;
                }
                for (int frame = 0; frame < frames; frame++) {
                    int[] data = texture.getPixels(0, size * frame, size, size);
                    putTexture(new Position(position.x + (frame % columns) * size,
                                    position.y + (frame / columns) * size),
                            data, size,
                            size
                    );
                }
                result.details.put(name, details);
                continue;
            }
            Position position = getFree(texture.getWidth(), texture.getHeight(), true);
            if (position == null) {
                System.out.println("Failed to place texture: " + name);
                continue;
            }
            putTexture(position, texture);
            Position virtualPosition = getVirtual(texture.getWidth(), texture.getHeight());
            result.details.put(name, new TextureDetails(name, position.x, position.y,
                    texture.getWidth(), texture.getWidth(), 1, new int[]{0}, -1, virtualPosition));
        }

        result.output = new Texture[textures.size()];
        for (int i = 0; i < textures.size(); i++) {
            result.output[i] = textures.get(i).texture;
        }
        result.virtualCount = virtualTextures.size();
        return result;
    }


    private Position getVirtual(int width, int height) {
        if (width > TEXTURE_SIZE || height > TEXTURE_SIZE) {
            throw new IllegalArgumentException("Texture too big");
        }
        Position position = null;
        int i = 0;
        for (StitchedTexture texture : virtualTextures) {
            position = texture.getFree(width, height, true);
            if (position != null) {
                position = new Position(position.x, position.y + i * TEXTURE_SIZE);
                break;
            }
            i++;
        }
        if (position == null) {
            StitchedTexture texture = new StitchedTexture(textureFactory.create(
                    TEXTURE_SIZE, TEXTURE_SIZE));
            position = texture.getFree(width, height, true);
            position = new Position(position.x, position.y + virtualTextures.size() * TEXTURE_SIZE);
            virtualTextures.add(texture);
        }
        return position;
    }

    private void putTexture(Position position, Texture t) {
        putTexture(position, t.getPixels(0, 0, t.getWidth(), t.getHeight()),
                t.getWidth(), t.getHeight());
    }

    private void putTexture(Position position, int[] data, int width, int height) {
        int tid = position.y / TEXTURE_SIZE;
        StitchedTexture texture = textures.get(tid);
        texture.putTexture(new Position(position.x, position.y - tid * TEXTURE_SIZE), data,
                width, height);
    }

    private Position getFree(int width, int height, boolean createNew) {
        if (width > TEXTURE_SIZE || height > TEXTURE_SIZE) {
            throw new IllegalArgumentException("Texture too big");
        }
        Position position = null;
        int i = 0;
        for (StitchedTexture texture : textures) {
            position = texture.getFree(width, height, false);
            if (position != null) {
                position = new Position(position.x, position.y + i * TEXTURE_SIZE);
                break;
            }
            i++;
        }
        if (position == null && createNew) {
            StitchedTexture texture = new StitchedTexture(textureFactory.create(
                    TEXTURE_SIZE, TEXTURE_SIZE));
            position = texture.getFree(width, height, false);
            position = new Position(position.x, position.y + textures.size() * TEXTURE_SIZE);
            textures.add(texture);
        }
        return position;
    }

    private static class StitchedTexture {

        private final Texture texture;

        private final boolean[] usedPixels;

        public StitchedTexture(Texture texture) {
            this.texture = texture;
            usedPixels = new boolean[texture.getWidth() * texture.getHeight()];
        }

        public Position getFree(int width, int height, boolean mark) {
            for (int x = 0; x < texture.getWidth(); x++) {
                main:
                for (int y = 0; y < texture.getHeight(); y++) {
                    if (!usedPixels[x + y * texture.getWidth()]) {
                        for (int x2 = 0; x2 < width; x2++) {
                            for (int y2 = 0; y2 < height; y2++) {
                                if ((x + x2) >= TEXTURE_SIZE
                                        || (y + y2) >= TEXTURE_SIZE
                                        || usedPixels[(x + x2) + (y + y2) * texture.getWidth()]) {
                                    continue main;
                                }
                            }
                        }
                        if (mark) {
                            markLocation(x, y, width, height);
                        }
                        return new Position(x, y);
                    }
                }
            }
            return null;
        }

        public void putTexture(Position position, int[] data, int width, int height) {
            texture.setPixels(data, position.x, position.y, width, height);
            markLocation(position.x, position.y, width, height);
        }

        private void markLocation(int px, int py, int width, int height) {
            for (int x = px; x < px + width; x++) {
                for (int y = py; y < py + height; y++) {
                    if (usedPixels[x + y * texture.getWidth()]) {
                        throw new RuntimeException("Double used location (" + x + "," + y + ")");
                    }
                    usedPixels[x + y * texture.getWidth()] = true;
                }
            }
        }
    }

    public static class Position {
        private final int x;
        private final int y;

        public Position(final int x, final int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}
