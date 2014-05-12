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

package uk.co.thinkofdeath.thinkmap.bukkit.textures;

import uk.co.thinkofdeath.thinkmap.textures.Texture;
import uk.co.thinkofdeath.thinkmap.textures.TextureFactory;

import java.io.InputStream;

public class BufferedTextureFactory implements TextureFactory {
    @Override
    public Texture fromInputStream(InputStream inputStream) {
        return new BufferedTexture(inputStream);
    }

    @Override
    public Texture create(int w, int h) {
        return new BufferedTexture(w, h);
    }
}
