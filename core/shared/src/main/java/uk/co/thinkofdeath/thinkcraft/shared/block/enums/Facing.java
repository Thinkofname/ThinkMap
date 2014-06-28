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

public enum Facing {
    DOWN(0),
    UP(1),
    SOUTH(0, 1),
    WEST(1, 2),
    NORTH(2, 0),
    EAST(3, 3);

    private final int clockwiseRotation;
    private final int nsweOrder;
    private final int dunsweOrder;

    Facing(int dunsweOrder) {
        clockwiseRotation = -1;
        nsweOrder = -1;
        this.dunsweOrder = dunsweOrder;
    }

    Facing(int clockwiseRotation, int nsweOrder) {
        this.clockwiseRotation = clockwiseRotation;
        this.nsweOrder = nsweOrder;
        this.dunsweOrder = 2 + nsweOrder;
    }

    public int getClockwiseRotation() {
        return clockwiseRotation;
    }

    public int getNSWEOrder() {
        return nsweOrder;
    }

    public int getDUNSWEOrder() {
        return dunsweOrder;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
