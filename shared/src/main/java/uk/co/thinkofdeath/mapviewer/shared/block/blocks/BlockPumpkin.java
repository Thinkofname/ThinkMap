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
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;

public class BlockPumpkin extends BlockFactory {

    public static final String FACING = "facing";

    private final Texture top;
    private final Texture side;
    private final Texture front;

    public BlockPumpkin(IMapViewer iMapViewer, boolean on) {
        super(iMapViewer);
        addState(FACING, new EnumState(Facing.class));
        top = iMapViewer.getTexture("pumpkin_top");
        side = iMapViewer.getTexture("pumpkin_side");
        front = iMapViewer.getTexture("pumpkin_face_" + (on ? "on" : "off"));
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    public static enum Facing {
        SOUTH,
        WEST,
        NORTH,
        EAST,
        NO_FACE;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockPumpkin.this, state);
        }

        @Override
        public int getLegacyData() {
            return this.<Facing>getState(FACING).ordinal();
        }

        @Override
        public Texture getTexture(Face face) {
            Facing facing = getState(FACING);
            switch (face) {
                case LEFT:
                    if (facing == Facing.EAST) {
                        return front;
                    }
                    break;
                case RIGHT:
                    if (facing == Facing.WEST) {
                        return front;
                    }
                    break;
                case FRONT:
                    if (facing == Facing.SOUTH) {
                        return front;
                    }
                    break;
                case BACK:
                    if (facing == Facing.NORTH) {
                        return front;
                    }
                    break;
                case TOP:
                case BOTTOM:
                    return top;
            }
            return side;
        }
    }
}
