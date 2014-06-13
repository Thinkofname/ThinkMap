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
import uk.co.thinkofdeath.thinkcraft.shared.block.states.BooleanState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;
import uk.co.thinkofdeath.thinkcraft.shared.world.World;

public class BlockVine extends BlockFactory {

    public final StateKey<Boolean> EAST = stateAllocator.alloc("east", new BooleanState());
    public final StateKey<Boolean> NORTH = stateAllocator.alloc("north", new BooleanState());
    public final StateKey<Boolean> SOUTH = stateAllocator.alloc("south", new BooleanState());
    public final StateKey<Boolean> WEST = stateAllocator.alloc("west", new BooleanState());
    public final StateKey<Boolean> UP = stateAllocator.alloc("up", new BooleanState());

    private final Texture texture;

    public BlockVine(IMapViewer iMapViewer) {
        super(iMapViewer);

        texture = iMapViewer.getTexture("vine");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockVine.this, state);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                if (getState(UP)) {
                    model.addFace(new ModelFace(Face.BOTTOM, texture, 0, 0, 16, 16, 15.9f)
                            .colour(0x87, 0xBA, 0x34));
                    model.addFace(new ModelFace(Face.TOP, texture, 0, 0, 16, 16, 15.9f, true)
                            .colour(0x87, 0xBA, 0x34));
                }

                if (getState(SOUTH)) {
                    model.addFace(new ModelFace(Face.BACK, texture, 0, 0, 16, 16, 15.9f)
                            .colour(0x87, 0xBA, 0x34));
                    model.addFace(new ModelFace(Face.FRONT, texture, 0, 0, 16, 16, 15.9f, true)
                            .colour(0x87, 0xBA, 0x34));
                }

                if (getState(NORTH)) {
                    model.addFace(new ModelFace(Face.FRONT, texture, 0, 0, 16, 16, 0.1f)
                            .colour(0x87, 0xBA, 0x34));
                    model.addFace(new ModelFace(Face.BACK, texture, 0, 0, 16, 16, 0.1f, true)
                            .colour(0x87, 0xBA, 0x34));
                }

                if (getState(EAST)) {
                    model.addFace(new ModelFace(Face.RIGHT, texture, 0, 0, 16, 16, 15.9f)
                            .colour(0x87, 0xBA, 0x34));
                    model.addFace(new ModelFace(Face.LEFT, texture, 0, 0, 16, 16, 15.9f, true)
                            .colour(0x87, 0xBA, 0x34));
                }

                if (getState(WEST)) {
                    model.addFace(new ModelFace(Face.LEFT, texture, 0, 0, 16, 16, 0.1f)
                            .colour(0x87, 0xBA, 0x34));
                    model.addFace(new ModelFace(Face.RIGHT, texture, 0, 0, 16, 16, 0.1f, true)
                            .colour(0x87, 0xBA, 0x34));
                }
            }
            return model;
        }

        @Override
        public boolean shouldRenderAgainst(Block other) {
            return !other.isSolid();
        }

        @Override
        public Block update(World world, int x, int y, int z) {
            StateMap state = new StateMap(this.state);
            if (world.getBlock(x, y + 1, z).isSolid()) {
                state.set(UP, true);
            }
            return world.getMapViewer().getBlockRegistry().get("minecraft:vine", state);
        }

        @Override
        public int getLegacyData() {
            if (getState(UP)) {
                return -1; // Virtual block
            }
            int val = 0;
            if (getState(SOUTH)) {
                val |= 1;
            }
            if (getState(WEST)) {
                val |= 2;
            }
            if (getState(NORTH)) {
                val |= 4;
            }
            if (getState(EAST)) {
                val |= 8;
            }
            return val;
        }
    }
}
