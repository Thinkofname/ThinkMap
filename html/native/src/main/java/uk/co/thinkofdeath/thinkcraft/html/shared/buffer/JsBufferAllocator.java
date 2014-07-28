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

import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.BufferAllocator;
import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.FloatBuffer;
import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.UByteBuffer;
import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.UShortBuffer;

public class JsBufferAllocator implements BufferAllocator {
    @Override
    public FloatBuffer floatBuffer(int size) {
        return JavascriptFloatBuffer.create(size);
    }

    @Override
    public FloatBuffer floatBuffer(FloatBuffer other) {
        return JavascriptFloatBuffer.create((JavascriptFloatBuffer) other);
    }

    @Override
    public UByteBuffer ubyteBuffer(int size) {
        return JavascriptUByteBuffer.create(size);
    }

    @Override
    public UByteBuffer ubyteBuffer(UByteBuffer other) {
        return JavascriptUByteBuffer.create((JavascriptUByteBuffer) other);
    }

    @Override
    public UShortBuffer ushortBuffer(int size) {
        return JavascriptUShortBuffer.create(size);
    }

    @Override
    public UShortBuffer ushortBuffer(UShortBuffer other) {
        return JavascriptUShortBuffer.create((JavascriptUShortBuffer) other);
    }
}
