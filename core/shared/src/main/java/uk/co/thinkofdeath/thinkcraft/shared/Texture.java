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

import uk.co.thinkofdeath.thinkcraft.shared.serializing.Serializable;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.Serializer;

public class Texture implements Serializable {

    private String name;
    private int id;
    private boolean isLoaded;

    Texture() {

    }

    public Texture(String name, int id) {
        this.name = name;
        this.id = id;
    }

    /**
     * Returns the name of the texture
     *
     * @return The texture's name
     */
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    @Override
    public void serialize(Serializer serializer) {
        serializer.putString("name", name);
        serializer.putInt("id", id);
    }

    @Override
    public void deserialize(Serializer serializer) {
        name = serializer.getString("name");
        id = serializer.getInt("id");
    }

    @Override
    public Serializable create() {
        return new Texture();
    }
}
