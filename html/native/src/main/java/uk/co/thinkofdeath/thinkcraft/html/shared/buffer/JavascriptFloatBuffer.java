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

import elemental.html.ArrayBufferView;
import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.FloatBuffer;

public class JavascriptFloatBuffer extends JavascriptBuffer implements FloatBuffer {

    public JavascriptFloatBuffer(int size) {
        super(createBuffer(size));
    }

    @Override
    public native void set(int index, float value)/*-{
        this.@uk.co.thinkofdeath.thinkcraft.html.shared.buffer.JavascriptBuffer::buffer[index] = value;
    }-*/;

    @Override
    public native float get(int index)/*-{
        return this.@uk.co.thinkofdeath.thinkcraft.html.shared.buffer.JavascriptBuffer::buffer[index];
    }-*/;

    private native static ArrayBufferView createBuffer(int size)/*-{
        return new Float32Array(size);
    }-*/;
}
