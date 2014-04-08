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

package uk.co.thinkofdeath.mapviewer.client.render;

public class Camera {

    private float x;
    private float y;
    private float z;

    // TODO: Change to look vector?
    private float rotationX;
    private float rotationY;

    /**
     * Returns the camera's position on the x axis
     *
     * @return The x position
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the camera's position on the x axis
     *
     * @param x The x position
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Returns the camera's position on the y axis
     *
     * @return The y position
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the camera's position on the t axis
     *
     * @param y The y position
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Returns the camera's position on the z axis
     *
     * @return The z position
     */
    public float getZ() {
        return z;
    }

    /**
     * Sets the camera's position on the z axis
     *
     * @param z The z position
     */
    public void setZ(float z) {
        this.z = z;
    }

    /**
     * Gets the camera's rotation on the x axis
     *
     * @return The x rotation
     */
    public float getRotationX() {
        return rotationX;
    }

    /**
     * Sets the camera's rotation on the x axis
     *
     * @param rotationX The x rotation in radians
     */
    public void setRotationX(float rotationX) {
        this.rotationX = rotationX;
    }


    /**
     * Gets the camera's rotation on the y axis
     *
     * @return The y rotation
     */
    public float getRotationY() {
        return rotationY;
    }

    /**
     * Sets the camera's rotation on the y axis
     *
     * @param rotationY The y rotation in radians
     */
    public void setRotationY(float rotationY) {
        this.rotationY = rotationY;
    }
}
