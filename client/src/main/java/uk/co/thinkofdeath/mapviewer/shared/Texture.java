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

package uk.co.thinkofdeath.mapviewer.shared;

public class Texture {

    private final int start;
    private final int end;
    private final String name;

    public Texture(String name, int start, int end) {
        this.name = name;
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the name of the texture
     *
     * @return The texture's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the start position of the texture
     *
     * @return The start position
     */
    public int getStart() {
        return start;
    }

    /**
     * Returns the end position of the texture
     *
     * @return The end position
     */
    public int getEnd() {
        return end;
    }
}
