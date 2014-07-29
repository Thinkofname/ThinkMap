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

import com.google.gwt.core.client.JavaScriptObject;
import elemental.html.ArrayBuffer;
import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.ViewBuffer;

public class JavascriptViewBuffer extends JavaScriptObject implements ViewBuffer {

    protected JavascriptViewBuffer() {
    }

    public static native JavascriptViewBuffer create(JavascriptBuffer buffer, boolean littleEndian, int offset, int length)/*-{
        var dv = new DataView(buffer.buffer, offset, length);
        dv.littleEndian = littleEndian;
        return dv;
    }-*/;

    public static native JavascriptViewBuffer create(ArrayBuffer buffer, boolean littleEndian, int offset, int length)/*-{
        var dv = new DataView(buffer, offset, length);
        dv.littleEndian = littleEndian;
        return dv;
    }-*/;

    @Override
    public final native void setUInt8(int index, int value)/*-{
        this.setUint8(index, value);
    }-*/;

    @Override
    public final native void setInt8(int index, int value)/*-{
        this.setInt8(index, value);
    }-*/;

    @Override
    public final native void setUInt16(int index, int value)/*-{
        this.setUint16(index, value, this.littleEndian);
    }-*/;

    @Override
    public final native void setInt16(int index, int value)/*-{
        this.setInt16(index, value, this.littleEndian);
    }-*/;

    @Override
    public final native void setUInt32(int index, int value)/*-{
        this.setUint32(index, value, this.littleEndian);
    }-*/;

    @Override
    public final native void setInt32(int index, int value)/*-{
        this.setInt32(index, value, this.littleEndian);
    }-*/;

    @Override
    public final native void setFloat32(int index, float value)/*-{
        this.setFloat32(index, value, this.littleEndian);
    }-*/;

    @Override
    public final native void setFloat64(int index, double value)/*-{
        this.setFloat64(index, value, this.littleEndian);
    }-*/;

    @Override
    public final native int getUInt8(int index)/*-{
        return this.getUint8(index);
    }-*/;

    @Override
    public final native int getInt8(int index)/*-{
        return this.getInt8(index);
    }-*/;

    @Override
    public final native int getUInt16(int index)/*-{
        return this.getUint16(index, this.littleEndian);
    }-*/;

    @Override
    public final native int getInt16(int index)/*-{
        return this.getInt16(index, this.littleEndian);
    }-*/;

    @Override
    public final native int getUInt32(int index)/*-{
        return this.getUint32(index, this.littleEndian);
    }-*/;

    @Override
    public final native int getInt32(int index)/*-{
        return this.getInt32(index, this.littleEndian);
    }-*/;

    @Override
    public final native float getFloat32(int index)/*-{
        return this.getFloat32(index, this.littleEndian);
    }-*/;

    @Override
    public final native double getFloat64(int index)/*-{
        return this.getFloat64(index, this.littleEndian);
    }-*/;
}
