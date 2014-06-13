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

package uk.co.thinkofdeath.thinkcraft.html.client.render;

import java.util.Comparator;

class ChunkSorter implements Comparator<ChunkRenderObject> {

    private final Camera camera;

    public ChunkSorter(Camera camera) {
        this.camera = camera;
    }

    @Override
    public int compare(ChunkRenderObject a, ChunkRenderObject b) {
        double ax = (a.x << 4) + 8 - camera.getX();
        double ay = (a.y << 4) + 8 - camera.getY();
        double az = (a.z << 4) + 8 - camera.getZ();
        double bx = (b.x << 4) + 8 - camera.getX();
        double by = (b.y << 4) + 8 - camera.getY();
        double bz = (b.z << 4) + 8 - camera.getZ();
        return (int) ((ax * ax + ay * ay + az * az) - (bx * bx + by * by + bz * bz));
    }
}
