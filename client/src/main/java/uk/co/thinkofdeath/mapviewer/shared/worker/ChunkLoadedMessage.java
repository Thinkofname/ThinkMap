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
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.support.TUint8Array;

public class ChunkLoadedMessage extends JavaScriptObject {
    protected ChunkLoadedMessage() {
    }

    /**
     * Creates a chunk loaded message
     *
     * @param x
     *         The x position of the loaded chunk
     * @param z
     *         The y position of the loaded chunk
     * @return The created message
     */
    public static native ChunkLoadedMessage create(int x, int z)/*-{
        return {x: x, z: z, sections: [], nextId: 0, idmap: [], blockmap: {}};
    }-*/;

    /**
     * Gets the x position of the loaded chunk
     *
     * @return The x position
     */
    public final native int getX()/*-{
        return this.x;
    }-*/;

    /**
     * Gets the z position of the loaded chunk
     *
     * @return The z position
     */
    public final native int getZ()/*-{
        return this.z;
    }-*/;

    /**
     * Sets the section for this message
     *
     * @param i
     *         Section position
     * @param count
     *         Non-zero item count
     * @param buffer
     *         Data buffer
     */
    public final native void setSection(int i, int count, TUint8Array buffer)/*-{
        this.sections[i] = {
            count: count,
            buffer: buffer
        };
    }-*/;

    /**
     * Sets the next block id for this chunk
     *
     * @param nextId
     *         The next id
     */
    public final native void setNextId(int nextId)/*-{
        this.nextId = nextId;
    }-*/;

    /**
     * Adds an id -> block mapping to the message
     *
     * @param key
     *         The id
     * @param value
     *         The block
     */
    public final native void addIdBlockMapping(int key, Block value)/*-{
        this.idmap[key] = value.@uk.co.thinkofdeath.mapviewer.shared.block.Block::toString()();
    }-*/;

    /**
     * Adds a block -> id mapping to the message
     *
     * @param key
     *         The block
     * @param value
     *         The id
     */
    public final native void addBlockIdMapping(Block key, int value)/*-{
        this.blockmap[key.@uk.co.thinkofdeath.mapviewer.shared.block.Block::toString()()] = value;
    }-*/;
}
