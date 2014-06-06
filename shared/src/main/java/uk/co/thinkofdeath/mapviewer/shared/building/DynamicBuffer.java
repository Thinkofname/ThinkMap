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

import elemental.html.ArrayBuffer;
import uk.co.thinkofdeath.mapviewer.shared.support.DataReader;
import uk.co.thinkofdeath.mapviewer.shared.support.TUint8Array;

public class DynamicBuffer {

    private static final boolean IS_LITTLE_ENDIAN =
            (DataReader.create(createEndianTestBuffer()).getInt8(0) == 1);
    public static final BufferPool POOL = new BufferPool();

    private TUint8Array buffer;
    private DataReader dataReader;
    private int offset = 0;

    /**
     * Creates a DynamicBuffer which resizes as it needs more space. The endianness of the buffer is
     * that of the current system. The starting start has a minimum value of 16.
     *
     * @param size
     *         The starting size of the buffer
     */
    public DynamicBuffer(int size) {
        if (size < 16) size = 16;
        buffer = POOL.alloc(size);
        dataReader = DataReader.create(buffer.getBuffer());
    }

    /**
     * Adds a single byte to the buffer
     *
     * @param val
     *         The byte to the buffer
     */
    public void add(int val) {
        if (offset >= buffer.length()) {
            resize();
        }
        buffer.set(offset++, val);
    }

    /**
     * Adds a unsigned short to the buffer
     *
     * @param val
     *         The short to add
     */
    public void addUnsignedShort(int val) {
        if (offset + 1 >= buffer.length()) {
            resize();
        }
        dataReader.setUint16(offset, val, IS_LITTLE_ENDIAN);
        offset += 2;
    }

    /**
     * Adds a float to the buffer
     *
     * @param val
     *         The float to add
     */
    public void addFloat(float val) {
        if (offset + 3 >= buffer.length()) {
            resize();
        }
        dataReader.setFloat32(offset, val, IS_LITTLE_ENDIAN);
        offset += 4;
    }

    // Doubles the size of the buffer
    private void resize() {
        buffer = POOL.resize(buffer, buffer.length() * 2);
        dataReader = DataReader.create(buffer.getBuffer());
    }

    /**
     * Resets the buffer for reused
     */
    public void reset() {
        offset = 0;
    }

    /**
     * Returns a view into the buffer sized at the final size of the buffer
     *
     * @return The view into the array
     */
    public TUint8Array getArray() {
        return buffer.subarray(0, offset);
    }

    // Used by IS_LITTLE_ENDIAN
    private static native ArrayBuffer createEndianTestBuffer()/*-{
        return new Uint16Array([1]).buffer;
    }-*/;

    public int getOffset() {
        return offset;
    }
}
