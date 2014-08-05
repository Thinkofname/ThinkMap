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

public class BlockFenceGate extends BlockFactory {

    public final StateKey<Facing> FACING = stateAllocator.alloc("facing", new EnumState<>(Facing.class, new NoVerticalFacing()));
    public final StateKey<Boolean> OPEN = stateAllocator.alloc("open", new BooleanState());

    private final Texture texture;

    public BlockFenceGate(IMapViewer iMapViewer) {
        super(iMapViewer);

        texture = mapViewer.getTexture("planks_oak");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockFenceGate.this, state);
            if (getState(OPEN)) {
                collidable = false;
            }
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                Model door = new Model();

                // Post 1
                door.addFace(new ModelFace(Face.TOP, texture, 7, 7, 2, 2, 16));
                door.addFace(new ModelFace(Face.BOTTOM, texture, 7, 7, 2, 2, 5));
                door.addFace(new ModelFace(Face.LEFT, texture, 7, 5, 2, 11, 9));
                door.addFace(new ModelFace(Face.RIGHT, texture, 7, 5, 2, 11, 7));
                door.addFace(new ModelFace(Face.FRONT, texture, 7, 5, 2, 11, 9));
                door.addFace(new ModelFace(Face.BACK, texture, 7, 5, 2, 11, 7));

                // Post 2
                door.addFace(new ModelFace(Face.TOP, texture, 13, 7, 2, 2, 15));
                door.addFace(new ModelFace(Face.BOTTOM, texture, 13, 7, 2, 2, 6));
                door.addFace(new ModelFace(Face.LEFT, texture, 7, 6, 2, 9, 15));
                door.addFace(new ModelFace(Face.RIGHT, texture, 7, 6, 2, 9, 13));
                door.addFace(new ModelFace(Face.FRONT, texture, 13, 6, 2, 9, 9));
                door.addFace(new ModelFace(Face.BACK, texture, 13, 6, 2, 9, 7));

                // Bar 1
                door.addFace(new ModelFace(Face.TOP, texture, 9, 7, 4, 2, 15));
                door.addFace(new ModelFace(Face.BOTTOM, texture, 9, 7, 4, 2, 12));
                door.addFace(new ModelFace(Face.FRONT, texture, 9, 12, 4, 3, 9));
                door.addFace(new ModelFace(Face.BACK, texture, 9, 12, 4, 3, 7));

                // Bar 2
                door.addFace(new ModelFace(Face.TOP, texture, 9, 7, 4, 2, 9));
                door.addFace(new ModelFace(Face.BOTTOM, texture, 9, 7, 4, 2, 6));
                door.addFace(new ModelFace(Face.FRONT, texture, 9, 6, 4, 3, 9));
                door.addFace(new ModelFace(Face.BACK, texture, 9, 6, 4, 3, 7));

                boolean open = getState(OPEN);
                model.join(door.duplicate().rotateY(open ? 90 : 0), -7, 0, 0);
                model.join(door.duplicate().rotateY(180 - (open ? 90 : 0)), 7, 0, 0);
                model.rotateY(getState(FACING).getClockwiseRotation() * 90);
                model.realignTextures();
            }
            return model;
        }

        @Override
        public int getLegacyData() {
            int val = getState(FACING).getClockwiseRotation();
            if (getState(OPEN)) {
                val |= 0x4;
            }
            return val;
        }

    }
}
