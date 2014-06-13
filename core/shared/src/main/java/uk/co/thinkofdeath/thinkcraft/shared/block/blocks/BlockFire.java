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
import uk.co.thinkofdeath.thinkcraft.shared.block.states.BooleanState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.IntegerState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelVertex;
import uk.co.thinkofdeath.thinkcraft.shared.world.World;

public class BlockFire extends BlockFactory {

    public final StateKey<Integer> AGE = stateAllocator.alloc("age", new IntegerState(0, 15));
    public final StateKey<Boolean> UP = stateAllocator.alloc("up", new BooleanState());
    public final StateKey<Boolean> DOWN = stateAllocator.alloc("down", new BooleanState());
    public final StateKey<Boolean> NORTH = stateAllocator.alloc("north", new BooleanState());
    public final StateKey<Boolean> SOUTH = stateAllocator.alloc("south", new BooleanState());
    public final StateKey<Boolean> EAST = stateAllocator.alloc("east", new BooleanState());
    public final StateKey<Boolean> WEST = stateAllocator.alloc("west", new BooleanState());

    private final Texture layer0;
    private final Texture layer1;

    public BlockFire(IMapViewer iMapViewer) {
        super(iMapViewer);

        layer0 = mapViewer.getTexture("fire_layer_0");
        layer1 = mapViewer.getTexture("fire_layer_1");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }


    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockFire.this, state);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                if (getState(DOWN)) {
                    model.addFace(new ModelFace(Face.RIGHT, layer0, 0, 0, 16, 16, 0)
                            .forEach(new FireMod(true, 10)));
                    model.addFace(new ModelFace(Face.LEFT, layer0, 0, 0, 16, 16, 16)
                            .forEach(new FireMod(true, -1)));

                    model.addFace(new ModelFace(Face.LEFT, layer0, 0, 0, 16, 16, 16)
                            .forEach(new FireMod(true, -10)));
                    model.addFace(new ModelFace(Face.RIGHT, layer0, 0, 0, 16, 16, 0)
                            .forEach(new FireMod(true, 1)));

                    model.addFace(new ModelFace(Face.BACK, layer0, 0, 0, 16, 16, 0)
                            .forEach(new FireMod(false, 10)));
                    model.addFace(new ModelFace(Face.FRONT, layer0, 0, 0, 16, 16, 16)
                            .forEach(new FireMod(false, -1)));

                    model.addFace(new ModelFace(Face.FRONT, layer0, 0, 0, 16, 16, 16)
                            .forEach(new FireMod(false, -10)));
                    model.addFace(new ModelFace(Face.BACK, layer0, 0, 0, 16, 16, 0)
                            .forEach(new FireMod(false, 1)));
                } else {
                    if (getState(UP)) {
                        model.addFace(new ModelFace(Face.BOTTOM, layer1, 0, 0, 16, 16, 16)
                                .forEach(new ForEachIterator<ModelVertex>() {
                                    @Override
                                    public void run(ModelVertex v) {
                                        if (v.getZ() == 0) {
                                            v.setY(v.getY() - 2f / 16f);
                                        }
                                    }
                                }));
                        model.addFace(new ModelFace(Face.BOTTOM, layer1, 0, 0, 16, 16, 16)
                                .forEach(new ForEachIterator<ModelVertex>() {
                                    @Override
                                    public void run(ModelVertex v) {
                                        if (v.getZ() == 1) {
                                            v.setY(v.getY() - 2f / 16f);
                                        }
                                        v.setTextureY(1 - v.getTextureY());
                                    }
                                }));
                    }
                    if (getState(NORTH)) {
                        model.addFace(new ModelFace(Face.FRONT, layer0, 0, 0, 16, 16, 0)
                                .forEach(new FireMod(false, 3)));
                    }
                    if (getState(SOUTH)) {
                        model.addFace(new ModelFace(Face.BACK, layer0, 0, 0, 16, 16, 16)
                                .forEach(new FireMod(false, -3)));
                    }
                    if (getState(WEST)) {
                        model.addFace(new ModelFace(Face.LEFT, layer0, 0, 0, 16, 16, 0)
                                .forEach(new FireMod(true, 3)));
                    }
                    if (getState(EAST)) {
                        model.addFace(new ModelFace(Face.RIGHT, layer0, 0, 0, 16, 16, 16)
                                .forEach(new FireMod(true, -3)));
                    }
                }
            }
            return model;
        }

        @Override
        public Block update(World world, int x, int y, int z) {
            StateMap map = new StateMap();
            map.set(AGE, getState(AGE));

            map.set(UP, world.getBlock(x, y + 1, z).isSolid());
            map.set(DOWN, world.getBlock(x, y - 1, z).isSolid());
            map.set(NORTH, world.getBlock(x, y, z - 1).isSolid());
            map.set(SOUTH, world.getBlock(x, y, z + 1).isSolid());
            map.set(EAST, world.getBlock(x + 1, y, z).isSolid());
            map.set(WEST, world.getBlock(x - 1, y, z).isSolid());
            return mapViewer.getBlockRegistry().get(fullName, map);
        }

        @Override
        public int getLegacyData() {
            if (getState(DOWN)
                    || getState(UP)
                    || getState(NORTH)
                    || getState(SOUTH)
                    || getState(EAST)
                    || getState(WEST)) {
                return -1;
            }
            return getState(AGE);
        }
    }

    private static class FireMod implements ForEachIterator<ModelVertex> {

        private final boolean changeX;
        private final int amount;

        public FireMod(boolean changeX, int amount) {
            this.changeX = changeX;
            this.amount = amount;
        }

        @Override
        public void run(ModelVertex v) {
            if (v.getY() == 1) {
                v.setY(v.getY() + 6f / 16f);
                if (changeX) {
                    v.setX(v.getX() + (amount / 16f));
                } else {
                    v.setZ(v.getZ() + (amount / 16f));
                }
            }
        }
    }
}
