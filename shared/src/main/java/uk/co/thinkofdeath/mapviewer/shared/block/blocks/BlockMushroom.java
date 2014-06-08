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

package uk.co.thinkofdeath.mapviewer.shared.block.blocks;

import uk.co.thinkofdeath.mapviewer.shared.Face;
import uk.co.thinkofdeath.mapviewer.shared.IMapViewer;
import uk.co.thinkofdeath.mapviewer.shared.Texture;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockFactory;
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;
import uk.co.thinkofdeath.mapviewer.shared.block.states.StateKey;
import uk.co.thinkofdeath.mapviewer.shared.block.states.StateMap;

public class BlockMushroom extends BlockFactory {

    private static final String PORES = "mushroom_block_inside";
    private static final String SKIN = "mushroom_block_skin_$type";
    private static final String STEM = "mushroom_block_skin_stem";

    public final StateKey<Variant> VARIANT = stateAllocator.alloc("variant", new EnumState<>(Variant.class));

    private final Texture[] textures = new Texture[Variant.values().length * 6];

    public BlockMushroom(IMapViewer iMapViewer, String type) {
        super(iMapViewer);

        Variant[] values = Variant.values();
        for (int i = 0; i < values.length; i++) {
            for (Face face : Face.values()) {
                textures[i * 6 + face.ordinal()] = mapViewer.getTexture(values[i].textures[face.ordinal()].replaceAll("\\$type", type));
            }
        }
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    public static enum Variant {
        PORES_ALL(0, PORES, PORES, PORES, PORES, PORES, PORES),
        CAP_TOP_WEST_NORTH(1, SKIN, PORES, PORES, SKIN, PORES, SKIN),
        CAP_TOP_NORTH(2, SKIN, PORES, PORES, PORES, PORES, SKIN),
        CAP_TOP_NORTH_EAST(3, SKIN, PORES, SKIN, PORES, PORES, SKIN),
        CAP_TOP_WEST(4, SKIN, PORES, PORES, SKIN, PORES, PORES),
        CAP_TOP(5, SKIN, PORES, PORES, PORES, PORES, PORES),
        CAP_TOP_EAST(6, SKIN, PORES, SKIN, PORES, PORES, PORES),
        CAP_TOP_SOUTH_EAST(7, SKIN, PORES, PORES, SKIN, SKIN, PORES),
        CAP_TOP_SOUTH(8, SKIN, PORES, PORES, PORES, SKIN, PORES),
        CAP_TOP_SOUTH_WEST(9, SKIN, PORES, SKIN, PORES, SKIN, PORES),
        PORES_TOP_BOTTON(10, PORES, PORES, STEM, STEM, STEM, STEM),
        CAP_ALL(14, SKIN, SKIN, SKIN, SKIN, SKIN, SKIN),
        STEM_ALL(15, STEM, STEM, STEM, STEM, STEM, STEM);

        private final int id;
        private final String[] textures = new String[6];

        Variant(int id, String top, String bottom, String left, String right, String front, String back) {
            this.id = id;
            textures[Face.TOP.ordinal()] = top;
            textures[Face.BOTTOM.ordinal()] = bottom;
            textures[Face.LEFT.ordinal()] = left;
            textures[Face.RIGHT.ordinal()] = right;
            textures[Face.FRONT.ordinal()] = front;
            textures[Face.BACK.ordinal()] = back;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockMushroom.this, state);
        }

        @Override
        public int getLegacyData() {
            return getState(VARIANT).id;
        }

        @Override
        public Texture getTexture(Face face) {
            return textures[getState(VARIANT).ordinal() * 6 + face.ordinal()];
        }
    }
}
