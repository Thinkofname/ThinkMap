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

package uk.co.thinkofdeath.thinkcraft.shared.block.enums;

public enum LeverDirection {
    CEILING_EAST(1, 0, 1),
    WALL_EAST(1),
    WALL_WEST(3),
    WALL_SOUTH(2),
    WALL_NORTH(0),
    FLOOR_SOUTH(3, 0, 0),
    FLOOR_EAST(3, 0, 1),
    CEILING_SOUTH(1, 0, 2);

    private final int rotationX;
    private final int rotationY;
    private final int rotationZ;

    LeverDirection(int rotation) {
        this(0, rotation, 0);
    }

    LeverDirection(int rotationX, int rotationY, int rotationZ) {
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
    }

    public int getRotationX() {
        return rotationX;
    }

    public int getRotationY() {
        return rotationY;
    }

    public int getRotationZ() {
        return rotationZ;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
