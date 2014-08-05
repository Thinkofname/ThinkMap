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
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.Facing;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.BooleanState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;

public class BlockDispenser extends BlockFactory {

    public final StateKey<Facing> FACING = stateAllocator.alloc("facing", new EnumState<>(Facing.class));
    public final StateKey<Boolean> TRIGGERED = stateAllocator.alloc("triggered", new BooleanState());

    private final Texture furnaceTop;
    private final Texture furnaceSide;
    private final Texture frontVertical;
    private final Texture frontHorizontal;

    public BlockDispenser(IMapViewer iMapViewer, String textureName) {
        super(iMapViewer);

        furnaceTop = iMapViewer.getTexture("furnace_top");
        furnaceSide = iMapViewer.getTexture("furnace_side");
        frontVertical = iMapViewer.getTexture(textureName + "_front_vertical");
        frontHorizontal = iMapViewer.getTexture(textureName + "_front_horizontal");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockDispenser.this, state);
        }

        @Override
        public int getLegacyData() {
            int val = getState(FACING).getDUNSWEOrder();
            if (getState(TRIGGERED)) {
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
