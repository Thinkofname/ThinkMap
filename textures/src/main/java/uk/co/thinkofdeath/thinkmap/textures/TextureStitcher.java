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

package uk.co.thinkofdeath.thinkmap.textures;

public class TextureStitcher {

    private final TextureProvider provider;
    private final TextureFactory textureFactory;

    public TextureStitcher(TextureProvider provider, TextureFactory textureFactory) {
        this.provider = provider;
        this.textureFactory = textureFactory;
    }

    public StitchResult stitch() {
        StitchResult result = new StitchResult();

        Texture output = textureFactory.create(512, 512);
        result.output = output;
        int currentTextureId = 0;

        for (String name : provider.getTextures()) {
            Texture texture = provider.getTexture(name);

            if (texture.getWidth() != 16 || texture.getHeight() != 16) {
                TextureMetadata metadata = provider.getMetadata(name);

                int[] frames = metadata.getFrames();
                if (frames == null) {
                    // Work out from texture size
                    frames = new int[texture.getHeight() / texture.getWidth()];
                    for (int i = 0; i < frames.length; i++) {
                        frames[i] = i;
                    }
                }
                int start = currentTextureId;
                for (int frame : frames) {
                    for (int i = 0; i < metadata.getFrameTime(); i++) {
                        int[] data = texture.getPixels(0, 16 * frame, 16, 16);
                        output.setPixels(data, (currentTextureId % 32) * 16, (currentTextureId / 32) * 16,
                                16, 16);
                        currentTextureId++;
                    }
                }
                result.details.put(name, new TextureDetails(name, start, currentTextureId - 1));
                continue;
            }

            int[] data = texture.getPixels(0, 0, 16, 16);
            output.setPixels(data, (currentTextureId % 32) * 16, (currentTextureId / 32) * 16,
                    16, 16);
            result.details.put(name, new TextureDetails(name, currentTextureId, currentTextureId));
            currentTextureId++;
        }

        return result;
    }
}
