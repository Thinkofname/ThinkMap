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

public interface SerializerFactory {

    /**
     * Creates a standard serializer
     *
     * @return The serializer
     */
    Serializer create();

    /**
     * Creates an array of serializers
     *
     * @return The array
     */
    SerializerArraySerializer createSerializerArray();

    /**
     * Creates an array of integers
     *
     * @return The array
     */
    IntArraySerializer createIntArray();

    /**
     * Creates an array of booleans
     *
     * @return The array
     */
    BooleanArraySerializer createBooleanArray();

    /**
     * Creates an array of strings
     *
     * @return The array
     */
    StringArraySerializer createStringArray();

    /**
     * Creates an array of arrays
     *
     * @return The array
     */
    ArraySerializerArraySerializer createArrayArray();

    /**
     * Creates an array of arrays
     *
     * @return The array
     */
    BufferArraySerializer createBufferArray();
}
