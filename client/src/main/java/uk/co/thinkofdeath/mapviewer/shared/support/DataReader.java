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

package uk.co.thinkofdeath.mapviewer.shared.support;

import com.google.gwt.core.client.JavaScriptObject;
import elemental.html.ArrayBuffer;

// Because GWT's version of DataView is wrong and we
// only need big endian
public class DataReader extends JavaScriptObject {

    protected DataReader() {
    }

    public static final native DataReader create(ArrayBuffer buffer)/*-{
        return new DataView(buffer);
    }-*/;

    public static final native DataReader create(ArrayBuffer buffer, int offset)/*-{
        return new DataView(buffer, offset);
    }-*/;

    public final native int getUint8(int offset)/*-{
        return this.getUint8(offset);
    }-*/;

    public final native int getInt32(int offset)/*-{
        return this.getInt32(offset, false);
    }-*/;

    public final native int getLength() /*-{
        return this.byteLength;
    }-*/;

    public final native int getUint16(int offset)/*-{
        return this.getUint16(offset, false);
    }-*/;
}
