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

public enum Face {
    /**
     * Top face
     */
    TOP(0, 1, 0),
    /**
     * Bottom face
     */
    BOTTOM(0, -1, 0),
    /**
     * Left face
     */
    LEFT(1, 0, 0),
    /**
     * Right face
     */
    RIGHT(-1, 0, 0),
    /**
     * Front face
     */
    FRONT(0, 0, 1),
    /**
     * Back face
     */
    BACK(0, 0, -1);

    private final String name;
    private final int offsetX;
    private final int offsetY;
    private final int offsetZ;

    Face(int offsetX, int offsetY, int offsetZ) {
        this.name = name().toLowerCase();
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    /**
     * Returns the readable name of this facing direction
     *
     * @return The readable name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the offset x of the face
     *
     * @return The x offset
     */
    public int getOffsetX() {
        return offsetX;
    }

    /**
     * Returns the offset y of the face
     *
     * @return The y offset
     */
    public int getOffsetY() {
        return offsetY;
    }

    /**
     * Returns the offset z of the face
     *
     * @return The z offset
     */
    public int getOffsetZ() {
        return offsetZ;
    }
}
