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
import uk.co.thinkofdeath.mapviewer.shared.block.states.BooleanState;
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;
import uk.co.thinkofdeath.mapviewer.shared.block.states.StateKey;
import uk.co.thinkofdeath.mapviewer.shared.block.states.StateMap;
import uk.co.thinkofdeath.mapviewer.shared.model.Model;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelFace;

public class BlockTrapdoor extends BlockFactory {

    public final StateKey<Facing> FACING = stateAllocator.alloc("facing", new EnumState<>(Facing.class));
    public final StateKey<Boolean> TOP = stateAllocator.alloc("top", new BooleanState());
    public final StateKey<Boolean> OPEN = stateAllocator.alloc("open", new BooleanState());

    private final Texture texture;

    public BlockTrapdoor(IMapViewer iMapViewer) {
        super(iMapViewer);

        texture = mapViewer.getTexture("trapdoor");
    }

    public static enum Facing {
        SOUTH(3),
        NORTH(1),
        EAST(2),
        WEST(0);

        public final int rotation;

        Facing(int rotation) {
            this.rotation = rotation;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockTrapdoor.this, state);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                if (!getState(OPEN)) {
                    model.addFace(new ModelFace(Face.TOP, texture, 0, 0, 16, 16, 3));
                    model.addFace(new ModelFace(Face.BOTTOM, texture, 0, 0, 16, 16, 0, true));
                    model.addFace(new ModelFace(Face.LEFT, texture, 0, 0, 16, 3, 16, true));
                    model.addFace(new ModelFace(Face.RIGHT, texture, 0, 0, 16, 3, 0, true));
                    model.addFace(new ModelFace(Face.FRONT, texture, 0, 0, 16, 3, 16, true));
                    model.addFace(new ModelFace(Face.BACK, texture, 0, 0, 16, 3, 0, true));
                } else {
                    model.addFace(new ModelFace(Face.LEFT, texture, 0, 0, 16, 16, 3));
                    model.addFace(new ModelFace(Face.RIGHT, texture, 0, 0, 16, 16, 0, true));
                    model.addFace(new ModelFace(Face.TOP, texture, 0, 0, 3, 16, 16, true));
                    model.addFace(new ModelFace(Face.BOTTOM, texture, 0, 0, 3, 16, 0, true));
                    model.addFace(new ModelFace(Face.FRONT, texture, 0, 0, 3, 16, 16, true));
                    model.addFace(new ModelFace(Face.BACK, texture, 0, 0, 3, 16, 0, true));
                }

                if (getState(TOP)) {
                    model.flipModel();
                }

                model.rotateY(getState(FACING).rotation * 90);
                model.realignTextures();
            }
            return model;
        }

        @Override
        public int getLegacyData() {
            int val = getState(FACING).ordinal();
            if (getState(TOP)) {
                val |= 0x8;
            }
            if (getState(OPEN)) {
                val |= 0x4;
            }
            return val;
        }
    }
}
