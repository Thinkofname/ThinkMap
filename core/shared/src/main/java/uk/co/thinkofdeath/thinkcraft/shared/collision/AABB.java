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

package uk.co.thinkofdeath.thinkcraft.shared.collision;

import uk.co.thinkofdeath.thinkcraft.shared.vector.Vector3;

public class AABB {

    private double x1;
    private double y1;
    private double z1;
    private double x2;
    private double y2;
    private double z2;

    public AABB(double x1, double y1, double z1,
                double x2, double y2, double z2) {
        set(x1, y1, z1, x2, y2, z2);
    }

    public boolean intersects(AABB other) {
        return !(other.x1 > x2 || other.x2 < x1
                || other.y1 > y2 || other.y2 < y1
                || other.z1 > z2 || other.z2 < z1);
    }

    public boolean intersectsOffset(AABB other, double x, double y, double z) {
        return !(other.x1 > x + x2 || other.x2 < x + x1
                || other.y1 > y + y2 || other.y2 < y + y1
                || other.z1 > z + z2 || other.z2 < z + z1);
    }

    public void moveOutOf(AABB other, Vector3 direction) {
        moveOutOf(other, 0, 0, 0, direction);
    }

    public void moveOutOf(AABB other, double x, double y, double z, Vector3 direction) {
        if (direction.getX() != 0) {
            if (direction.getX() > 0) {
                double ox = x2;
                x2 = x + other.x1 - 0.0001;
                x1 += x2 - ox;
            } else {
                double ox = x1;
                x1 = x + other.x2 + 0.0001;
                x2 += x1 - ox;
            }
        }

        if (direction.getY() != 0) {
            if (direction.getY() > 0) {
                double oy = y2;
                y2 = y + other.y1 - 0.0001;
                y1 += y2 - oy;
            } else {
                double oy = y1;
                y1 = y + other.y2 + 0.0001;
                y2 += y1 - oy;
            }
        }

        if (direction.getZ() != 0) {
            if (direction.getZ() > 0) {
                double oz = z2;
                z2 = z + other.z1 - 0.0001;
                z1 += z2 - oz;
            } else {
                double oz = z1;
                z1 = z + other.z2 + 0.0001;
                z2 += z1 - oz;
            }
        }
    }

    public void set(double x1, double y1, double z1,
                    double x2, double y2, double z2) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    public void setZ1(double z1) {
        this.z1 = z1;
    }

    public void setX2(double x2) {
        this.x2 = x2;
    }

    public void setY2(double y2) {
        this.y2 = y2;
    }

    public void setZ2(double z2) {
        this.z2 = z2;
    }

    public double getX1() {
        return x1;
    }

    public double getY1() {
        return y1;
    }

    public double getZ1() {
        return z1;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }

    public double getZ2() {
        return z2;
    }
}
