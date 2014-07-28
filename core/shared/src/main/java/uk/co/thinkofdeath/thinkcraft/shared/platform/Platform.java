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

package uk.co.thinkofdeath.thinkcraft.shared.platform;

import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.BufferAllocator;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.SerializerFactory;

/**
 * Handles Platform specific code
 */
public abstract class Platform {

    private static Platform platform;

    /**
     * Sets the platform provider. May only be called once.
     *
     * @param platform
     *         The provider to use
     */
    public static void setPlatform(Platform platform) {
        if (Platform.platform != null) throw new IllegalArgumentException();
        Platform.platform = platform;
    }

    /**
     * Returns the default allocator for this platform
     *
     * @return The allocator
     */
    public static BufferAllocator alloc() {
        return platform.allocator();
    }

    /**
     * @see #alloc()
     */
    protected abstract BufferAllocator allocator();

    /**
     * Runs the passed runnable repeatedly at the interval
     * specified
     *
     * @param runnable
     *         The runnable to run
     * @param timeMS
     *         The interval in milliseconds
     */
    public static void runRepeated(Runnable runnable, int timeMS) {
        platform.repeatTask(runnable, timeMS);
    }

    /**
     * @see #runRepeated(Runnable, int)
     */
    protected abstract void repeatTask(Runnable runnable, int timeMS);

    /**
     * Returns a serializer factory which creates worker safe
     * serializers
     *
     * @return The worker safe serializer factory
     */
    public static SerializerFactory workerSerializers() {
        return platform.serializersWorker();
    }

    /**
     * @see #workerSerializers()
     */
    protected abstract SerializerFactory serializersWorker();
}
