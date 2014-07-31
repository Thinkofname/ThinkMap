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

package uk.co.thinkofdeath.thinkcraft.html.shared.buffer;


import elemental.html.ArrayBuffer;
import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.UByteBuffer;

public class JavascriptUByteBuffer extends JavascriptBuffer implements UByteBuffer {

    protected JavascriptUByteBuffer() {
    }

    public native static JavascriptUByteBuffer create(int size)/*-{
        return new Uint8Array(size);
    }-*/;

    public native static JavascriptUByteBuffer create(JavascriptUByteBuffer other)/*-{
        var buf = new Uint8Array(other.length);
        buf.set(other, 0);
        return buf;
    }-*/;

    public native static JavascriptUByteBuffer create(JavascriptBuffer buffer, int offset, int length)/*-{
        return new Uint8Array(buffer.buffer, offset, length);
    }-*/;

    public native static JavascriptUByteBuffer create(ArrayBuffer buffer, int offset, int length)/*-{
        return new Uint8Array(buffer, offset, length);
    }-*/;

    @Override
    public final native void set(int index, int value)/*-{
        this[index] = value;
    }-*/;

    @Override
    public final native int get(int index)/*-{
        return this[index];
    }-*/;
}
