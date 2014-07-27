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
import uk.co.thinkofdeath.thinkcraft.shared.serializing.Serializer;

public class JsObjectSerializer extends JavaScriptObject implements Serializer {
    protected JsObjectSerializer() {
    }

    public static native JsObjectSerializer from(Object msg)/*-{
        return msg;
    }-*/;

    public static native JsObjectSerializer newInstance()/*-{
        return {};
    }-*/;

    @Override
    public final native String getString(String name)/*-{
        return this[name];
    }-*/;

    @Override
    public final native int getInt(String name)/*-{
        return this[name];
    }-*/;

    @Override
    public final native boolean getBoolean(String name)/*-{
        return this[name];
    }-*/;

    @Override
    public final native JsObjectSerializer getSub(String name)/*-{
        return this[name];
    }-*/;

    @Override
    public final native ArraySerializer<?> getArray(String name)/*-{
        return this[name];
    }-*/;

    @Override
    public final native Object getTemp(String name)/*-{
        return this[name];
    }-*/;

    @Override
    public final native void putString(String name, String value)/*-{
        this[name] = value;
    }-*/;

    @Override
    public final native void putInt(String name, int value)/*-{
        this[name] = value;
    }-*/;

    @Override
    public final native void putBoolean(String name, boolean value)/*-{
        this[name] = value;
    }-*/;

    @Override
    public final native void putSub(String name, Serializer value)/*-{
        this[name] = value;
    }-*/;

    @Override
    public final native void putArray(String name, ArraySerializer<?> value)/*-{
        this[name] = value;
    }-*/;

    @Override
    public final native void putTemp(String name, Object value)/*-{
        this[name] = value;
    }-*/;
}
