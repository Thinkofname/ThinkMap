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

package uk.co.thinkofdeath.mapviewer.client.world;

import uk.co.thinkofdeath.mapviewer.client.render.Camera;

class ChunkArraySorter implements java.util.Comparator<int[]> {

    private final Camera camera;

    public ChunkArraySorter(Camera camera) {
        this.camera = camera;
    }

    @Override
    public int compare(int[] ints, int[] ints2) {
        int dx1 = (int) (ints[0] * 16 - camera.getX());
        int dz1 = (int) (ints[1] * 16 - camera.getZ());
        int dx2 = (int) (ints2[0] * 16 - camera.getX());
        int dz2 = (int) (ints2[1] * 16 - camera.getZ());
        return (dx1 * dx1 + dz1 * dz1) - (dx2 * dx2 + dz2 * dz2);
    }
}
