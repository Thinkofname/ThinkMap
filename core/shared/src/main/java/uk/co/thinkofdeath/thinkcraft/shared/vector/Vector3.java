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

public class Vector3 {

    float[] values = new float[3];

    public Vector3() {

    }

    public void apply(Matrix4 matrix) {
        float x = values[0];
        float y = values[1];
        float z = values[2];
        float w = matrix.get(3) * x + matrix.get(7) * y + matrix.get(11) * z + matrix.get(15);
        w = w == 0 ? 1 : w;

        values[0] = (matrix.get(0) * x + matrix.get(4) * y + matrix.get(8) * z + matrix.get(12)) / w;
        values[1] = (matrix.get(1) * x + matrix.get(5) * y + matrix.get(9) * z + matrix.get(13)) / w;
        values[2] = (matrix.get(2) * x + matrix.get(6) * y + matrix.get(10) * z + matrix.get(14)) / w;
    }

    public void set(float x, float y, float z) {
        values[0] = x;
        values[1] = y;
        values[2] = z;
    }

    public float getX() {
        return values[0];
    }

    public float getY() {
        return values[1];
    }

    public float getZ() {
        return values[2];
    }
}
