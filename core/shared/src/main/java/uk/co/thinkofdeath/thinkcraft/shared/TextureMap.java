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

package uk.co.thinkofdeath.thinkcraft.shared;

import uk.co.thinkofdeath.thinkcraft.shared.platform.Platform;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.IntArraySerializer;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.Serializable;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.Serializer;

import java.util.HashMap;
import java.util.Map;

public class TextureMap implements Serializable {

    private int textureImages;
    private int virtualCount;
    private Map<Integer, Integer> grassColors = new HashMap<>();
    private Map<Integer, Integer> foliageColors = new HashMap<>();
    private Map<String, Texture> textures = new HashMap<>();

    public TextureMap() {
    }

    public int getNumberOfImages() {
        return textureImages;
    }

    public int getNumberVirtuals() {
        return virtualCount;
    }

    public void copyGrassColormap(Map<Integer, Integer> target) {
        target.putAll(grassColors);
    }

    public void copyFoliageColormap(Map<Integer, Integer> target) {
        target.putAll(foliageColors);
    }

    public void copyTextures(HashMap<String, Texture> textures) {
        textures.putAll(this.textures);
    }

    @Override
    public void serialize(Serializer serializer) {
        serializer.putInt("textureImages", textureImages);
        serializer.putInt("virtualCount", virtualCount);
        Serializer gc = Platform.workerSerializers().create();
        for (Map.Entry<Integer, Integer> e : grassColors.entrySet()) {
            gc.putInt(e.getKey().toString(), e.getValue());
        }
        serializer.putSub("grassColormap", gc);
        Serializer fc = Platform.workerSerializers().create();
        for (Map.Entry<Integer, Integer> e : foliageColors.entrySet()) {
            fc.putInt(e.getKey().toString(), e.getValue());
        }
        serializer.putSub("foliageColormap", fc);

        Serializer tex = Platform.workerSerializers().create();
        for (Map.Entry<String, Texture> e : textures.entrySet()) {
            Texture t = e.getValue();
            Serializer texture = Platform.workerSerializers().create();
            IntArraySerializer fr = Platform.workerSerializers().createIntArray();
            for (int i : t.getFrames()) {
                fr.add(i);
            }
            texture.putArray("frames", fr);
            texture.putInt("posX", t.getPosX());
            texture.putInt("posY", t.getPosY());
            texture.putInt("size", t.getSize());
            texture.putInt("width", t.getWidth());
            texture.putInt("frameCount", t.getFrameCount());
            texture.putInt("frameTime", t.getFrameTime());
            texture.putInt("virtualX", t.getVirtualX());
            texture.putInt("virtualY", t.getVirtualY());
            tex.putSub(e.getKey(), texture);
        }
        serializer.putSub("textures", tex);
    }

    @Override
    public void deserialize(Serializer serializer) {
        textureImages = serializer.getInt("textureImages");
        virtualCount = serializer.getInt("virtualCount");
        Serializer gc = serializer.getSub("grassColormap");
        for (String position : gc.keys()) {
            grassColors.put(
                    Integer.valueOf(position),
                    gc.getInt(position)
            );
        }
        Serializer fc = serializer.getSub("foliageColormap");
        for (String position : fc.keys()) {
            foliageColors.put(
                    Integer.valueOf(position),
                    fc.getInt(position)
            );
        }

        Serializer tex = serializer.getSub("textures");
        for (String name : tex.keys()) {
            Serializer texture = tex.getSub(name);
            IntArraySerializer fr = (IntArraySerializer) texture.getArray("frames");
            int[] frames = new int[fr.size()];
            for (int i = 0; i < frames.length; i++) {
                frames[i] = fr.getInt(i);
            }
            textures.put(name,
                    new Texture(
                            name,
                            texture.getInt("posX"),
                            texture.getInt("posY"),
                            texture.getInt("size"),
                            texture.getInt("width"),
                            texture.getInt("frameCount"),
                            frames,
                            texture.getInt("frameTime"),
                            texture.getInt("virtualX"),
                            texture.getInt("virtualY")
                    ));
        }
    }

    @Override
    public TextureMap create() {
        return new TextureMap();
    }
}
