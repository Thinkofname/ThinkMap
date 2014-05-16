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

package uk.co.thinkofdeath.thinkmap.textures.mojang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import uk.co.thinkofdeath.thinkmap.textures.Texture;
import uk.co.thinkofdeath.thinkmap.textures.TextureFactory;
import uk.co.thinkofdeath.thinkmap.textures.TextureMetadata;
import uk.co.thinkofdeath.thinkmap.textures.TextureProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MojangTextureProvider implements TextureProvider {

    private static final String JAR_LOCATION =
            "http://s3.amazonaws.com/Minecraft.Download/versions/%1$s/%1$s.jar";

    private ArrayList<String> textureNames = new ArrayList<>();
    private HashMap<String, Texture> textures = new HashMap<>();
    private HashMap<String, TextureMetadata> metadata = new HashMap<>();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(MojangMetadata.class, new MojangMetadataDeserializer())
            .registerTypeAdapter(MojangMetadataAnimation.class, new MojangMetadataAnimationDeserializer())
            .create();

    private HashMap<String, String> whitelistedTextures = new HashMap<String, String>() {{
        put("assets/minecraft/textures/entity/chest/normal.png", "chest_normal");
    }};

    public MojangTextureProvider(String version, TextureFactory factory) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    String.format(JAR_LOCATION, version))
                    .openConnection();
            try (ZipInputStream in = new ZipInputStream(connection.getInputStream())) {
                ZipEntry entry;
                while ((entry = in.getNextEntry()) != null) {
                    if (!entry.getName().startsWith("assets/minecraft/textures/blocks/")) {
                        if (whitelistedTextures.containsKey(entry.getName())) {
                            String name = whitelistedTextures.get(entry.getName());
                            textureNames.add(name);
                            textures.put(name, factory.fromInputStream(new NoCloseStream(in)));
                        }
                        continue;
                    }
                    if (entry.getName().endsWith(".png")) {
                        String name = entry.getName().substring(
                                "assets/minecraft/textures/blocks/".length(),
                                entry.getName().length() - 4
                        );
                        textureNames.add(name);
                        textures.put(name, factory.fromInputStream(new NoCloseStream(in)));
                    } else if (entry.getName().endsWith(".png.mcmeta")) {
                        MojangMetadata metadata = gson.fromJson(
                                new InputStreamReader(new NoCloseStream(in)),
                                MojangMetadata.class);
                        String name = entry.getName().substring(
                                "assets/minecraft/textures/blocks/".length(),
                                entry.getName().length() - 4 - 7
                        );
                        this.metadata.put(name, metadata);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
