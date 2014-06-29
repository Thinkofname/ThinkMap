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
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.RedstoneSide;
import uk.co.thinkofdeath.thinkcraft.shared.block.helpers.RedstoneConnectible;
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
    public final StateKey<RedstoneSide> NORTH = stateAllocator.alloc("north", new EnumState<>(RedstoneSide.class));
    public final StateKey<RedstoneSide> SOUTH = stateAllocator.alloc("south", new EnumState<>(RedstoneSide.class));
    public final StateKey<RedstoneSide> EAST = stateAllocator.alloc("east", new EnumState<>(RedstoneSide.class));
    public final StateKey<RedstoneSide> WEST = stateAllocator.alloc("west", new EnumState<>(RedstoneSide.class));

    private final Texture line;
    private final Texture cross;

    public BlockRedstone(IMapViewer iMapViewer) {
        super(iMapViewer);

        line = mapViewer.getTexture("redstone_dust_line");
        cross = mapViewer.getTexture("redstone_dust_cross");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }


    private class BlockImpl extends Block implements RedstoneConnectible {

        BlockImpl(StateMap state) {
            super(BlockRedstone.this, state);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                int power = getState(POWER);
                int brightness = (int) ((255d / 30d) * (power + 14d));

                model = new Model();

                RedstoneSide north = getState(NORTH);
                RedstoneSide south = getState(SOUTH);
                RedstoneSide east = getState(EAST);
                RedstoneSide west = getState(WEST);

                if ((east != RedstoneSide.NONE || west != RedstoneSide.NONE)
                        && north == RedstoneSide.NONE && south == RedstoneSide.NONE) {
                    model.addFace(new ModelFace(Face.TOP, line, 0, 0, 16, 16, 0.5f)
                            .colour(brightness, 0, 0));
                } else if ((north != RedstoneSide.NONE || south != RedstoneSide.NONE)
                        && east == RedstoneSide.NONE && west == RedstoneSide.NONE) {
                    model.addFace(new ModelFace(Face.TOP, line, 0, 0, 16, 16, 0.5f)
                            .colour(brightness, 0, 0).forEach(swap));
                } else {
                    float x = 5;
                    float y = 5;
                    float w = 6;
                    float h = 6;
                    if (west != RedstoneSide.NONE) {
                        x = 0;
                        w += 5;
                    }
                    if (east != RedstoneSide.NONE) {
                        w += 5;
                    }
                    if (north != RedstoneSide.NONE) {
                        y = 0;
                        h += 5;
                    }
                    if (south != RedstoneSide.NONE) {
                        h += 5;
                    }
                    model.addFace(new ModelFace(Face.TOP, cross, x, y, w, h, 0.5f)
                            .colour(brightness, 0, 0));
                }

                if (east == RedstoneSide.UP) {
                    model.addFace(new ModelFace(Face.RIGHT, line, 0, 0, 16, 16, 15.5f)
                            .colour(brightness, 0, 0).forEach(swap));
                }
                if (west == RedstoneSide.UP) {
                    model.addFace(new ModelFace(Face.LEFT, line, 0, 0, 16, 16, 0.5f)
                            .colour(brightness, 0, 0).forEach(swap));
                }

                if (south == RedstoneSide.UP) {
                    model.addFace(new ModelFace(Face.BACK, line, 0, 0, 16, 16, 15.5f)
                            .colour(brightness, 0, 0).forEach(swap));
                }
                if (north == RedstoneSide.UP) {
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

        private RedstoneSide checkRedstone(World world, int x, int y, int z, boolean blocked) {
            Block block = world.getBlock(x, y, z);
            if (block instanceof RedstoneConnectible
                    && ((RedstoneConnectible) block).isRedstoneConnectible()) {
                return RedstoneSide.SIDE;
            }
            Block other = world.getBlock(x, y - 1, z);
            if (!block.isSolid() && other instanceof RedstoneConnectible
                    && ((RedstoneConnectible) other).isRedstoneConnectible()) {
                return RedstoneSide.SIDE;
            }
            if (!blocked) {
                block = world.getBlock(x, y + 1, z);
                if (block instanceof RedstoneConnectible
                        && ((RedstoneConnectible) block).isRedstoneConnectible()) {
                    return RedstoneSide.UP;
                }
            }
            return RedstoneSide.NONE;
        }

        @Override
        public int getLegacyData() {
            if (getState(NORTH) == RedstoneSide.NONE
                    && getState(SOUTH) == RedstoneSide.NONE
                    && getState(EAST) == RedstoneSide.NONE
                    && getState(WEST) == RedstoneSide.NONE) {
                return getState(POWER);
            }
            return -1;
        }

        @Override
        public boolean isRedstoneConnectible() {
            return true;
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
