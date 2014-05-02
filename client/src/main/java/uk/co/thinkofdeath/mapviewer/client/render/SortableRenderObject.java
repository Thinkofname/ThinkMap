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

import elemental.html.WebGLBuffer;
import uk.co.thinkofdeath.mapviewer.shared.model.SendableModel;

import java.util.ArrayList;

public class SortableRenderObject {

    private ArrayList<SendableModel> models;
    private final int x;
    private final int y;
    private final int z;

    public SortableRenderObject(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Gets the BSPTree for this object
     *
     * @return The BSPTree
     */
    public ArrayList<SendableModel> getModels() {
        return models;
    }

    /**
     * Changes the models for this object
     *
     * @param models
     */
    public void setModels(ArrayList<SendableModel> models) {
        this.models = models;
        needResort = true;
    }

    /**
     * Gets the position of this object in the world
     *
     * @return The x position
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the position of this object in the world
     *
     * @return The y position
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the position of this object in the world
     *
     * @return The z position
     */
    public int getZ() {
        return z;
    }

    WebGLBuffer buffer;
    int count = 0;
    boolean needResort = true;
}
