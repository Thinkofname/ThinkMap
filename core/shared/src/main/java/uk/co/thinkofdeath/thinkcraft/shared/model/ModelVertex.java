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

package uk.co.thinkofdeath.thinkcraft.shared.model;

import uk.co.thinkofdeath.thinkcraft.shared.platform.Platform;
import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.FloatBuffer;

public class ModelVertex {

    private final FloatBuffer buffer = Platform.createFloatBuffer(5);

    /**
     * Creates a model vertex initialed to the passed values
     *
     * @param x
     *         The x position of this vertex
     * @param y
     *         The y position of this vertex
     * @param z
     *         The z position of this vertex
     * @param textureX
     *         The x position of the texture for this vertex
     * @param textureY
     *         The y position of the texture for this vertex
     */
    public ModelVertex(float x, float y, float z, float textureX, float textureY) {
        setX(x);
        setY(y);
        setZ(z);
        setTextureX(textureX);
        setTextureY(textureY);
    }

    /**
     * Returns the position of this vertex on the x axis
     *
     * @return The position on the x axis
     */
    public float getX() {
        return buffer.get(0);
    }

    /**
     * Sets the position of this vertex on the x axis
     *
     * @param x
     *         The new position on the x axis
     */
    public void setX(float x) {
        buffer.set(0, x);
    }

    /**
     * Returns the position of this vertex on the y axis
     *
     * @return The position on the y axis
     */
    public float getY() {
        return buffer.get(1);
    }

    /**
     * Sets the position of this vertex on the y axis
     *
     * @param y
     *         The new position on the y axis
     */
    public void setY(float y) {
        buffer.set(1, y);
    }

    /**
     * Returns the position of this vertex on the z axis
     *
     * @return The position on the z axis
     */
    public float getZ() {
        return buffer.get(2);
    }

    /**
     * Sets the position of this vertex on the z axis
     *
     * @param z
     *         The new position on the z axis
     */
    public void setZ(float z) {
        buffer.set(2, z);
    }


    /**
     * Returns the texture position of this vertex on the x axis
     *
     * @return The texture position on the x axis
     */
    public float getTextureX() {
        return buffer.get(3);
    }

    /**
     * Sets the texture position of this vertex on the x axis
     *
     * @param x
     *         The new texture position on the x axis
     */
    public void setTextureX(float x) {
        buffer.set(3, x);
    }


    /**
     * Returns the texture position of this vertex on the x axis
     *
     * @return The texture position on the x axis
     */
    public float getTextureY() {
        return buffer.get(4);
    }

    /**
     * Sets the texture position of this vertex on the y axis
     *
     * @param y
     *         The new texture position on the y axis
     */
    public void setTextureY(float y) {
        buffer.set(4, y);
    }

    /**
     * Creates a copy of this vertex
     *
     * @return A copy
     */
    public ModelVertex duplicate() {
        return new ModelVertex(getX(), getY(), getZ(), getTextureX(), getTextureY());
    }

    // Raw access methods
    float getRaw(int i) {
        return buffer.get(i);
    }

    void setRaw(int i, float v) {
        buffer.set(i, v);
    }
}
