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

package uk.co.thinkofdeath.mapviewer.shared.support;

import com.google.gwt.core.client.JavaScriptObject;
import elemental.html.ArrayBuffer;
import elemental.html.ArrayBufferView;

// Workaround for the fact gwt elemental's typed arrays are missing an actual
// set method...
public class TUint8Array extends JavaScriptObject {
    protected TUint8Array() {
    }

    /**
     * Creates a new TUint8Array (Uint8Array) with the specified size
     *
     * @param size
     *         Size of the array
     * @return The created array
     */
    public static native TUint8Array create(int size)/*-{
        return new Uint8Array(size);
    }-*/;

    /**
     * Creates a view into the ArrayBuffer
     *
     * @param buffer
     *         The buffer to create a view of
     * @param start
     *         The start position
     * @param length
     *         The length of the view
     * @return The view
     */
    public static native TUint8Array create(ArrayBuffer buffer, int start, int length)/*-{
        return new Uint8Array(buffer, start, length);
    }-*/;

    /**
     * Creates a copy of the other array
     *
     * @param other
     *         The array to copy
     * @return The copy
     */
    public static native TUint8Array create(TUint8Array other)/*-{
        return new Uint8Array(other);
    }-*/;

    /**
     * Sets the value at the specified index in the array
     *
     * @param index
     *         Index in the array
     * @param value
     *         Value to set to
     */
    public final native void set(int index, int value)/*-{
        this[index] = value;
    }-*/;


    /**
     * Sets the values at the specified index in the array
     *
     * @param index
     *         Index in the array
     * @param values
     *         Values to set to
     */
    public final native void set(int index, int[] values)/*-{
        this.set(values, index);
    }-*/;

    /**
     * Sets the values at the beginning of the array
     *
     * @param values
     *         Values to set to
     */
    public final native void set(int[] values)/*-{
        this.set(values);
    }-*/;

    /**
     * Sets the values at the specified index in the array
     *
     * @param index
     *         Index in the array
     * @param values
     *         Values to set to
     */
    public final native void set(int index, ArrayBufferView values)/*-{
        this.set(values, index);
    }-*/;

    /**
     * Sets the values at the beginning of the array
     *
     * @param values
     *         Values to set to
     */
    public final native void set(ArrayBufferView values)/*-{
        this.set(values);
    }-*/;

    /**
     * Sets the values at the specified index in the array
     *
     * @param index
     *         Index in the array
     * @param values
     *         Values to set to
     */
    public final native void set(int index, TUint8Array values)/*-{
        this.set(values, index);
    }-*/;

    /**
     * Sets the values at the beginning of the array
     *
     * @param values
     *         Values to set to
     */
    public final native void set(TUint8Array values)/*-{
        this.set(values);
    }-*/;

    /**
     * Returns the value at the specified index in the array
     *
     * @param index
     *         Index in the array
     * @return The value
     */
    public final native int get(int index)/*-{
        return this[index];
    }-*/;

    /**
     * Creates a sub array of this array starting at the specified index. This is a view not a copy
     *
     * @param start
     *         The start position
     * @return The sub array
     */
    public final native TUint8Array subarray(int start)/*-{
        return this.subarray(start);
    }-*/;

    /**
     * Creates a sub array of this array starting and finishing at the specified indexes. This is a
     * view not a copy
     *
     * @param start
     *         The start position
     * @param end
     *         The end position
     * @return The sub array
     */
    public final native TUint8Array subarray(int start, int end)/*-{
        return this.subarray(start, end);
    }-*/;

    /**
     * Returns the buffer used by this array
     *
     * @return The buffer
     */
    public final native ArrayBuffer getBuffer()/*-{
        return this.buffer;
    }-*/;

    /**
     * Returns the number of bytes in this array
     *
     * @return The number of bytes
     */
    public final native int getByteLength()/*-{
        return this.byteLength;
    }-*/;

    /**
     * Returns the offset into the backing buffer
     *
     * @return The byte offset into the buffer
     */
    public final native int getByteOffset()/*-{
        return this.byteOffset;
    }-*/;

    /**
     * Returns the number of values in this array
     *
     * @return The number of values
     */
    public final native int length()/*-{
        return this.length;
    }-*/;
}
