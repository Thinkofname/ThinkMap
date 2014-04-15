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

package uk.co.thinkofdeath.mapviewer.shared.worker;

import com.google.gwt.core.client.JavaScriptObject;
import elemental.html.ArrayBuffer;

public class ChunkLoadMessage extends JavaScriptObject {
    protected ChunkLoadMessage() {
    }

    /**
     * Creates a new chunk load message
     *
     * @param x    The x position of the chunk
     * @param z    The z position of the chunk
     * @param data The data of the chunk
     * @return The message
     */
    public static native ChunkLoadMessage create(int x, int z, ArrayBuffer data)/*-{
        return {x: x, z: z, data: data};
    }-*/;

    /**
     * Returns the x position of the requested chunk
     *
     * @return The x position
     */
    public final native int getX()/*-{
        return this.x;
    }-*/;

    /**
     * Returns the z position of the requested chunk
     *
     * @return The z position
     */
    public final native int getZ()/*-{
        return this.z;
    }-*/;

    /**
     * Returns the data needed to load this chunk
     *
     * @return The data
     */
    public final native ArrayBuffer getData()/*-{
        return this.data;
    }-*/;
}
