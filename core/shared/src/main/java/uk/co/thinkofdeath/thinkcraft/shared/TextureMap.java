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
import uk.co.thinkofdeath.thinkcraft.shared.serializing.Serializable;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.Serializer;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.SerializerArraySerializer;

import java.util.*;

public class TextureMap implements Serializable {

    private Map<Integer, Integer> grassColors = new HashMap<>();
    private Map<Integer, Integer> foliageColors = new HashMap<>();
    private List<Texture> textures = new ArrayList<>();

    public TextureMap() {
    }

    public void copyGrassColormap(Map<Integer, Integer> target) {
        target.putAll(grassColors);
    }

    public void copyFoliageColormap(Map<Integer, Integer> target) {
        target.putAll(foliageColors);
    }

    public void copyTextures(Map<String, Texture> target) {
        for (Texture texture : textures) {
            target.put(texture.getName(), texture);
        }
    }

    public void addTextures(Collection<Texture> values) {
        textures.addAll(values);
    }

    @Override
    public void serialize(Serializer serializer) {
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

        SerializerArraySerializer texs = Platform.workerSerializers().createSerializerArray();
        for (Texture texture : textures) {
            Serializer tex = Platform.workerSerializers().create();
            texture.serialize(tex);
            texs.add(tex);
        }
        serializer.putArray("textures", texs);
    }

    @Override
    public void deserialize(Serializer serializer) {
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

        SerializerArraySerializer texs = (SerializerArraySerializer) serializer.getArray("textures");
        if (texs != null) {
            for (int i = 0; i < texs.size(); i++) {
                Texture texture = new Texture();
                texture.deserialize(texs.get(i));
                textures.add(texture);
            }
        }
    }

    @Override
    public TextureMap create() {
        return new TextureMap();
    }
}
