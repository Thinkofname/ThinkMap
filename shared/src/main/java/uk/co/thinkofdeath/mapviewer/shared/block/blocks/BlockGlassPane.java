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
import uk.co.thinkofdeath.mapviewer.shared.block.states.BooleanState;
import uk.co.thinkofdeath.mapviewer.shared.block.states.StateKey;
import uk.co.thinkofdeath.mapviewer.shared.block.states.StateMap;
import uk.co.thinkofdeath.mapviewer.shared.model.Model;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelFace;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelVertex;
import uk.co.thinkofdeath.mapviewer.shared.world.World;

public class BlockGlassPane extends BlockFactory {

    public final StateKey<Boolean> NORTH = stateAllocator.alloc("north", new BooleanState());
    public final StateKey<Boolean> SOUTH = stateAllocator.alloc("south", new BooleanState());
    public final StateKey<Boolean> EAST = stateAllocator.alloc("east", new BooleanState());
    public final StateKey<Boolean> WEST = stateAllocator.alloc("west", new BooleanState());

    private final Texture texture;
    private final Texture top;

    public BlockGlassPane(IMapViewer iMapViewer) {
        super(iMapViewer);

        this.texture = mapViewer.getTexture("glass");
        top = mapViewer.getTexture("glass_pane_top");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    protected Texture getTexture(BlockImpl block, boolean top) {
        return top ? this.top : texture;
    }

    protected int legacy(BlockImpl block) {
        return 0;
    }

    protected class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockGlassPane.this, state);
        }

        @Override
        public int getLegacyData() {
            if (getState(NORTH)
                    && getState(SOUTH)
                    && getState(EAST)
                    && getState(WEST)) {
                return legacy(this);
            }
            return -1;
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                Texture top = BlockGlassPane.this.getTexture(this, true);
                Texture texture = BlockGlassPane.this.getTexture(this, false);

                boolean north = getState(NORTH);
                boolean south = getState(SOUTH);
                boolean east = getState(EAST);
                boolean west = getState(WEST);

                int z = north ? 0 : 7;
                int z2 = south ? 16 : 9;

                model.addFace(new ModelFace(Face.TOP, top, 7, z, 2, z2 - z, 16, true));
                model.addFace(new ModelFace(Face.BOTTOM, top, 7, z, 2, z2 - z, 0, true));

                if (north || south) {

                    //z = north ? 0 : 8;
                    //z2 = south ? 16 : 8;

                    model.addFace(new ModelFace(Face.LEFT, texture, z, 0, z2 - z, 16, 9));
                    model.addFace(new ModelFace(Face.RIGHT, texture, z, 0, z2 - z, 16, 7));
                }


                if (east || west) {
                    int x = west ? 0 : 7;
                    int x2 = east ? 16 : 9;

                    ForEachIterator<ModelVertex> swap = new ForEachIterator<ModelVertex>() {
                        @Override
                        public void run(ModelVertex v) {
                            float x = v.getTextureX();
                            v.setTextureX(v.getTextureY());
                            v.setTextureY(x);
                        }
                    };

                    model.addFace(new ModelFace(Face.FRONT, texture, x, 0, x2 - x, 16, 9));
                    model.addFace(new ModelFace(Face.BACK, texture, x, 0, x2 - x, 16, 7));

                    if (west) {
                        model.addFace(new ModelFace(Face.TOP, top, 0, 7, 7, 2, 16, true).forEach(swap));
                        model.addFace(new ModelFace(Face.BOTTOM, top, 0, 7, 7, 2, 0, true).forEach(swap));
                    }
                    if (east) {
                        model.addFace(new ModelFace(Face.TOP, top, 9, 7, 7, 2, 16, true).forEach(swap));
                        model.addFace(new ModelFace(Face.BOTTOM, top, 9, 7, 7, 2, 0, true).forEach(swap));
                    }
                }

                if (north && !south && !east && !west) {
                    model.addFace(new ModelFace(Face.NORTH, texture, 7, 0, 2, 16, 9));
                    model.addFace(new ModelFace(Face.SOUTH, texture, 7, 0, 2, 16, 9));
                }

                if (!north && south && !east && !west) {
                    model.addFace(new ModelFace(Face.NORTH, texture, 7, 0, 2, 16, 7));
                    model.addFace(new ModelFace(Face.SOUTH, texture, 7, 0, 2, 16, 7));
                }

                if (!north && !south && !east && west) {
                    model.addFace(new ModelFace(Face.WEST, texture, 7, 0, 2, 16, 9));
                    model.addFace(new ModelFace(Face.EAST, texture, 7, 0, 2, 16, 9));
                }

                if (!north && !south && east && !west) {
                    model.addFace(new ModelFace(Face.WEST, texture, 7, 0, 2, 16, 7));
                    model.addFace(new ModelFace(Face.EAST, texture, 7, 0, 2, 16, 7));
                }
            }
            return model;
        }

        @Override
        public Block update(World world, int x, int y, int z) {
            StateMap stateMap = new StateMap(state);
            stateMap.set(NORTH, checkAttach(world, x, y, z, Face.NORTH));
            stateMap.set(SOUTH, checkAttach(world, x, y, z, Face.SOUTH));
            stateMap.set(EAST, checkAttach(world, x, y, z, Face.EAST));
            stateMap.set(WEST, checkAttach(world, x, y, z, Face.WEST));

            if (!stateMap.get(NORTH)
                    && !stateMap.get(SOUTH)
                    && !stateMap.get(EAST)
                    && !stateMap.get(WEST)) {
                stateMap.set(NORTH, true);
                stateMap.set(SOUTH, true);
                stateMap.set(EAST, true);
                stateMap.set(WEST, true);
            }
            return mapViewer.getBlockRegistry().get(fullName, stateMap);
        }

    }

    private static boolean checkAttach(World world, int x, int y, int z, Face face) {
        Block block = world.getBlock(x + face.getOffsetX(), y + face.getOffsetY(), z + face.getOffsetZ());
        return block.isSolid() || block instanceof BlockImpl;
    }
}
