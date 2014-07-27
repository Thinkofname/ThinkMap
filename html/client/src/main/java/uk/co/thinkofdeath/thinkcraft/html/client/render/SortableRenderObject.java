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

package uk.co.thinkofdeath.thinkcraft.html.client.render;

import elemental.html.WebGLBuffer;
import uk.co.thinkofdeath.thinkcraft.shared.model.PositionedModel;
import uk.co.thinkofdeath.thinkcraft.shared.support.TUint8Array;

import java.util.ArrayList;

public class SortableRenderObject {

    private final int x;
    private final int y;
    private final int z;
    private final ArrayList<PositionedModel> models = new ArrayList<>();
    private TUint8Array data;

    public SortableRenderObject(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    public ArrayList<PositionedModel> getModels() {
        return models;
    }

    public void setData(TUint8Array data) {
        this.data = data;
        if (data != null) {
            tempArray = TUint8Array.create(data.length());
        } else {
            tempArray = null;
        }
    }

    public TUint8Array getData() {
        return data;
    }

    WebGLBuffer buffer;
    TUint8Array tempArray;
    int count = 0;
    boolean needResort = true;
}
