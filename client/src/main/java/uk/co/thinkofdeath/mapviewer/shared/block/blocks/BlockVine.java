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
import uk.co.thinkofdeath.mapviewer.shared.model.Model;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelFace;
import uk.co.thinkofdeath.mapviewer.shared.world.World;

public class BlockVine extends BlockFactory {

    public static final String EAST = "east";
    public static final String NORTH = "north";
    public static final String SOUTH = "south";
    public static final String UP = "up";
    public static final String WEST = "west";

    private final Texture texture;

    public BlockVine(IMapViewer iMapViewer) {
        super(iMapViewer);
        addState(EAST, new BooleanState());
        addState(NORTH, new BooleanState());
        addState(SOUTH, new BooleanState());
        addState(UP, new BooleanState());
        addState(WEST, new BooleanState());

        texture = iMapViewer.getTexture("vine");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    public static enum Variant {
        DEFAULT,
        GRASSLESS,
        PODZOL;

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
            super(BlockVine.this, state);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                if (this.<Boolean>getState(UP)) {
                    model.addFace(new ModelFace(Face.BOTTOM, texture, 0, 0, 16, 16, 15.9f)
                            .colour(0x87, 0xBA, 0x34));
                    model.addFace(new ModelFace(Face.TOP, texture, 0, 0, 16, 16, 15.9f, true)
                            .colour(0x87, 0xBA, 0x34));
                }

                if (this.<Boolean>getState(SOUTH)) {
                    model.addFace(new ModelFace(Face.BACK, texture, 0, 0, 16, 16, 15.9f)
                            .colour(0x87, 0xBA, 0x34));
                    model.addFace(new ModelFace(Face.FRONT, texture, 0, 0, 16, 16, 15.9f, true)
                            .colour(0x87, 0xBA, 0x34));
                }

                if (this.<Boolean>getState(NORTH)) {
                    model.addFace(new ModelFace(Face.FRONT, texture, 0, 0, 16, 16, 0.1f)
                            .colour(0x87, 0xBA, 0x34));
                    model.addFace(new ModelFace(Face.BACK, texture, 0, 0, 16, 16, 0.1f, true)
                            .colour(0x87, 0xBA, 0x34));
                }

                if (this.<Boolean>getState(EAST)) {
                    model.addFace(new ModelFace(Face.RIGHT, texture, 0, 0, 16, 16, 15.9f)
                            .colour(0x87, 0xBA, 0x34));
                    model.addFace(new ModelFace(Face.LEFT, texture, 0, 0, 16, 16, 15.9f, true)
                            .colour(0x87, 0xBA, 0x34));
                }

                if (this.<Boolean>getState(WEST)) {
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
            if (this.<Boolean>getState(UP)) {
                return -1; // Virtual block
            }
            int val = 0;
            if (this.<Boolean>getState(SOUTH)) {
                val |= 1;
            }
            if (this.<Boolean>getState(WEST)) {
                val |= 2;
            }
            if (this.<Boolean>getState(NORTH)) {
                val |= 4;
            }
            if (this.<Boolean>getState(EAST)) {
                val |= 8;
            }
            return val;
        }
    }
}
