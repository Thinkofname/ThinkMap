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

package uk.co.thinkofdeath.mapviewer.assets;

import com.google.gson.*;
import com.google.gwt.core.shared.GwtIncompatible;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

@GwtIncompatible("Used to create the texture atlas")
public class TextureBuilder {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        final Gson gson = new Gson();
        final JsonParser parser = new JsonParser();
        File assetDir = new File("assets/block_images");
        File[] files = assetDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".png");
            }
        });
        BufferedImage image = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
        HashMap<String, int[]> textures = new HashMap<String, int[]>();
        int pos = 0;

        for (File file : files) {
            BufferedImage sprite = ImageIO.read(file);

            if (sprite.getWidth() != 16 || sprite.getHeight() != 16) {
                JsonObject meta = parser.parse(new FileReader(
                        "assets/block_images/" + file.getName() + ".mcmeta")).getAsJsonObject();
                ArrayList<Integer> frames = new ArrayList<Integer>();
                JsonObject animation = meta.get("animation").getAsJsonObject();
                JsonElement frsEle = animation.get("frames");
                if (frsEle != null) {
                    JsonArray frs = frsEle.getAsJsonArray();
                    for (JsonElement ele : frs) {
                        frames.add(ele.getAsInt());
                    }
                } else {
                    for (int i = 0; i < sprite.getHeight() / sprite.getWidth(); i++) {
                        frames.add(i);
                    }
                }
                int frameTime = animation.has("frametime") ? animation.get("frametime").getAsInt() : 1;
                int start = pos;
                for (int frame : frames) {
                    for (int i = 0; i < frameTime; i++) {
                        int[] data = sprite.getRGB(0, 16 * frame, 16, 16, null, 0, 16);
                        image.setRGB((pos % 32) * 16, (pos / 32) * 16, 16, 16, data, 0, 16);
                        pos++;
                    }
                }
                textures.put(file.getName().substring(0, file.getName().length() - 4), new int[]{start, pos - 1});
                continue;
            }

            int[] data = sprite.getRGB(0, 0, 16, 16, null, 0, 16);
            image.setRGB((pos % 32) * 16, (pos / 32) * 16, 16, 16, data, 0, 16);
            textures.put(file.getName().substring(0, file.getName().length() - 4), new int[]{pos, pos});
            pos++;
        }
        Files.write(Paths.get("build", "block_images", "blocks.json"), gson.toJson(textures).getBytes("UTF-8"));
        ImageIO.write(image, "PNG", new File("build/block_images/blocks.png"));

        System.out.printf("Done in %dms\n", System.currentTimeMillis() - startTime);
    }
}
