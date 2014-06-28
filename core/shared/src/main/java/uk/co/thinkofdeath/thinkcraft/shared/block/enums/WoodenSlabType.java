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

import uk.co.thinkofdeath.thinkcraft.shared.Face;

public enum WoodenSlabType implements SlabType {
    OAK("planks_oak"),
    SPRUCE("planks_spruce"),
    BIRCH("planks_birch"),
    JUNGLE("planks_jungle"),
    ACACIA("planks_acacia"),
    DARK_OAK("planks_big_oak");

    private final String texture;

    WoodenSlabType(String texture) {
        this.texture = texture;
    }

    @Override
    public String texture(Face face) {
        return texture;
    }

    @Override
    public boolean isDouble() {
        return false;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
