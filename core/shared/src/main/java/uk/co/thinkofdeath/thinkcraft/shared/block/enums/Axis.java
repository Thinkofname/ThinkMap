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

public enum Axis {
    X(4),
    Y(0),
    Z(8),
    NONE(12);

    private final int legacy;

    Axis(int legacy) {
        this.legacy = legacy;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    /**
     * Returns the legacy value for this axis
     *
     * @return The legacy value
     */
    public int getLegacy() {
        return legacy;
    }
}
