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

package uk.co.thinkofdeath.thinkcraft.shared.serializing;

public interface Serializer {

    /**
     * Stores the string at the key with the given name,
     * overwriting if needed
     *
     * @param name
     *         The key to use
     * @param value
     *         The value to store
     */
    void putString(String name, String value);

    /**
     * Stores the integer at the key with the given name,
     * overwriting if needed
     *
     * @param name
     *         The key to use
     * @param value
     *         The value to store
     */
    void putInt(String name, int value);

    /**
     * Stores the boolean at the key with the given name,
     * overwriting if needed
     *
     * @param name
     *         The key to use
     * @param value
     *         The value to store
     */
    void putBoolean(String name, boolean value);

    /**
     * Stores the serializer at the key with the given name,
     * overwriting if needed
     *
     * @param name
     *         The key to use
     * @param value
     *         The value to store
     */
    void putSub(String name, Serializer value);

    /**
     * Stores the array at the key with the given name,
     * overwriting if needed
     *
     * @param name
     *         The key to use
     * @param value
     *         The value to store
     */
    void putArray(String name, ArraySerializer<?> value);

    /**
     * Returns the string at the key with the given name
     * or null if its not found
     *
     * @param name
     *         The key to use
     * @return The value stored or null
     */
    String getString(String name);

    /**
     * Returns the integer at the key with the given name
     *
     * @param name
     *         The key to use
     * @return The value stored
     */
    int getInt(String name);

    /**
     * Returns the integer at the key with the given name
     *
     * @param name
     *         The key to use
     * @return The value stored
     */
    boolean getBoolean(String name);

    /**
     * Returns the serializer at the key with the given name
     * or null if its not found
     *
     * @param name
     *         The key to use
     * @return The value stored or null
     */
    Serializer getSub(String name);

    /**
     * Returns the array at the key with the given name
     * or null if its not found
     *
     * @param name
     *         The key to use
     * @return The value stored or null
     */
    ArraySerializer<?> getArray(String name);

    // TODO: Burn this
    @Deprecated
    Object getTemp(String name);

    // TODO: Burn this
    @Deprecated
    void putTemp(String name, Object value);
}
