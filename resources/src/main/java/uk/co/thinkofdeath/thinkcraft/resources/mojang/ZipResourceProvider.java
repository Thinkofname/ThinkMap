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

import uk.co.thinkofdeath.thinkcraft.resources.ResourceProvider;
import uk.co.thinkofdeath.thinkcraft.resources.Texture;
import uk.co.thinkofdeath.thinkcraft.resources.TextureFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipResourceProvider implements ResourceProvider {

    private final ArrayList<String> textureNames = new ArrayList<>();
    private final HashMap<String, Texture> textures = new HashMap<>();
    private final HashMap<String, byte[]> resources = new HashMap<>();

    public ZipResourceProvider(InputStream inputStream, TextureFactory factory) {
        fromStream(inputStream, factory);
    }

    void fromStream(InputStream inputStream, TextureFactory factory) {
        try {
            try (ZipInputStream in = new ZipInputStream(inputStream)) {
                ZipEntry entry;
                while ((entry = in.getNextEntry()) != null) {
                    if (entry.getName().endsWith(".png")) {
                        String name = entry.getName().substring(0, entry.getName().length() - 4);
                        textureNames.add(name);
                        textures.put(name, factory.fromInputStream(new NoCloseStream(in)));
                    } else if (!entry.getName().endsWith(".class")) {
                        String name = entry.getName();
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
