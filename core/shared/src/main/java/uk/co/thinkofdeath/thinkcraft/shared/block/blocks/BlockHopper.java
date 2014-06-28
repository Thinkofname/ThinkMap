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
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;

public class BlockHopper extends BlockFactory {

    public final StateKey<Facing> FACING = stateAllocator.alloc("facing", new EnumState<>(Facing.class));
    public final StateKey<Boolean> TRIGGERED = stateAllocator.alloc("triggered", new BooleanState());

    private final Texture hopperTop;
    private final Texture hopperInside;
    private final Texture hopperOutside;

    public BlockHopper(IMapViewer iMapViewer) {
        super(iMapViewer);

        hopperTop = iMapViewer.getTexture("hopper_top");
        hopperInside = iMapViewer.getTexture("hopper_inside");
        hopperOutside = iMapViewer.getTexture("hopper_outside");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockHopper.this, state);
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
        public Model getModel() {
            if (model == null) {
                model = new Model();

                model.addFace(new ModelFace(Face.TOP, hopperTop, 0, 0, 16, 16, 16, true));
                model.addFace(new ModelFace(Face.TOP, hopperInside, 0, 0, 16, 16, 10));

                // Inside

                model.addFace(new ModelFace(Face.FRONT, hopperOutside, 2, 10, 12, 6, 2)
                        .setTextureSize(2, 0, 12, 6));
                model.addFace(new ModelFace(Face.BACK, hopperOutside, 2, 10, 12, 6, 14)
                        .setTextureSize(2, 0, 12, 6));
                model.addFace(new ModelFace(Face.LEFT, hopperOutside, 2, 10, 12, 6, 2)
                        .setTextureSize(2, 0, 12, 6));
                model.addFace(new ModelFace(Face.RIGHT, hopperOutside, 2, 10, 12, 6, 14)
                        .setTextureSize(2, 0, 12, 6));

                // Outside - top

                model.addFace(new ModelFace(Face.BACK, hopperOutside, 0, 10, 16, 6, 0, true)
                        .setTextureSize(0, 0, 16, 6));
                model.addFace(new ModelFace(Face.FRONT, hopperOutside, 0, 10, 16, 6, 16, true)
                        .setTextureSize(0, 0, 16, 6));
                model.addFace(new ModelFace(Face.RIGHT, hopperOutside, 0, 10, 16, 6, 0, true)
                        .setTextureSize(0, 0, 16, 6));
                model.addFace(new ModelFace(Face.LEFT, hopperOutside, 0, 10, 16, 6, 16, true)
                        .setTextureSize(0, 0, 16, 6));
                model.addFace(new ModelFace(Face.BOTTOM, hopperOutside, 0, 0, 16, 16, 10));

                // Outside - middle

                model.addFace(new ModelFace(Face.BACK, hopperOutside, 4, 4, 8, 6, 4)
                        .setTextureSize(4, 6, 8, 6));
                model.addFace(new ModelFace(Face.FRONT, hopperOutside, 4, 4, 8, 6, 12)
                        .setTextureSize(4, 6, 8, 6));
                model.addFace(new ModelFace(Face.RIGHT, hopperOutside, 4, 4, 8, 6, 4)
                        .setTextureSize(4, 6, 8, 6));
                model.addFace(new ModelFace(Face.LEFT, hopperOutside, 4, 4, 8, 6, 12)
                        .setTextureSize(4, 6, 8, 6));
                model.addFace(new ModelFace(Face.BOTTOM, hopperOutside, 4, 4, 8, 8, 4));


                Model spout = new Model();

                spout.addFace(new ModelFace(Face.BACK, hopperOutside, 0, 0, 4, 4, 0)
                        .setTextureSize(6, 12, 4, 4));
                spout.addFace(new ModelFace(Face.FRONT, hopperOutside, 0, 0, 4, 4, 4)
                        .setTextureSize(6, 12, 4, 4));
                spout.addFace(new ModelFace(Face.RIGHT, hopperOutside, 0, 0, 4, 4, 0)
                        .setTextureSize(6, 12, 4, 4));
                spout.addFace(new ModelFace(Face.LEFT, hopperOutside, 0, 0, 4, 4, 4)
                        .setTextureSize(6, 12, 4, 4));
                spout.addFace(new ModelFace(Face.BOTTOM, hopperOutside, 0, 0, 4, 4, 0)
                        .setTextureSize(6, 6, 4, 4));
                spout.addFace(new ModelFace(Face.TOP, hopperOutside, 0, 0, 4, 4, 4)
                        .setTextureSize(6, 6, 4, 4));

                Facing facing = getState(FACING);
                if (facing == Facing.UP || facing == Facing.DOWN) {
                    model.join(spout, 6, 0, 6);
                } else {
                    model.join(spout, 6, 4, 12).rotateY(facing.getClockwiseRotation() * 90);
                }
            }
            return model;
        }
    }
}
