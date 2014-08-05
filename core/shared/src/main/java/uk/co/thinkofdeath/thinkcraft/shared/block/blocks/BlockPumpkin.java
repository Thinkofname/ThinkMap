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
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.PumpkinFacing;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;

public class BlockPumpkin extends BlockFactory {

    public final StateKey<PumpkinFacing> FACING = stateAllocator.alloc("facing", new EnumState<>(PumpkinFacing.class));

    private final Texture top;
    private final Texture side;
    private final Texture front;

    public BlockPumpkin(IMapViewer iMapViewer, boolean on) {
        super(iMapViewer);

        top = iMapViewer.getTexture("pumpkin_top");
        side = iMapViewer.getTexture("pumpkin_side");
        front = iMapViewer.getTexture("pumpkin_face_" + (on ? "on" : "off"));
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockPumpkin.this, state);
        }

        @Override
        public int getLegacyData() {
            return getState(FACING).ordinal();
        }

        @Override
        public Texture getTexture(Face face) {
            PumpkinFacing facing = getState(FACING);
            switch (face) {
                case LEFT:
                    if (facing == PumpkinFacing.EAST) {
                        return front;
                    }
                    break;
                case RIGHT:
                    if (facing == PumpkinFacing.WEST) {
                        return front;
                    }
                    break;
                case FRONT:
                    if (facing == PumpkinFacing.SOUTH) {
                        return front;
                    }
                    break;
                case BACK:
                    if (facing == PumpkinFacing.NORTH) {
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
