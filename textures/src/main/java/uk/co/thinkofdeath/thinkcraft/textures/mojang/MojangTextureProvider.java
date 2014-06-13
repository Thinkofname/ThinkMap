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

package uk.co.thinkofdeath.thinkcraft.textures.mojang;

import uk.co.thinkofdeath.thinkcraft.textures.TextureFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MojangTextureProvider extends ZipTextureProvider {

    private static final String JAR_LOCATION =
            "http://s3.amazonaws.com/Minecraft.Download/versions/%1$s/%1$s.jar";

    public MojangTextureProvider(String version, TextureFactory factory) {
        try {
            InputStream inputStream = new URL(String.format(JAR_LOCATION, version))
                    .openConnection().getInputStream();
            fromStream(inputStream, factory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
