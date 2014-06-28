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

public enum StoneSlabType implements SlabType {
    STONE("", false) {
        @Override
        public String texture(Face face) {
            return face == Face.TOP || face == Face.BOTTOM ?
                    "stone_slab_top" :
                    "stone_slab_side";
        }
    },
    SANDSTONE("", false) {
        @Override
        public String texture(Face face) {
            return face == Face.TOP || face == Face.BOTTOM ?
                    "sandstone_top" :
                    "sandstone_normal";
        }
    },
    WOODEN("planks_oak", false),
    COBBLESTONE("cobblestone", false),
    BRICK("brick", false),
    STONE_BRICK("stonebrick", false),
    NETHER_BRICK("nether_brick", false),
    QUARTZ("", false) {
        @Override
        public String texture(Face face) {
            return face == Face.TOP || face == Face.BOTTOM ?
                    "quartz_block_top" :
                    "quartz_block_side";
        }
    },
    SMOOTH_STONE("stone_slab_top", true),
    SMOOTH_SANDSTONE("sandstone_top", true);

    private final String texture;
    private final boolean isDouble;

    StoneSlabType(String texture, boolean isDouble) {
        this.texture = texture;
        this.isDouble = isDouble;
    }

    @Override
    public String texture(Face face) {
        return texture;
    }

    @Override
    public boolean isDouble() {
        return isDouble;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
