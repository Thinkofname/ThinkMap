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
import uk.co.thinkofdeath.thinkcraft.shared.support.TUint8Array;

public class ChunkRenderObject {

    final int x;
    final int y;
    final int z;

    WebGLBuffer buffer;
    TUint8Array data;
    int triangleCount;
    int sender;

    /**
     * Creates a new chunk render object used by the render to render a chunk section
     *
     * @param x
     *         The x position of the chunk
     * @param y
     *         The y position of the chunk
     * @param z
     *         The z position of the chunk
     */
    public ChunkRenderObject(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Loads/replaced the data used for this object
     *
     * @param data
     *         The new data to use
     * @param sender
     */
    public void load(TUint8Array data, int sender) {
        this.data = data;
        triangleCount = data.length() / 22;
        this.sender = sender;
    }
}
