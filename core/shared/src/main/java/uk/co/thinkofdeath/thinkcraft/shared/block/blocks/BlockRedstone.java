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
import uk.co.thinkofdeath.thinkcraft.shared.ForEachIterator;
import uk.co.thinkofdeath.thinkcraft.shared.IMapViewer;
import uk.co.thinkofdeath.thinkcraft.shared.Texture;
import uk.co.thinkofdeath.thinkcraft.shared.block.Block;
import uk.co.thinkofdeath.thinkcraft.shared.block.BlockFactory;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.IntegerState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelVertex;
import uk.co.thinkofdeath.thinkcraft.shared.world.World;

public class BlockRedstone extends BlockFactory {

    public final StateKey<Integer> POWER = stateAllocator.alloc("power", new IntegerState(0, 15));
    public final StateKey<Side> NORTH = stateAllocator.alloc("north", new EnumState<>(Side.class));
    public final StateKey<Side> SOUTH = stateAllocator.alloc("south", new EnumState<>(Side.class));
    public final StateKey<Side> EAST = stateAllocator.alloc("east", new EnumState<>(Side.class));
    public final StateKey<Side> WEST = stateAllocator.alloc("west", new EnumState<>(Side.class));

    private final Texture line;
    private final Texture cross;

    public BlockRedstone(IMapViewer iMapViewer) {
        super(iMapViewer);

        line = mapViewer.getTexture("redstone_dust_line");
        cross = mapViewer.getTexture("redstone_dust_cross");
    }

    public enum Side {
        NONE,
        SIDE,
        UP;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }


    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockRedstone.this, state);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                int power = getState(POWER);
                int brightness = (int) ((255d / 30d) * (power + 14d));

                model = new Model();

                Side north = getState(NORTH);
                Side south = getState(SOUTH);
                Side east = getState(EAST);
                Side west = getState(WEST);

                if ((east != Side.NONE || west != Side.NONE)
                        && north == Side.NONE && south == Side.NONE) {
                    model.addFace(new ModelFace(Face.TOP, line, 0, 0, 16, 16, 0.5f)
                            .colour(brightness, 0, 0));
                } else if ((north != Side.NONE || south != Side.NONE)
                        && east == Side.NONE && west == Side.NONE) {
                    model.addFace(new ModelFace(Face.TOP, line, 0, 0, 16, 16, 0.5f)
                            .colour(brightness, 0, 0).forEach(swap));
                } else {
                    float x = 5;
                    float y = 5;
                    float w = 6;
                    float h = 6;
                    if (west != Side.NONE) {
                        x = 0;
                        w += 5;
                    }
                    if (east != Side.NONE) {
                        w += 5;
                    }
                    if (north != Side.NONE) {
                        y = 0;
                        h += 5;
                    }
                    if (south != Side.NONE) {
                        h += 5;
                    }
                    model.addFace(new ModelFace(Face.TOP, cross, x, y, w, h, 0.5f)
                            .colour(brightness, 0, 0));
                }

                if (east == Side.UP) {
                    model.addFace(new ModelFace(Face.RIGHT, line, 0, 0, 16, 16, 15.5f)
                            .colour(brightness, 0, 0).forEach(swap));
                }
                if (west == Side.UP) {
                    model.addFace(new ModelFace(Face.LEFT, line, 0, 0, 16, 16, 0.5f)
                            .colour(brightness, 0, 0).forEach(swap));
                }

                if (south == Side.UP) {
                    model.addFace(new ModelFace(Face.BACK, line, 0, 0, 16, 16, 15.5f)
                            .colour(brightness, 0, 0).forEach(swap));
                }
                if (north == Side.UP) {
                    model.addFace(new ModelFace(Face.FRONT, line, 0, 0, 16, 16, 0.5f)
                            .colour(brightness, 0, 0).forEach(swap));
                }
            }
            return model;
        }

        @Override
        public Block update(World world, int x, int y, int z) {
            StateMap map = new StateMap();
            map.set(POWER, getState(POWER));

            boolean blocked = world.getBlock(x, y + 1, z).isSolid();

            map.set(NORTH, checkRedstone(world, x, y, z - 1, blocked));
            map.set(SOUTH, checkRedstone(world, x, y, z + 1, blocked));
            map.set(EAST, checkRedstone(world, x + 1, y, z, blocked));
            map.set(WEST, checkRedstone(world, x - 1, y, z, blocked));

            return mapViewer.getBlockRegistry().get(fullName, map);
        }

        private Side checkRedstone(World world, int x, int y, int z, boolean blocked) {
            Block block = world.getBlock(x, y, z);
            if (block instanceof BlockImpl) {
                return Side.SIDE;
            }
            Block other = world.getBlock(x, y - 1, z);
            if (!block.isSolid() && other instanceof BlockImpl) {
                return Side.SIDE;
            }
            if (!blocked) {
                block = world.getBlock(x, y + 1, z);
                if (block instanceof BlockImpl) {
                    return Side.UP;
                }
            }
            return Side.NONE;
        }

        @Override
        public int getLegacyData() {
            if (getState(NORTH) == Side.NONE
                    && getState(SOUTH) == Side.NONE
                    && getState(EAST) == Side.NONE
                    && getState(WEST) == Side.NONE) {
                return getState(POWER);
            }
            return -1;
        }
    }

    private static final ForEachIterator<ModelVertex> swap = new ForEachIterator<ModelVertex>() {
        @Override
        public void run(ModelVertex v) {
            float x = v.getTextureX();
            v.setTextureX(v.getTextureY());
            v.setTextureY(x);
        }
    };
}
