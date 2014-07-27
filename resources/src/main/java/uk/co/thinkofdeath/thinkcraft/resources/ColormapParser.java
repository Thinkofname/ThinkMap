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

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ColormapParser {

    private static final HashSet<Integer> allowedColours = new HashSet<Integer>() {{
        add(255 | (255 << 8));
        add(242 | (251 << 8));
        add(204 | (239 << 8));
        add(191 | (204 << 8));
        add(127 | (191 << 8));
        add(178 | (193 << 8));
        add(50 | (71 << 8));
        add(25 | (25 << 8));
        add(50 | (173 << 8));
        add(12 | (36 << 8));
        add(12 | (61 << 8));
        add(0 | (255 << 8));
    }};

    public static Map<Integer, Integer> parse(ResourceProvider provider, TextureFactory factory, String name) {
        Texture texture = factory.fromInputStream(new ByteArrayInputStream(provider.getResource(name)));
        if (texture.getWidth() != 256 || texture.getHeight() != 256) {
            throw new RuntimeException("Invalid colormap");
        }
        HashMap<Integer, Integer> colors = new HashMap<>();
        for (int position : allowedColours) {
            int x = position & 0xFF;
            int y = position >> 8;
            int col = texture.getPixels(x, y, 1, 1)[0] & 0xFFFFFF;
            colors.put(position, col);
        }
        return colors;
    }
}
