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

package uk.co.thinkofdeath.thinkcraft.resources.mojang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import uk.co.thinkofdeath.thinkcraft.resources.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipResourceProvider implements ResourceProvider {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(TextureMetadata.class, new TextureMetadataDeserializer())
            .registerTypeAdapter(TextureMetadataAnimation.class, new TextureMetadataAnimationDeserializer())
            .create();

    private static final HashMap<String, String> whitelistedTextures = new HashMap<String, String>() {{
        put("assets/minecraft/textures/entity/chest/normal.png", "chest_normal");
        put("assets/minecraft/textures/entity/chest/ender.png", "chest_ender");
        put("assets/minecraft/textures/entity/chest/trapped.png", "chest_trapped");
    }};

    private static final HashMap<String, String> whitelistedResources = new HashMap<String, String>() {{
        put("assets/minecraft/textures/colormap/grass.png", "grass_colormap");
        put("assets/minecraft/textures/colormap/foliage.png", "foliage_colormap");
    }};

    private final ArrayList<String> textureNames = new ArrayList<>();
    private final HashMap<String, Texture> textures = new HashMap<>();
    private final HashMap<String, TextureMetadata> metadata = new HashMap<>();
    private final HashMap<String, byte[]> resources = new HashMap<>();

    public ZipResourceProvider(InputStream inputStream, TextureFactory factory) {
        fromStream(inputStream, factory);
    }

    void fromStream(InputStream inputStream, TextureFactory factory) {
        try {
            try (ZipInputStream in = new ZipInputStream(inputStream)) {
                ZipEntry entry;
                while ((entry = in.getNextEntry()) != null) {
                    if (entry.getName().startsWith("assets/minecraft/textures/blocks/")) {
                        if (entry.getName().endsWith(".png")) {
                            String name = entry.getName().substring(
                                    "assets/minecraft/textures/blocks/".length(),
                                    entry.getName().length() - 4
                            );
                            textureNames.add(name);
                            textures.put(name, factory.fromInputStream(new NoCloseStream(in)));
                        } else if (entry.getName().endsWith(".png.mcmeta")) {
                            TextureMetadata metadata = gson.fromJson(
                                    new InputStreamReader(new NoCloseStream(in), "UTF-8"),
                                    TextureMetadata.class);
                            String name = entry.getName().substring(
                                    "assets/minecraft/textures/blocks/".length(),
                                    entry.getName().length() - 4 - 7
                            );
                            this.metadata.put(name, metadata);
                        }
                    } else {
                        if (whitelistedTextures.containsKey(entry.getName())) {
                            String name = whitelistedTextures.get(entry.getName());
                            textureNames.add(name);
                            textures.put(name, factory.fromInputStream(new NoCloseStream(in)));
                        } else if (whitelistedResources.containsKey(entry.getName())) {
                            String name = whitelistedResources.get(entry.getName());
                            byte[] data;
                            if (entry.getSize() != -1) {
                                data = new byte[(int) entry.getSize()];
                                int position = 0;
                                int lastRead;
                                while ((lastRead = in.read(data, position, data.length - position)) != -1) {
                                    position += lastRead;
                                }
                            } else {
                                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                                    byte[] buffer = new byte[4092];
                                    int lastRead;
                                    while ((lastRead = in.read(buffer)) != -1) {
                                        outputStream.write(buffer, 0, lastRead);
                                    }
                                    data = outputStream.toByteArray();
                                }
                            }
                            resources.put(name, data);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ZipResourceProvider() {
    }

    public void addTexture(String name, Texture texture) {
        textures.put(name, texture);
        textureNames.add(name);
    }

    @Override
    public String[] getTextures() {
        return textureNames.toArray(new String[textureNames.size()]);
    }

    @Override
    public Texture getTexture(String name) {
        return textures.get(name);
    }

    @Override
    public TextureMetadata getMetadata(String name) {
        return metadata.get(name);
    }

    @Override
    public byte[] getResource(String name) {
        return resources.get(name);
    }

    @Override
    public String[] getResources() {
        return resources.keySet().toArray(new String[resources.keySet().size()]);
    }

    private static class NoCloseStream extends InputStream {
        private final InputStream in;

        public NoCloseStream(InputStream in) {
            this.in = in;
        }

        @Override
        public int read() throws IOException {
            return in.read();
        }

        @Override
        public void close() throws IOException {
            // Nope
        }
    }
}
