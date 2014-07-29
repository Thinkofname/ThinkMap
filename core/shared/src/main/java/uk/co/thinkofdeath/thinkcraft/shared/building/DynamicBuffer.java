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

package uk.co.thinkofdeath.thinkcraft.shared.building;

import elemental.html.ArrayBuffer;
import elemental.html.ArrayBufferView;
import uk.co.thinkofdeath.thinkcraft.shared.platform.Platform;
import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.UByteBuffer;
import uk.co.thinkofdeath.thinkcraft.shared.support.DataStream;

public class DynamicBuffer {

    private static final boolean IS_LITTLE_ENDIAN =
            (DataStream.create(createEndianTestBuffer()).getInt8(0) == 1);
    private final boolean littleEndian;

    private UByteBuffer buffer;
    private DataStream dataStream;
    private int offset = 0;

    /**
     * Creates a DynamicBuffer which resizes as it needs more space.
     * The endianness of the buffer is that of the current system.
     * The starting start has a minimum value of 16.
     *
     * @param size
     *         The starting size of the buffer
     */
    public DynamicBuffer(int size) {
        this(size, IS_LITTLE_ENDIAN);
    }

    public DynamicBuffer(int size, boolean littleEndian) {
        this.littleEndian = littleEndian;
        if (size < 16) size = 16;
        buffer = Platform.alloc().ubyteBuffer(size);
        dataStream = DataStream.create(((ArrayBufferView) buffer).getBuffer()); // Fixme
    }

    /**
     * Adds a single byte to the buffer
     *
     * @param val
     *         The byte to the buffer
     */
    public void add(int val) {
        if (offset >= buffer.size()) {
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
        if (offset + 1 >= buffer.size()) {
            resize();
        }
        dataStream.setUint16(offset, val, littleEndian);
        offset += 2;
    }

    public void addInt(int val) {
        if (offset + 3 >= buffer.size()) {
            resize();
        }
        dataStream.setInt32(offset, val, littleEndian);
        offset += 4;
    }

    /**
     * Adds a float to the buffer
     *
     * @param val
     *         The float to add
     */
    public void addFloat(float val) {
        if (offset + 3 >= buffer.size()) {
            resize();
        }
        dataStream.setFloat32(offset, val, littleEndian);
        offset += 4;
    }

    // Doubles the size of the buffer
    private void resize() {
        UByteBuffer oldBuffer = buffer;
        buffer = Platform.alloc().ubyteBuffer(buffer.size() * 2);
        buffer.set(0, oldBuffer);
        dataStream = DataStream.create(((ArrayBufferView) buffer).getBuffer()); // Fixme
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
    public UByteBuffer getArray() {
        return buffer;
    }

    // Used by IS_LITTLE_ENDIAN
    private static native ArrayBuffer createEndianTestBuffer()/*-{
        return new Uint16Array([1]).buffer;
    }-*/;

    public int getOffset() {
        return offset;
    }
}
