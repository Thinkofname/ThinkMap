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

package uk.co.thinkofdeath.thinkcraft.html.shared.serialize;

import com.google.gwt.core.client.JavaScriptObject;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.ArraySerializer;

public abstract class JsArraySerializer<V> extends JavaScriptObject implements ArraySerializer<V> {
    protected JsArraySerializer() {
    }

    public static native <V> JsArraySerializer<V> create()/*-{
        return [];
    }-*/;

    @Override
    public final native void add(V v)/*-{
        this.push(v);
    }-*/;

    @Override
    public final native V get(int i)/*-{
        return this[i];
    }-*/;

    @Override
    public final native int size()/*-{
        return this.length;
    }-*/;
}
