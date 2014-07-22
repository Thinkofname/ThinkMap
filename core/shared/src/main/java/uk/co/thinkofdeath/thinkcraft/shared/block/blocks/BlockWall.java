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
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.WallType;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.BooleanState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.collision.AABB;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelVertex;
import uk.co.thinkofdeath.thinkcraft.shared.world.World;

public class BlockWall extends BlockFactory {

    public final StateKey<WallType> TYPE = stateAllocator.alloc("type", new EnumState<>(WallType.class));
    public final StateKey<Boolean> NORTH = stateAllocator.alloc("north", new BooleanState());
    public final StateKey<Boolean> SOUTH = stateAllocator.alloc("south", new BooleanState());
    public final StateKey<Boolean> EAST = stateAllocator.alloc("east", new BooleanState());
    public final StateKey<Boolean> WEST = stateAllocator.alloc("west", new BooleanState());

    private final Texture[] textures = new Texture[WallType.values().length];

    public BlockWall(IMapViewer iMapViewer) {
        super(iMapViewer);
        for (WallType wallType : WallType.values()) {
            textures[wallType.ordinal()] = mapViewer.getTexture(wallType.name().toLowerCase());
        }
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
            super(BlockWall.this, state);
        }

        @Override
        public int getLegacyData() {
            boolean north = getState(NORTH);
            boolean south = getState(SOUTH);
            boolean east = getState(EAST);
            boolean west = getState(WEST);
            return north && south && east && west ? getState(TYPE).ordinal() : -1;
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                Texture texture = textures[getState(TYPE).ordinal()];

                boolean north = getState(NORTH);
                boolean south = getState(SOUTH);
                boolean east = getState(EAST);
                boolean west = getState(WEST);

                int z = north ? 0 : 5;
                int z2 = south ? 16 : 11;

                model.addFace(new ModelFace(Face.TOP, texture, 5, z, 6, z2 - z, 13));
                model.addFace(new ModelFace(Face.BOTTOM, texture, 5, z, 6, z2 - z, 0, true));

                if (north || south) {

                    model.addFace(new ModelFace(Face.LEFT, texture, z, 0, z2 - z, 13, 11));
                    model.addFace(new ModelFace(Face.RIGHT, texture, z, 0, z2 - z, 13, 5));
                }


                if (east || west) {
                    int x = west ? 0 : 5;
                    int x2 = east ? 16 : 11;

                    ForEachIterator<ModelVertex> swap = new ForEachIterator<ModelVertex>() {
                        @Override
                        public void run(ModelVertex v) {
                            float x = v.getTextureX();
                            v.setTextureX(v.getTextureY());
                            v.setTextureY(x);
                        }
                    };

                    model.addFace(new ModelFace(Face.FRONT, texture, x, 0, x2 - x, 13, 11));
                    model.addFace(new ModelFace(Face.BACK, texture, x, 0, x2 - x, 13, 5));

                    if (west) {
                        model.addFace(new ModelFace(Face.TOP, texture, 0, 5, 7, 6, 13).forEach(swap));
                        model.addFace(new ModelFace(Face.BOTTOM, texture, 0, 5, 7, 6, 0, true).forEach(swap));
                    }
                    if (east) {
                        model.addFace(new ModelFace(Face.TOP, texture, 9, 5, 7, 6, 13).forEach(swap));
                        model.addFace(new ModelFace(Face.BOTTOM, texture, 9, 5, 7, 6, 0, true).forEach(swap));
                    }
                }

                if (((!east || !west || north || south))
                        && ((!north || !south || east || west))) {
                    // Center post
                    model.addFace(new ModelFace(Face.TOP, texture, 4, 4, 8, 8, 16, true));
                    model.addFace(new ModelFace(Face.BOTTOM, texture, 4, 4, 8, 8, 0, true));

                    model.addFace(new ModelFace(Face.LEFT, texture, 4, 0, 8, 16, 12));
                    model.addFace(new ModelFace(Face.RIGHT, texture, 4, 0, 8, 16, 4));
                    model.addFace(new ModelFace(Face.FRONT, texture, 4, 0, 8, 16, 12));
                    model.addFace(new ModelFace(Face.BACK, texture, 4, 0, 8, 16, 4));
                }

                model.realignTextures();
            }
            return model;
        }

        @Override
        public Block update(World world, int x, int y, int z) {
            StateMap map = new StateMap();

            map.set(NORTH, check(world, x, y, z - 1));
            map.set(SOUTH, check(world, x, y, z + 1));
            map.set(EAST, check(world, x + 1, y, z));
            map.set(WEST, check(world, x - 1, y, z));

            return mapViewer.getBlockRegistry().get(fullName, map);
        }

        @Override
        public AABB[] getHitbox() {
            if (hitbox == null) {
                AABB horz = new AABB(4d / 16d, 0, 4d / 16, 12d / 16d, 1.5, 12d / 16d);
                AABB vert = new AABB(4d / 16d, 0, 4d / 16, 12d / 16d, 1.5, 12d / 16d);

                if (getState(EAST)) {
                    horz.setX2(1);
                }
                if (getState(WEST)) {
                    horz.setX1(0);
                }

                if (getState(SOUTH)) {
                    vert.setZ2(1);
                }
                if (getState(NORTH)) {
                    vert.setZ1(0);
                }
                hitbox = new AABB[]{
                        horz,
                        vert
                };
            }
            return hitbox;
        }

        public boolean check(World world, int x, int y, int z) {
            Block block = world.getBlock(x, y, z);
            return block instanceof BlockImpl || block.isSolid();
        }
    }
}
