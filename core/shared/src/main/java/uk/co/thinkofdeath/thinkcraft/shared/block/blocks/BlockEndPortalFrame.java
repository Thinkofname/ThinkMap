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
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.NoVerticalFacing;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.BooleanState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;

public class BlockEndPortalFrame extends BlockFactory {

    public final StateKey<Facing> FACING = stateAllocator.alloc("facing", new EnumState<>(Facing.class, new NoVerticalFacing()));
    public final StateKey<Boolean> HAS_EYE = stateAllocator.alloc("hasEye", new BooleanState());

    private final Texture bottom;
    private final Texture side;
    private final Texture top;
    private final Texture eye;

    public BlockEndPortalFrame(IMapViewer iMapViewer) {
        super(iMapViewer);

        bottom = mapViewer.getBlockTexture("minecraft:end_stone");
        side = mapViewer.getBlockTexture("minecraft:endframe_side");
        top = mapViewer.getBlockTexture("minecraft:endframe_top");
        eye = mapViewer.getBlockTexture("minecraft:endframe_eye");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockEndPortalFrame.this, state);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                model.addFace(new ModelFace(Face.TOP, top, 0, 0, 16, 16, 13));
                model.addFace(new ModelFace(Face.BOTTOM, bottom, 0, 0, 16, 16, 0, true));

                model.addFace(new ModelFace(Face.LEFT, side, 0, 0, 16, 13, 16, true)
                        .setTextureSize(0, 3, 16, 13));
                model.addFace(new ModelFace(Face.RIGHT, side, 0, 0, 16, 13, 0, true)
                        .setTextureSize(0, 3, 16, 13));
                model.addFace(new ModelFace(Face.FRONT, side, 0, 0, 16, 13, 16, true)
                        .setTextureSize(0, 3, 16, 13));
                model.addFace(new ModelFace(Face.BACK, side, 0, 0, 16, 13, 0, true)
                        .setTextureSize(0, 3, 16, 13));

                if (getState(HAS_EYE)) {
                    model.addFace(new ModelFace(Face.TOP, eye, 4, 4, 8, 8, 16, true));

                    model.addFace(new ModelFace(Face.LEFT, eye, 4, 13, 8, 3, 12)
                            .setTextureSize(4, 0, 8, 3));
                    model.addFace(new ModelFace(Face.RIGHT, eye, 4, 13, 8, 3, 4)
                            .setTextureSize(4, 0, 8, 3));
                    model.addFace(new ModelFace(Face.FRONT, eye, 4, 13, 8, 3, 12)
                            .setTextureSize(4, 0, 8, 3));
                    model.addFace(new ModelFace(Face.BACK, eye, 4, 13, 8, 3, 4)
                            .setTextureSize(4, 0, 8, 3));
                }

                model.rotateY(getState(FACING).getClockwiseRotation() * 90);
            }
            return model;
        }

        @Override
        public int getLegacyData() {
            int val = getState(FACING).getClockwiseRotation();
            if (getState(HAS_EYE)) {
                val |= 0x4;
            }
            return val;
        }

    }
}
