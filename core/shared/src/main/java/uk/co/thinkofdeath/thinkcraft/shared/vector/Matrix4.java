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

import uk.co.thinkofdeath.thinkcraft.shared.platform.Platform;
import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.FloatBuffer;

public class Matrix4 {

    private FloatBuffer buffer = Platform.alloc().floatBuffer(4 * 4);

    public Matrix4() {
        identity();
    }

    public void identity() {
        for (int i = 0; i < 16; i++) {
            buffer.set(i, 0);
        }
        buffer.set(k(0, 0), 1);
        buffer.set(k(1, 1), 1);
        buffer.set(k(2, 2), 1);
        buffer.set(k(3, 3), 1);
    }

    private static int k(int x, int y) {
        return y + x * 4;
    }

    public void perspective(float fovy, float aspect, float near, float far) {
        float invDepth = 1 / (near - far);

        buffer.set(k(1, 1), (float) (1 / Math.tan(0.5f * fovy)));
        buffer.set(k(0, 0), buffer.get(k(1, 1)) / aspect);
        buffer.set(k(2, 2), (far + near) * invDepth);
        buffer.set(k(3, 2), 2 * (far * near) * invDepth);
        buffer.set(k(2, 3), -1);
        buffer.set(k(3, 3), 0);
    }

    public void scale(float x, float y, float z) {
        buffer.set(0, buffer.get(0) * x);
        buffer.set(1, buffer.get(1) * x);
        buffer.set(2, buffer.get(2) * x);
        buffer.set(3, buffer.get(3) * x);
        buffer.set(4, buffer.get(4) * y);
        buffer.set(5, buffer.get(5) * y);
        buffer.set(6, buffer.get(6) * y);
        buffer.set(8, buffer.get(8) * y);
        buffer.set(9, buffer.get(9) * z);
        buffer.set(10, buffer.get(10) * z);
        buffer.set(11, buffer.get(11) * z);
        buffer.set(12, buffer.get(12) * z);
    }

    public void translate(float x, float y, float z) {
        buffer.set(0, buffer.get(0) + buffer.get(3) * x);
        buffer.set(1, buffer.get(1) + buffer.get(3) * y);
        buffer.set(2, buffer.get(2) + buffer.get(3) * z);

        buffer.set(4, buffer.get(4) + buffer.get(7) * x);
        buffer.set(5, buffer.get(5) + buffer.get(7) * y);
        buffer.set(6, buffer.get(6) + buffer.get(7) * z);

        buffer.set(8, buffer.get(8) + buffer.get(11) * x);
        buffer.set(9, buffer.get(9) + buffer.get(11) * y);
        buffer.set(10, buffer.get(10) + buffer.get(11) * z);

        buffer.set(12, buffer.get(12) + buffer.get(15) * x);
        buffer.set(13, buffer.get(13) + buffer.get(15) * y);
        buffer.set(14, buffer.get(14) + buffer.get(15) * z);
    }

    public void rotateX(float ang) {
        float c = (float) Math.cos(ang);
        float s = (float) Math.sin(ang);

        float t = buffer.get(1);
        buffer.set(1, t * c + buffer.get(2) * s);
        buffer.set(2, t * -s + buffer.get(2) * c);

        t = buffer.get(5);
        buffer.set(5, t * c + buffer.get(6) * s);
        buffer.set(6, t * -s + buffer.get(6) * c);

        t = buffer.get(9);
        buffer.set(9, t * c + buffer.get(10) * s);
        buffer.set(10, t * -s + buffer.get(10) * c);

        t = buffer.get(13);
        buffer.set(13, t * c + buffer.get(14) * s);
        buffer.set(14, t * -s + buffer.get(14) * c);
    }

    public void rotateY(float ang) {
        float c = (float) Math.cos(ang);
        float s = (float) Math.sin(ang);

        float t = buffer.get(0);
        buffer.set(0, t * c + buffer.get(2) * -s);
        buffer.set(2, t * s + buffer.get(2) * c);

        t = buffer.get(4);
        buffer.set(4, t * c + buffer.get(6) * -s);
        buffer.set(6, t * s + buffer.get(6) * c);

        t = buffer.get(8);
        buffer.set(8, t * c + buffer.get(10) * -s);
        buffer.set(10, t * s + buffer.get(10) * c);

        t = buffer.get(12);
        buffer.set(12, t * c + buffer.get(14) * -s);
        buffer.set(14, t * s + buffer.get(14) * c);
    }

    public void rotateZ(float ang) {
        float c = (float) Math.cos(ang);
        float s = (float) Math.sin(ang);

        float t = buffer.get(0);
        buffer.set(0, t * c + buffer.get(1) * s);
        buffer.set(1, t * -s + buffer.get(1) * c);

        t = buffer.get(4);
        buffer.set(4, t * c + buffer.get(5) * s);
        buffer.set(5, t * -s + buffer.get(5) * c);

        t = buffer.get(8);
        buffer.set(8, t * c + buffer.get(9) * s);
        buffer.set(9, t * -s + buffer.get(9) * c);

        t = buffer.get(12);
        buffer.set(12, t * c + buffer.get(13) * s);
        buffer.set(13, t * -s + buffer.get(13) * c);
    }

    public void multiply(Matrix4 other) {
        FloatBuffer ovals = other.buffer;

        float b0 = buffer.get(0);
        float b1 = buffer.get(1);
        float b2 = buffer.get(2);
        float b3 = buffer.get(3);
        buffer.set(0, b0 * ovals.get(0) + b1 * ovals.get(4) + b2 * ovals.get(8) + b3 * ovals.get(12));
        buffer.set(1, b0 * ovals.get(1) + b1 * ovals.get(5) + b2 * ovals.get(9) + b3 * ovals.get(13));
        buffer.set(2, b0 * ovals.get(2) + b1 * ovals.get(6) + b2 * ovals.get(10) + b3 * ovals.get(14));
        buffer.set(3, b0 * ovals.get(3) + b1 * ovals.get(7) + b2 * ovals.get(11) + b3 * ovals.get(15));

        b0 = buffer.get(4);
        b1 = buffer.get(5);
        b2 = buffer.get(6);
        b3 = buffer.get(7);
        buffer.set(4, b0 * ovals.get(0) + b1 * ovals.get(4) + b2 * ovals.get(8) + b3 * ovals.get(12));
        buffer.set(5, b0 * ovals.get(1) + b1 * ovals.get(5) + b2 * ovals.get(9) + b3 * ovals.get(13));
        buffer.set(6, b0 * ovals.get(2) + b1 * ovals.get(6) + b2 * ovals.get(10) + b3 * ovals.get(14));
        buffer.set(7, b0 * ovals.get(3) + b1 * ovals.get(7) + b2 * ovals.get(11) + b3 * ovals.get(15));

        b0 = buffer.get(8);
        b1 = buffer.get(9);
        b2 = buffer.get(10);
        b3 = buffer.get(11);
        buffer.set(8, b0 * ovals.get(0) + b1 * ovals.get(4) + b2 * ovals.get(8) + b3 * ovals.get(12));
        buffer.set(9, b0 * ovals.get(1) + b1 * ovals.get(5) + b2 * ovals.get(9) + b3 * ovals.get(13));
        buffer.set(10, b0 * ovals.get(2) + b1 * ovals.get(6) + b2 * ovals.get(10) + b3 * ovals.get(14));
        buffer.set(11, b0 * ovals.get(3) + b1 * ovals.get(7) + b2 * ovals.get(11) + b3 * ovals.get(15));

        b0 = buffer.get(12);
        b1 = buffer.get(13);
        b2 = buffer.get(14);
        b3 = buffer.get(15);
        buffer.set(12, b0 * ovals.get(0) + b1 * ovals.get(4) + b2 * ovals.get(8) + b3 * ovals.get(12));
        buffer.set(13, b0 * ovals.get(1) + b1 * ovals.get(5) + b2 * ovals.get(9) + b3 * ovals.get(13));
        buffer.set(14, b0 * ovals.get(2) + b1 * ovals.get(6) + b2 * ovals.get(10) + b3 * ovals.get(14));
        buffer.set(15, b0 * ovals.get(3) + b1 * ovals.get(7) + b2 * ovals.get(11) + b3 * ovals.get(15));
    }

    public void copy(Matrix4 other) {
        for (int i = 0; i < 16; i++) {
            buffer.set(i, other.get(i));
        }
    }

    public float get(int i) {
        return buffer.get(i);
    }

    public FloatBuffer getStorage() {
        return buffer;
    }
}
