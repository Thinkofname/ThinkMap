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

package uk.co.thinkofdeath.mapviewer.client.render;

import java.util.Comparator;

class ModelSorter implements Comparator<PositionedModel> {

    private final Camera camera;
    private final int cx;
    private final int cz;

    public ModelSorter(int cx, int cz, Camera camera) {
        this.camera = camera;
        this.cx = cx;
        this.cz = cz;
    }

    @Override
    public int compare(PositionedModel a, PositionedModel b) {
        double ax = (cx << 4) + a.getX() + 0.5 - camera.getX();
        double ay = a.getY() + 0.5 - camera.getY();
        double az = (cz << 4) + a.getZ() + 0.5 - camera.getZ();
        double bx = (cx << 4) + b.getX() + 0.5 - camera.getX();
        double by = b.getY() + 0.5 - camera.getY();
        double bz = (cz << 4) + b.getZ() + 0.5 - camera.getZ();
        return (int) (((bx * bx + by * by + bz * bz) - (ax * ax + ay * ay + az * az)) * 32d);
    }
}
