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
import uk.co.thinkofdeath.mapviewer.shared.block.states.BooleanState;
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;

import java.util.Map;

public class BlockDispenser extends BlockFactory {

    public static final String FACING = "facing";
    public static final String TRIGGERED = "triggered";

    private final String textureName;

    public BlockDispenser(String textureName) {
        this.textureName = textureName;
        states.put(FACING, new EnumState(Facing.class));
        states.put(TRIGGERED, new BooleanState());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Block createBlock(Map<String, Object> states) {
        return new BlockImpl(this, states);
    }

    public static enum Facing {
        DOWN,
        UP,
        NORTH,
        SOUTH,
        WEST,
        EAST;

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private static class BlockImpl extends Block {

        private final String textureName;

        BlockImpl(BlockFactory factory, Map<String, Object> state) {
            super(factory, state);
            textureName = ((BlockDispenser) factory).textureName;
        }

        @Override
        public int getLegacyData() {
            int val = this.<Facing>getState(FACING).ordinal();
            if (this.<Boolean>getState(TRIGGERED)) {
                val |= 0x8;
            }
            return val;
        }

        @Override
        public String getTexture(Face face) {
            Facing facing = getState(FACING);
            switch (face) {
                case TOP:
                    if (facing == Facing.UP) {
                        return textureName + "_front_vertical";
                    }
                    return "furnace_top";
                case BOTTOM:
                    if (facing == Facing.DOWN) {
                        return textureName + "_front_vertical";
                    }
                    return "furnace_top";
                case LEFT:
                    if (facing == Facing.EAST) {
                        return textureName + "_front_horizontal";
                    }
                    break;
                case RIGHT:
                    if (facing == Facing.WEST) {
                        return textureName + "_front_horizontal";
                    }
                    break;
                case FRONT:
                    if (facing == Facing.SOUTH) {
                        return textureName + "_front_horizontal";
                    }
                    break;
                case BACK:
                    if (facing == Facing.NORTH) {
                        return textureName + "_front_horizontal";
                    }
                    break;
            }
            return "furnace_side";
        }
    }
}
