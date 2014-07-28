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

package uk.co.thinkofdeath.thinkcraft.shared.platform.buffers;

public interface BufferAllocator {

    /**
     * Creates a FloatBuffer of the size passed
     *
     * @param size
     *         The size of the buffer
     * @return The created FloatBuffer
     */
    FloatBuffer floatBuffer(int size);

    /**
     * Creates a copy of the FloatBuffer
     *
     * @param other
     *         The buffer to copy
     * @return The created FloatBuffer
     */
    FloatBuffer floatBuffer(FloatBuffer other);

    /**
     * Creates a UByteBuffer of the size passed
     *
     * @param size
     *         The size of the buffer
     * @return The created UByteBuffer
     */
    UByteBuffer ubyteBuffer(int size);

    /**
     * Creates a copy of the UByteBuffer
     *
     * @param other
     *         The buffer to copy
     * @return The created UByteBuffer
     */
    UByteBuffer ubyteBuffer(UByteBuffer other);

    /**
     * Creates a UShortBuffer of the size passed
     *
     * @param size
     *         The size of the buffer
     * @return The created UShortBuffer
     */
    UShortBuffer ushortBuffer(int size);

    /**
     * Creates a copy of the UShortBuffer
     *
     * @param other
     *         The buffer to copy
     * @return The created UShortBuffer
     */
    UShortBuffer ushortBuffer(UShortBuffer other);
}
