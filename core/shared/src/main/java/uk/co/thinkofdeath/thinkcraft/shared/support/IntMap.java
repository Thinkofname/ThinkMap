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

package uk.co.thinkofdeath.thinkcraft.shared.support;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;

public class IntMap<T> extends JavaScriptObject {

    protected IntMap() {
    }

    /**
     * Creates an int map
     *
     * @param <T>
     *         The type that the map contains
     * @return The created map
     */
    public static native <T> IntMap<T> create()/*-{
        var v = [];
        v.$keys = [];
        return v;
    }-*/;

    /**
     * Returns a value from the map
     *
     * @param key
     *         The key for the value
     * @return The value or null
     */
    public final native T get(int key)/*-{
        return this[key];
    }-*/;

    /**
     * Stores a value into the map
     *
     * @param key
     *         The key for the value
     * @param value
     *         The value to store
     */
    public final native void put(int key, T value)/*-{
        this[key] = value;
        this.$keys.push(key);
    }-*/;

    /**
     * Returns all keys in this map
     *
     * @return The keys
     */
    public final native JsArrayInteger keys()/*-{
        return this.$keys;
    }-*/;
}
