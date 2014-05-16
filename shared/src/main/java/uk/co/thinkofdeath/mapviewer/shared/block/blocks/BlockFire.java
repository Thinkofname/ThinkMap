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
import uk.co.thinkofdeath.mapviewer.shared.ForEachIterator;
import uk.co.thinkofdeath.mapviewer.shared.IMapViewer;
import uk.co.thinkofdeath.mapviewer.shared.Texture;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockFactory;
import uk.co.thinkofdeath.mapviewer.shared.block.StateMap;
import uk.co.thinkofdeath.mapviewer.shared.block.states.BooleanState;
import uk.co.thinkofdeath.mapviewer.shared.block.states.IntegerState;
import uk.co.thinkofdeath.mapviewer.shared.model.Model;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelFace;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelVertex;
import uk.co.thinkofdeath.mapviewer.shared.world.World;

public class BlockFire extends BlockFactory {

    public static final String AGE = "age";
    public static final String UP = "up";
    public static final String DOWN = "down";
    public static final String NORTH = "north";
    public static final String SOUTH = "south";
    public static final String EAST = "east";
    public static final String WEST = "west";

    private final Texture layer0;
    private final Texture layer1;

    public BlockFire(IMapViewer iMapViewer) {
        super(iMapViewer);
        addState(AGE, new IntegerState(0, 15));
        addState(UP, new BooleanState());
        addState(DOWN, new BooleanState());
        addState(NORTH, new BooleanState());
        addState(SOUTH, new BooleanState());
        addState(EAST, new BooleanState());
        addState(WEST, new BooleanState());

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
            return mapViewer.getBlockRegistry().get(plugin + ":" + name, map);
        }

        @Override
        public int getLegacyData() {
            if (this.<Boolean>getState(DOWN)
                    || this.<Boolean>getState(UP)
                    || this.<Boolean>getState(NORTH)
                    || this.<Boolean>getState(SOUTH)
                    || this.<Boolean>getState(EAST)
                    || this.<Boolean>getState(WEST)) {
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
