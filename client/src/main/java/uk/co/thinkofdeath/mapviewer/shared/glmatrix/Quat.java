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

package uk.co.thinkofdeath.mapviewer.shared.glmatrix;

import elemental.js.html.JsFloat32Array;

public class Quat extends JsFloat32Array {
    protected Quat() {
    }

    public static native Quat create()/*-{
        return $wnd.quat.create();
    }-*/;

    public final native void setAxisAngle(float rad, float x, float y, float z)/*-{
        $wnd.quat.setAxisAngle(this, axis, rad)
    }-*/;

    public final native void conjugate(Quat q)/*-{
        $wnd.quat.conjugate(this, q);
    }-*/;

    public final native Quat multiply(Quat quat, float[] b)/*-{
        $wnd.quat.multiply(this, quat, b);
    }-*/;

    public final native void multiply(Quat other)/*-{
        $wnd.quat.multiply(this, other);
    }-*/;
}
