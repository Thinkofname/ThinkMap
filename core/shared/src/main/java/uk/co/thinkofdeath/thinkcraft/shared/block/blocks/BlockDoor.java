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
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelVertex;
import uk.co.thinkofdeath.thinkcraft.shared.world.World;

public class BlockDoor extends BlockFactory {

    public final StateKey<Half> HALF = stateAllocator.alloc("half", new EnumState<>(Half.class));
    public final StateKey<Boolean> OPEN = stateAllocator.alloc("open", new BooleanState());
    public final StateKey<Facing> FACING = stateAllocator.alloc("facing", new EnumState<>(Facing.class));
    public final StateKey<Hinge> HINGE = stateAllocator.alloc("hinge", new EnumState<>(Hinge.class));

    private final Texture upper;
    private final Texture lower;

    public BlockDoor(IMapViewer iMapViewer, String texture) {
        super(iMapViewer);
        upper = mapViewer.getTexture(texture + "_upper");
        lower = mapViewer.getTexture(texture + "_lower");
    }

    public static enum Hinge {
        RIGHT,
        LEFT;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public static enum Half {
        UPPER,
        LOWER;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public static enum Facing {
        WEST,
        NORTH,
        EAST,
        SOUTH;

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
            super(BlockDoor.this, state);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                Texture texture = getState(HALF) == Half.UPPER ? upper : lower;

                model.addFace(new ModelFace(Face.FRONT, texture, 0, 0, 16, 16, 3, true)
                        .forEach(new ForEachIterator<ModelVertex>() {
                            @Override
                            public void run(ModelVertex v) {
                                v.setTextureX(1 - v.getTextureX());
                            }
                        }));
                model.addFace(new ModelFace(Face.BACK, texture, 0, 0, 16, 16, 0, false));
                model.addFace(new ModelFace(Face.TOP, texture, 0, 0, 16, 3, 16, true));
                model.addFace(new ModelFace(Face.BOTTOM, texture, 0, 0, 16, 3, 0, true));
                model.addFace(new ModelFace(Face.LEFT, texture, 0, 0, 3, 16, 16, true));
                model.addFace(new ModelFace(Face.RIGHT, texture, 0, 0, 3, 16, 0, true));

                Hinge hinge = getState(HINGE);
                Facing facing = getState(FACING);
                boolean open = getState(OPEN);

                if (hinge == Hinge.LEFT) {
                    for (ModelFace face : model.getFaces()) {
                        face.forEach(new ForEachIterator<ModelVertex>() {
                            @Override
                            public void run(ModelVertex v) {
                                v.setTextureX(1 - v.getTextureX());
                            }
                        });
                    }
                }
                if (open) {
                    for (ModelFace face : model.getFaces()) {
                        face.forEach(new ForEachIterator<ModelVertex>() {
                            @Override
                            public void run(ModelVertex v) {
                                v.setTextureX(1 - v.getTextureX());
                            }
                        });
                    }
                }

                model.rotateY(
                        facing.ordinal() * 90 + 270 + (open ? (hinge == Hinge.LEFT ? -90 : 90) : 0)
                );
            }
            return model;
        }

        @Override
        public Block update(World world, int x, int y, int z) {
            StateMap stateMap = new StateMap(state);
            if (getState(HALF) == Half.UPPER) {
                Block block = world.getBlock(x, y - 1, z);
                if (block instanceof BlockImpl) {
                    BlockImpl other = (BlockImpl) block;
                    stateMap.set(OPEN, other.getState(OPEN));
                    stateMap.set(FACING, other.getState(FACING));
                }
            } else {
                Block block = world.getBlock(x, y + 1, z);
                if (block instanceof BlockImpl) {
                    BlockImpl other = (BlockImpl) block;
                    stateMap.set(HINGE, other.getState(HINGE));
                }
            }
            return world.getMapViewer().getBlockRegistry().get(fullName, stateMap);
        }

        @Override
        public int getLegacyData() {
            int val = getState(HALF) == Half.UPPER ? 0x8 : 0x0;
            if (getState(HALF) == Half.UPPER) {
                if (!getState(OPEN)
                        || getState(FACING) != Facing.WEST) {
                    return -1;
                }
                val |= getState(HINGE) == Hinge.LEFT ? 0x1 : 0x0;
            } else {
                if (getState(HINGE) == Hinge.RIGHT) {
                    return -1;
                }
                val |= getState(OPEN) ? 0x4 : 0x0;
                val |= getState(FACING).ordinal();
            }
            return val;
        }
    }
}
