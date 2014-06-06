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

public class BlockFurnace extends BlockFactory {

    public final StateKey<Facing> FACING = stateAllocator.alloc("facing", new EnumState<>(Facing.class));

    private final Texture furnaceTop;
    private final Texture furnaceSide;
    private final Texture furnaceFront;

    public BlockFurnace(IMapViewer iMapViewer, boolean on) {
        super(iMapViewer);

        furnaceTop = iMapViewer.getTexture("furnace_top");
        furnaceSide = iMapViewer.getTexture("furnace_side");
        furnaceFront = iMapViewer.getTexture("furnace_front_" + (on ? "on" : "off"));
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    public static enum Facing {
        NORTH,
        SOUTH,
        WEST,
        EAST;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockFurnace.this, state);
        }

        @Override
        public int getLegacyData() {
            return getState(FACING).ordinal() + 2;
        }

        @Override
        public Texture getTexture(Face face) {
            Facing facing = getState(FACING);
            switch (face) {
                case LEFT:
                    if (facing == Facing.EAST) {
                        return furnaceFront;
                    }
                    break;
                case RIGHT:
                    if (facing == Facing.WEST) {
                        return furnaceFront;
                    }
                    break;
                case FRONT:
                    if (facing == Facing.SOUTH) {
                        return furnaceFront;
                    }
                    break;
                case BACK:
                    if (facing == Facing.NORTH) {
                        return furnaceFront;
                    }
                    break;
                case TOP:
                case BOTTOM:
                    return furnaceTop;
            }
            return furnaceSide;
        }
    }
}
