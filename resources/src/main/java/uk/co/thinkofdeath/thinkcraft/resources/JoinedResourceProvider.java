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

import java.util.Arrays;
import java.util.HashSet;

public class JoinedResourceProvider implements ResourceProvider {

    private ResourceProvider[] providers;

    /**
     * Joins the providers in a way where if one texture is missing then the next one will be checked. The first argument is checked first and then on.
     *
     * @param providers
     *         The providers to join
     */
    public JoinedResourceProvider(ResourceProvider... providers) {
        this.providers = providers;
    }

    @Override
    public String[] getTextures() {
        HashSet<String> textures = new HashSet<>();
        for (ResourceProvider provider : providers) {
            textures.addAll(Arrays.asList(provider.getTextures()));
        }
        return textures.toArray(new String[textures.size()]);
    }

    @Override
    public Texture getTexture(String name) {
        for (ResourceProvider provider : providers) {
            Texture texture = provider.getTexture(name);
            if (texture != null) {
                return texture;
            }
        }
        return null;
    }

    @Override
    public TextureMetadata getMetadata(String name) {
        for (ResourceProvider provider : providers) {
            TextureMetadata metadata = provider.getMetadata(name);
            if (metadata != null) {
                return metadata;
            }
        }
        return null;
    }

    @Override
    public byte[] getResource(String name) {
        for (ResourceProvider provider : providers) {
            byte[] resource = provider.getResource(name);
            if (resource != null) {
                return resource;
            }
        }
        return null;
    }
}
