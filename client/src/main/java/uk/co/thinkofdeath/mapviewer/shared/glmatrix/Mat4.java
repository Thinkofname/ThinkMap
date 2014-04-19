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

public class Mat4 extends JsFloat32Array {
    protected Mat4() {
    }

    public static native Mat4 create()/*-{
        return $wnd.mat4.create();
    }-*/;

    public final native void identity()/*-{
        $wnd.mat4.identity(this);
    }-*/;

    public final native void perspective(float fovy, float aspect, float near, float far)/*-{
        $wnd.mat4.perspective(fovy, aspect, near, far);
    }-*/;

    public final native void scale(float x, float y, float z)/*-{
        $wnd.mat4.scale(this, this, [x, y, z]);
    }-*/;

    public final native void rotateX(float rad)/*-{
        $wnd.mat4.rotateX(this, this, rad);
    }-*/;

    public final native void rotateY(float rad)/*-{
        $wnd.mat4.rotateY(this, this, rad);
    }-*/;

    public final native void rotateZ(float rad)/*-{
        $wnd.mat4.rotateZ(this, this, rad);
    }-*/;

    public final native void rotate(float rad, float x, float y, float z)/*-{
        $wnd.mat4.rotate(this, this, rad, [x, y, z]);
    }-*/;

    public final native void translate(float x, float y, float z)/*-{
        $wnd.mat4.translate(this, this, [x, y, z]);
    }-*/;

    public final native Mat4 multiply(Mat4 other, Mat4 out)/*-{
        if (out == null) out = $wnd.mat4.create();
        $wnd.mat4.multiply(out, this, other);
        return out;
    }-*/;
}
