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
import uk.co.thinkofdeath.thinkcraft.html.client.world.ClientChunk;

public class ChunkRenderObject {

    final ClientChunk chunk;
    final int x;
    final int y;
    final int z;

    WebGLBuffer buffer;
    int triangleCount;

    /**
     * Creates a new chunk render object used by the render to render a chunk section
     *
     * @param chunk
     *         The owner of this object
     * @param x
     *         The x position of the chunk
     * @param y
     *         The y position of the chunk
     * @param z
     *         The z position of the chunk
     */
    public ChunkRenderObject(ClientChunk chunk, int x, int y, int z) {
        this.chunk = chunk;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
