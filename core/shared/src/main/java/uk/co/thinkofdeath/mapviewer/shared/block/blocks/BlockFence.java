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
import uk.co.thinkofdeath.mapviewer.shared.block.states.StateKey;
import uk.co.thinkofdeath.mapviewer.shared.block.states.StateMap;
import uk.co.thinkofdeath.mapviewer.shared.model.Model;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelFace;
import uk.co.thinkofdeath.mapviewer.shared.world.World;

public class BlockFence extends BlockFactory {

    public final StateKey<Boolean> NORTH = stateAllocator.alloc("north", new BooleanState());
    public final StateKey<Boolean> SOUTH = stateAllocator.alloc("south", new BooleanState());
    public final StateKey<Boolean> EAST = stateAllocator.alloc("east", new BooleanState());
    public final StateKey<Boolean> WEST = stateAllocator.alloc("west", new BooleanState());

    public BlockFence(IMapViewer iMapViewer) {
        super(iMapViewer);
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private static Model createJoin(Texture texture) {
        Model model = new Model();
        model.addFace(new ModelFace(Face.TOP, texture, 7, 0, 2, 6, 15));
        model.addFace(new ModelFace(Face.BOTTOM, texture, 7, 0, 2, 6, 12));
        model.addFace(new ModelFace(Face.LEFT, texture, 0, 12, 6, 3, 9));
        model.addFace(new ModelFace(Face.RIGHT, texture, 0, 12, 6, 3, 7));
        return model;
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockFence.this, state);
        }

        @Override
        public int getLegacyData() {
            boolean north = getState(NORTH);
            boolean south = getState(SOUTH);
            boolean east = getState(EAST);
            boolean west = getState(WEST);
            return north && south && east && west ? 0 : -1;
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();
                Texture texture = getTexture(Face.TOP);

                // Base
                model.addFace(new ModelFace(Face.TOP, texture, 6, 6, 4, 4, 16, true));
                model.addFace(new ModelFace(Face.BOTTOM, texture, 6, 6, 4, 4, 0, true));
                model.addFace(new ModelFace(Face.LEFT, texture, 6, 0, 4, 16, 10));
                model.addFace(new ModelFace(Face.RIGHT, texture, 6, 0, 4, 16, 6));
                model.addFace(new ModelFace(Face.FRONT, texture, 6, 0, 4, 16, 10));
                model.addFace(new ModelFace(Face.BACK, texture, 6, 0, 4, 16, 6));


                boolean north = getState(NORTH);
                boolean south = getState(SOUTH);
                boolean east = getState(EAST);
                boolean west = getState(WEST);

                if (north) {
                    model.join(createJoin(texture));
                    model.join(createJoin(texture), 0, -6, 0);
                }
                if (south) {
                    model.join(createJoin(texture).rotateY(180));
                    model.join(createJoin(texture).rotateY(180), 0, -6, 0);
                }
                if (east) {
                    model.join(createJoin(texture).rotateY(90));
                    model.join(createJoin(texture).rotateY(90), 0, -6, 0);
                }
                if (west) {
                    model.join(createJoin(texture).rotateY(270));
                    model.join(createJoin(texture).rotateY(270), 0, -6, 0);
                }

                model.realignTextures();
            }
            return model;
        }

        @Override
        public Block update(World world, int x, int y, int z) {
            StateMap map = new StateMap();

            map.set(NORTH, checkFence(world, x, y, z - 1));
            map.set(SOUTH, checkFence(world, x, y, z + 1));
            map.set(EAST, checkFence(world, x + 1, y, z));
            map.set(WEST, checkFence(world, x - 1, y, z));

            return mapViewer.getBlockRegistry().get(fullName, map);
        }

        public boolean checkFence(World world, int x, int y, int z) {
            Block block = world.getBlock(x, y, z);
            if (block instanceof BlockImpl) {
                BlockImpl fence = (BlockImpl) block;
                return fence.factory == this.factory;
            }
            return false;
        }
    }
}
