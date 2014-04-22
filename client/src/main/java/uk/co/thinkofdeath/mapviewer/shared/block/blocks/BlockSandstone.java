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
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockFactory;
import uk.co.thinkofdeath.mapviewer.shared.block.StateMap;
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;

public class BlockSandstone extends BlockFactory {

    public static final String VARIANT = "variant";

    public BlockSandstone() {
        addState(VARIANT, new EnumState(Variant.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(this, states);
    }

    public static enum Variant {
        DEFAULT("normal"),
        CHISELED("carved"),
        SMOOTH("smooth");

        private final String texture;

        Variant(String texture) {
            this.texture = texture;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private static class BlockImpl extends Block {

        BlockImpl(BlockFactory factory, StateMap state) {
            super(factory, state);
        }

        @Override
        public int getLegacyData() {
            return this.<Variant>getState(VARIANT).ordinal();
        }

        @Override
        public String getTexture(Face face) {
            if (face == Face.TOP) {
                return "sandstone_top";
            } else if (face == Face.BOTTOM) {
                return "sandstone_bottom";
            }
            return "sandstone_" + this.<Variant>getState(VARIANT).texture;
        }
    }
}
