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

import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.FloatBuffer;

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
     * Creates a FloatBuffer of the size passed
     *
     * @param size
     *         The size of the buffer
     * @return The created FloatBuffer
     */
    public static FloatBuffer createFloatBuffer(int size) {
        return platform.newFloatBuffer(size);
    }

    /**
     * @see #createFloatBuffer(int)
     */
    public abstract FloatBuffer newFloatBuffer(int size);

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
    public abstract void repeatTask(Runnable runnable, int timeMS);
}
