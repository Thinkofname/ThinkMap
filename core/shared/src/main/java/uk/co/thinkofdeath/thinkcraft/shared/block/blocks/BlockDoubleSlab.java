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

package uk.co.thinkofdeath.thinkcraft.shared.block.blocks;

import uk.co.thinkofdeath.thinkcraft.shared.Face;
import uk.co.thinkofdeath.thinkcraft.shared.IMapViewer;
import uk.co.thinkofdeath.thinkcraft.shared.Texture;
import uk.co.thinkofdeath.thinkcraft.shared.block.Block;
import uk.co.thinkofdeath.thinkcraft.shared.block.BlockFactory;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;

public class BlockDoubleSlab<T extends Enum<T> & BlockDoubleSlab.SlabType> extends BlockFactory {

    public final StateKey<T> VARIANT;

    private final Texture[] textures;

    public BlockDoubleSlab(IMapViewer iMapViewer, Class<T> clazz) {
        super(iMapViewer);
        VARIANT = stateAllocator.alloc("variant", new EnumState<>(clazz));

        textures = new Texture[clazz.getEnumConstants().length * 6];

        int i = 0;
        for (Enum e : clazz.getEnumConstants()) {
            SlabType type = (SlabType) e;
            for (Face face : Face.values()) {
                textures[i++] = iMapViewer.getTexture(type.texture(face));
            }
        }
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    public static interface SlabType {
        int ordinal();

        String texture(Face face);
    }

    public static enum StoneSlab implements SlabType {
        STONE("") {
            @Override
            public String texture(Face face) {
                return face == Face.TOP || face == Face.BOTTOM ?
                        "stone_slab_top" :
                        "stone_slab_side";
            }
        },
        SANDSTONE("") {
            @Override
            public String texture(Face face) {
                return face == Face.TOP || face == Face.BOTTOM ?
                        "sandstone_top" :
                        "sandstone_normal";
            }
        },
        WOODEN("planks_oak"),
        COBBLESTONE("cobblestone"),
        BRICK("brick"),
        STONE_BRICK("stonebrick"),
        NETHER_BRICK("nether_brick"),
        QUARTZ("") {
            @Override
            public String texture(Face face) {
                return face == Face.TOP || face == Face.BOTTOM ?
                        "quartz_block_top" :
                        "quartz_block_side";
            }
        },
        SMOOTH_STONE("stone_slab_top"),
        SMOOTH_SANDSTONE("sandstone_top");

        private final String texture;

        StoneSlab(String texture) {
            this.texture = texture;
        }

        @Override
        public String texture(Face face) {
            return texture;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public static enum WoodenSlab implements SlabType {
        OAK("planks_oak"),
        SPRUCE("planks_spruce"),
        BIRCH("planks_birch"),
        JUNGLE("planks_jungle"),
        ACACIA("planks_acacia"),
        DARK_OAK("planks_big_oak");

        private final String texture;

        WoodenSlab(String texture) {
            this.texture = texture;
        }

        @Override
        public String texture(Face face) {
            return texture;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockDoubleSlab.this, state);
        }

        @Override
        public int getLegacyData() {
            return getState(VARIANT).ordinal();
        }

        @Override
        public Texture getTexture(Face face) {
            return textures[getState(VARIANT).ordinal() * 6 + face.ordinal()];
        }
    }
}
