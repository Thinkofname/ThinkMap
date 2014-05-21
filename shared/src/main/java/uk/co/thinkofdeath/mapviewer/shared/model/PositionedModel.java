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

package uk.co.thinkofdeath.mapviewer.shared.model;

import com.google.gwt.core.client.JavaScriptObject;

public class PositionedModel extends JavaScriptObject {
    protected PositionedModel() {
    }

    public static native PositionedModel create(int x, int y, int z, int start, int length)/*-{
        return {x: x, y: y, z: z, start: start, length: length};
    }-*/;

    public final native int getX()/*-{
        return this.x;
    }-*/;

    public final native int getY()/*-{
        return this.y;
    }-*/;

    public final native int getZ()/*-{
        return this.z;
    }-*/;

    public final native int getStart()/*-{
        return this.start;
    }-*/;

    public final native int getLength()/*-{
        return this.length;
    }-*/;
}
