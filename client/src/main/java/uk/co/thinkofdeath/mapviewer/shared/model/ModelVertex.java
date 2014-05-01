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

package uk.co.thinkofdeath.mapviewer.shared.model;

import com.google.gwt.core.client.JavaScriptObject;

public class ModelVertex extends JavaScriptObject {

    protected ModelVertex() {
    }

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
     * @return The created model vertex
     */
    public static native ModelVertex create(float x, float y, float z, float textureX,
                                            float textureY)/*-{
        return new Float32Array([x, y, z, textureX, textureY]);
    }-*/;

    /**
     * Returns the position of this vertex on the x axis
     *
     * @return The position on the x axis
     */
    public final native float getX()/*-{
        return this[0];
    }-*/;

    /**
     * Sets the position of this vertex on the x axis
     *
     * @param x
     *         The new position on the x axis
     */
    public final native void setX(float x)/*-{
        this[0] = x;
    }-*/;

    /**
     * Returns the position of this vertex on the y axis
     *
     * @return The position on the y axis
     */
    public final native float getY()/*-{
        return this[1];
    }-*/;

    /**
     * Sets the position of this vertex on the y axis
     *
     * @param y
     *         The new position on the y axis
     */
    public final native void setY(float y)/*-{
        this[1] = y;
    }-*/;

    /**
     * Returns the position of this vertex on the z axis
     *
     * @return The position on the z axis
     */
    public final native float getZ()/*-{
        return this[2];
    }-*/;

    /**
     * Sets the position of this vertex on the z axis
     *
     * @param z
     *         The new position on the z axis
     */
    public final native void setZ(float z)/*-{
        this[2] = z;
    }-*/;


    /**
     * Returns the texture position of this vertex on the x axis
     *
     * @return The texture position on the x axis
     */
    public final native float getTextureX()/*-{
        return this[3];
    }-*/;

    /**
     * Sets the texture position of this vertex on the x axis
     *
     * @param x
     *         The new texture position on the x axis
     */
    public final native void setTextureX(float x)/*-{
        this[3] = x;
    }-*/;


    /**
     * Returns the texture position of this vertex on the x axis
     *
     * @return The texture position on the x axis
     */
    public final native float getTextureY()/*-{
        return this[4];
    }-*/;

    /**
     * Sets the texture position of this vertex on the y axis
     *
     * @param y
     *         The new texture position on the y axis
     */
    public final native void setTextureY(float y)/*-{
        this[4] = y;
    }-*/;

    /**
     * Creates a copy of this vertex
     *
     * @return A copy
     */
    public final native ModelVertex clone()/*-{
        return new Float32Array(this);
    }-*/;

    // Raw access methods
    final native float getRaw(int i)/*-{
        return this[i];
    }-*/;

    final native void setRaw(int i, float v)/*-{
        this[i] = v;
    }-*/;
}
