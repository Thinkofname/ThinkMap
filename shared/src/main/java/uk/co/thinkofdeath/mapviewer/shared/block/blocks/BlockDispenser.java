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
import uk.co.thinkofdeath.mapviewer.shared.block.StateMap;
import uk.co.thinkofdeath.mapviewer.shared.block.states.BooleanState;
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;

public class BlockDispenser extends BlockFactory {

    public static final String FACING = "facing";
    public static final String TRIGGERED = "triggered";

    private final Texture furnaceTop;
    private final Texture furnaceSide;
    private final Texture frontVertical;
    private final Texture frontHorizontal;

    public BlockDispenser(IMapViewer iMapViewer, String textureName) {
        super(iMapViewer);
        addState(FACING, new EnumState(Facing.class));
        addState(TRIGGERED, new BooleanState());
        furnaceTop = iMapViewer.getTexture("furnace_top");
        furnaceSide = iMapViewer.getTexture("furnace_side");
        frontVertical = iMapViewer.getTexture(textureName + "_front_vertical");
        frontHorizontal = iMapViewer.getTexture(textureName + "_front_horizontal");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
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

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockDispenser.this, state);
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
        public Texture getTexture(Face face) {
            Facing facing = getState(FACING);
            switch (face) {
                case TOP:
                    if (facing == Facing.UP) {
                        return frontVertical;
                    }
                    return furnaceTop;
                case BOTTOM:
                    if (facing == Facing.DOWN) {
                        return frontVertical;
                    }
                    return furnaceTop;
                case LEFT:
                    if (facing == Facing.EAST) {
                        return frontHorizontal;
                    }
                    break;
                case RIGHT:
                    if (facing == Facing.WEST) {
                        return frontHorizontal;
                    }
                    break;
                case FRONT:
                    if (facing == Facing.SOUTH) {
                        return frontHorizontal;
                    }
                    break;
                case BACK:
                    if (facing == Facing.NORTH) {
                        return frontHorizontal;
                    }
                    break;
            }
            return furnaceSide;
        }
    }
}
