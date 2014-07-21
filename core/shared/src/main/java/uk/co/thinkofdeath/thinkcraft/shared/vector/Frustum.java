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

public class Frustum {

    private final float[][] planes = new float[6][4];

    public void fromMatrix(Matrix4 matrix) {
        for (int i = 0; i < 6; i++) {
            int off = i >> 1;
            float a = planes[i][0] = matrix.get(3) - matrix.get(off);
            float b = planes[i][1] = matrix.get(7) - matrix.get(4 + off);
            float c = planes[i][2] = matrix.get(11) - matrix.get(8 + off);
            planes[i][3] = matrix.get(15) - matrix.get(12 + off);
            double t = Math.sqrt(a * a + b * b + c * c);
            planes[i][0] /= t;
            planes[i][1] /= t;
            planes[i][2] /= t;
            planes[i][3] /= t;
        }
    }

    public boolean isSphereInside(float x, float y, float z, float radius) {
        for (int i = 0; i < 6; i++) {
            if (planes[i][0] * x + planes[i][1] * y + planes[i][2] * z + planes[i][3] <= -radius) {
                return false;
            }
        }
        return true;
    }
}
