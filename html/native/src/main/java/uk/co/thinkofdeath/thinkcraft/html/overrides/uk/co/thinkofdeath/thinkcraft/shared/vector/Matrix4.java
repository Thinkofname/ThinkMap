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

package uk.co.thinkofdeath.thinkcraft.shared.vector;

import elemental.html.Float32Array;

public class Matrix4 {

    private Float32Array values;

    public Matrix4() {
        init();
        identity();
    }

    private native void init()/*-{
        this.@uk.co.thinkofdeath.thinkcraft.shared.vector.Matrix4::values = new Float32Array(4 * 4);
    }-*/;

    private static native int k(int x, int y)/*-{
        return y + x * 4;
    }-*/;

    public native void identity()/*-{
        var values = this.@uk.co.thinkofdeath.thinkcraft.shared.vector.Matrix4::values;
        for (var i = 0; i < 16; i++) values[i] = 0;
        var k = @uk.co.thinkofdeath.thinkcraft.shared.vector.Matrix4::k(II);

        values[k(0, 0)]
            = values[k(1, 1)]
            = values[k(2, 2)]
            = values[k(3, 3)]
            = 1;
    }-*/;

    public native void perspective(float fovy, float aspect, float near, float far)/*-{
        var values = this.@uk.co.thinkofdeath.thinkcraft.shared.vector.Matrix4::values;
        var invDepth = 1 / (near - far);
        var k = @uk.co.thinkofdeath.thinkcraft.shared.vector.Matrix4::k(II);

        values[k(1, 1)] = 1 / Math.tan(0.5 * fovy);
        values[k(0, 0)] = values[k(1, 1)] / aspect;
        values[k(2, 2)] = (far + near) * invDepth;
        values[k(3, 2)] = (2 * far * near) * invDepth;
        values[k(2, 3)] = -1;
        values[k(3, 3)] = 0;
    }-*/;

    public native void scale(float x, float y, float z)/*-{
        var values = this.@uk.co.thinkofdeath.thinkcraft.shared.vector.Matrix4::values;
        values[0] *= x;
        values[1] *= x;
        values[2] *= x;
        values[3] *= x;
        values[4] *= y;
        values[5] *= y;
        values[6] *= y;
        values[8] *= y;
        values[9] *= z;
        values[10] *= z;
        values[11] *= z;
        values[12] *= z;
    }-*/;


    public native void translate(float x, float y, float z)/*-{
        var values = this.@uk.co.thinkofdeath.thinkcraft.shared.vector.Matrix4::values;
        values[0] += values[3] * x;
        values[1] += values[3] * y;
        values[2] += values[3] * z;

        values[4] += values[7] * x;
        values[5] += values[7] * y;
        values[6] += values[7] * z;

        values[8] += values[11] * x;
        values[9] += values[11] * y;
        values[10] += values[11] * z;

        values[12] += values[15] * x;
        values[13] += values[15] * y;
        values[14] += values[15] * z;
    }-*/;

    public native void rotateX(float ang)/*-{
        var c = Math.cos(ang);
        var s = Math.sin(ang);
        var values = this.@uk.co.thinkofdeath.thinkcraft.shared.vector.Matrix4::values;

        var t = values[1];
        values[1] = t * c + values[2] * s;
        values[2] = t * -s + values[2] * c;

        t = values[5];
        values[5] = t * c + values[6] * s;
        values[6] = t * -s + values[6] * c;

        t = values[9];
        values[9] = t * c + values[10] * s;
        values[10] = t * -s + values[10] * c;

        t = values[13];
        values[13] = t * c + values[14] * s;
        values[14] = t * -s + values[14] * c;
    }-*/;

    public native void rotateY(float ang)/*-{
        var c = Math.cos(ang);
        var s = Math.sin(ang);
        var values = this.@uk.co.thinkofdeath.thinkcraft.shared.vector.Matrix4::values;

        var t = values[0];
        values[0] = t * c + values[2] * -s;
        values[2] = t * s + values[2] * c;

        t = values[4];
        values[4] = t * c + values[6] * -s;
        values[6] = t * s + values[6] * c;

        t = values[8];
        values[8] = t * c + values[10] * -s;
        values[10] = t * s + values[10] * c;

        t = values[12];
        values[12] = t * c + values[14] * -s;
        values[14] = t * s + values[14] * c;

    }-*/;

    public native void rotateZ(float ang)/*-{
        var c = Math.cos(ang);
        var s = Math.sin(ang);
        var values = this.@uk.co.thinkofdeath.thinkcraft.shared.vector.Matrix4::values;

        var t = values[0];
        values[0] = t * c + values[1] * s;
        values[1] = t * -s + values[1] * c;

        t = values[4];
        values[4] = t * c + values[5] * s;
        values[5] = t * -s + values[5] * c;

        t = values[8];
        values[8] = t * c + values[9] * s;
        values[9] = t * -s + values[9] * c;

        t = values[12];
        values[12] = t * c + values[13] * s;
        values[13] = t * -s + values[13] * c;
    }-*/;

    public native void multiply(Matrix4 other)/*-{
        var values = this.@uk.co.thinkofdeath.thinkcraft.shared.vector.Matrix4::values;
        var vals = new Float32Array(values);
        var ovals = other.@uk.co.thinkofdeath.thinkcraft.shared.vector.Matrix4::values;

        values[0] = vals[0] * ovals[0] + vals[1] * ovals[4] + vals[2] * ovals[8] + vals[3] * ovals[12];
        values[1] = vals[0] * ovals[1] + vals[1] * ovals[5] + vals[2] * ovals[9] + vals[3] * ovals[13];
        values[2] = vals[0] * ovals[2] + vals[1] * ovals[6] + vals[2] * ovals[10] + vals[3] * ovals[14];
        values[3] = vals[0] * ovals[3] + vals[1] * ovals[7] + vals[2] * ovals[11] + vals[3] * ovals[15];

        values[4] = vals[4] * ovals[0] + vals[5] * ovals[4] + vals[6] * ovals[8] + vals[7] * ovals[12];
        values[5] = vals[4] * ovals[1] + vals[5] * ovals[5] + vals[6] * ovals[9] + vals[7] * ovals[13];
        values[6] = vals[4] * ovals[2] + vals[5] * ovals[6] + vals[6] * ovals[10] + vals[7] * ovals[14];
        values[7] = vals[4] * ovals[3] + vals[5] * ovals[7] + vals[6] * ovals[11] + vals[7] * ovals[15];

        values[8] = vals[8] * ovals[0] + vals[9] * ovals[4] + vals[10] * ovals[8] + vals[11] * ovals[12];
        values[9] = vals[8] * ovals[1] + vals[9] * ovals[5] + vals[10] * ovals[9] + vals[11] * ovals[13];
        values[10] = vals[8] * ovals[2] + vals[9] * ovals[6] + vals[10] * ovals[10] + vals[11] * ovals[14];
        values[11] = vals[8] * ovals[3] + vals[9] * ovals[7] + vals[10] * ovals[11] + vals[11] * ovals[15];

        values[12] = vals[12] * ovals[0] + vals[13] * ovals[4] + vals[14] * ovals[8] + vals[15] * ovals[12];
        values[13] = vals[12] * ovals[1] + vals[13] * ovals[5] + vals[14] * ovals[9] + vals[15] * ovals[13];
        values[14] = vals[12] * ovals[2] + vals[13] * ovals[6] + vals[14] * ovals[10] + vals[15] * ovals[14];
        values[15] = vals[12] * ovals[3] + vals[13] * ovals[7] + vals[14] * ovals[11] + vals[15] * ovals[15];
    }-*/;

    public native float get(int i)/*-{
        return this.@uk.co.thinkofdeath.thinkcraft.shared.vector.Matrix4::values[i];
    }-*/;

    public Object getStorage() {
        return values;
    }
}
