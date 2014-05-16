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

package uk.co.thinkofdeath.mapviewer.shared.building;

import uk.co.thinkofdeath.mapviewer.shared.support.TUint8Array;

public class ModelBuilder {

    private DynamicBuffer buffer;

    /**
     * Creates a model builder with provides helper methods to create models in a format ready to
     * uploaded as a WebGL buffer
     */
    public ModelBuilder() {
        buffer = new DynamicBuffer(80000);
    }

    /**
     * Adds a position attribute to the builder
     *
     * @param x
     *         The x position to add
     * @param y
     *         The y position to add
     * @param z
     *         The z position to add
     * @return This builder
     */
    public ModelBuilder position(float x, float y, float z) {
        buffer.addUnsignedShort((int) (x * 256 + 0.5 + 128));
        buffer.addUnsignedShort((int) (y * 256 + 0.5 + 128));
        buffer.addUnsignedShort((int) (z * 256 + 0.5 + 128));
        return this;
    }

    /**
     * Adds a colour attribute to the builder
     *
     * @param r
     *         The red component
     * @param g
     *         The green component
     * @param b
     *         The blue component
     * @return This builder
     */
    public ModelBuilder colour(int r, int g, int b) {
        return colour(r, g, b, 255);
    }


    /**
     * Adds a colour attribute to the builder
     *
     * @param r
     *         The red component
     * @param g
     *         The green component
     * @param b
     *         The blue component
     * @param a
     *         The alpha component
     * @return This builder
     */
    public ModelBuilder colour(int r, int g, int b, int a) {
        buffer.add(r);
        buffer.add(g);
        buffer.add(b);
        buffer.add(a);
        return this;
    }

    /**
     * Adds a texture details attribute to the builder
     *
     * @return This builder
     */
    public ModelBuilder textureDetails(int posX, int posY, int size, int width, int frames) {
        buffer.addUnsignedShort(posX);
        buffer.addUnsignedShort(posY);
        buffer.addUnsignedShort(size);
        buffer.addUnsignedShort(width);
        buffer.add(frames);
        buffer.add(0); // Padding
        return this;
    }

    /**
     * Adds a texture position attribute to the builder
     *
     * @param x
     *         The x position to add
     * @param y
     *         The y position to add
     * @return This builder
     */
    public ModelBuilder texturePosition(float x, float y) {
        buffer.addUnsignedShort((int) (x * 256 + 0.5));
        buffer.addUnsignedShort((int) (y * 256 + 0.5));
        return this;
    }

    /**
     * Adds a lighting attribute to the builder
     *
     * @param emittedLight
     *         The emitted light from this vertex
     * @param skyLight
     *         The sky light from this vertex
     * @return The builder
     */
    public ModelBuilder lighting(int emittedLight, int skyLight) {
        buffer.add(emittedLight & 0xFF);
        buffer.add(skyLight & 0xFF);
        return this;
    }

    /**
     * Resets the builder so that it can be reused
     */
    public void reset() {
        buffer.reset();
    }

    /**
     * Returns the backing typed array to be uploaded to a buffer
     *
     * @return The typed array
     */
    public TUint8Array toTypedArray() {
        return buffer.getArray();
    }
}
