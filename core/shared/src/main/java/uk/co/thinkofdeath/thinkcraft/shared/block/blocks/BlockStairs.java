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
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;
import uk.co.thinkofdeath.thinkcraft.shared.world.World;

public class BlockStairs extends BlockFactory {

    public final StateKey<Boolean> TOP = stateAllocator.alloc("top", new BooleanState());
    public final StateKey<Facing> FACING = stateAllocator.alloc("facing", new EnumState<>(Facing.class));
    public final StateKey<Shape> SHAPE = stateAllocator.alloc("shape", new EnumState<>(Shape.class));

    public BlockStairs(IMapViewer iMapViewer) {
        super(iMapViewer);
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    public static enum Facing {
        EAST(2, 1, 0, new int[]{3, 2}, -1, 0),
        WEST(0, -1, 0, new int[]{3, 2}, 1, 0),
        SOUTH(3, 0, 1, new int[]{1, 0}, 0, -1),
        NORTH(1, 0, -1, new int[]{1, 0}, 0, 1);

        public final int rotation;
        public final int directionX;
        public final int directionZ;
        public final int[] acceptedDirections;
        public final int blockX;
        public final int blockZ;

        Facing(int rotation, int directionX, int directionZ, int[] acceptedDirections, int blockX, int blockZ) {
            this.rotation = rotation;
            this.directionX = directionX;
            this.directionZ = directionZ;
            this.acceptedDirections = acceptedDirections;
            this.blockX = blockX;
            this.blockZ = blockZ;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }


    public static enum Shape {
        STRAIGHT,
        INNER_LEFT,
        INNER_RIGHT,
        OUTER_LEFT,
        OUTER_RIGHT;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockStairs.this, state);
        }

        @Override
        public int getLegacyData() {
            if (getState(SHAPE) != Shape.STRAIGHT) {
                return -1;
            }
            int val = getState(FACING).ordinal();
            if (getState(TOP)) {
                val |= 0x4;
            }
            return val;
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                Texture texture = getTexture(Face.TOP);

                // Slab part
                model.addFace(new ModelFace(Face.TOP, texture, 0, 0, 16, 16, 8));
                model.addFace(new ModelFace(Face.BOTTOM, texture, 0, 0, 16, 16, 0, true));
                model.addFace(new ModelFace(Face.LEFT, texture, 0, 0, 16, 8, 16, true));
                model.addFace(new ModelFace(Face.RIGHT, texture, 0, 0, 16, 8, 0, true));
                model.addFace(new ModelFace(Face.FRONT, texture, 0, 0, 16, 8, 16, true));
                model.addFace(new ModelFace(Face.BACK, texture, 0, 0, 16, 8, 0, true));

                Facing facing = getState(FACING);
                Shape shape = getState(SHAPE);

                boolean alt = ((facing == Facing.EAST || facing == Facing.NORTH)
                        && (shape == Shape.OUTER_RIGHT || shape == Shape.INNER_RIGHT))
                        || ((facing == Facing.WEST || facing == Facing.SOUTH)
                        && (shape == Shape.OUTER_LEFT || shape == Shape.INNER_LEFT));

                Model section;
                switch (shape) {
                    case STRAIGHT:
                        section = new Model();
                        section.addFace(new ModelFace(Face.TOP, texture, 0, 0, 8, 16, 16, true));
                        section.addFace(new ModelFace(Face.LEFT, texture, 0, 8, 16, 8, 8));
                        section.addFace(new ModelFace(Face.RIGHT, texture, 0, 8, 16, 8, 0));
                        section.addFace(new ModelFace(Face.FRONT, texture, 0, 8, 8, 8, 16));
                        section.addFace(new ModelFace(Face.BACK, texture, 0, 8, 8, 8, 0));
                        model.join(section.rotateY(facing.rotation * 90));
                        break;
                    case INNER_LEFT:
                    case INNER_RIGHT:
                        section = new Model();
                        section.addFace(new ModelFace(Face.TOP, texture, 0, 8, 16, 8, 16));
                        section.addFace(new ModelFace(Face.LEFT, texture, 8, 8, 8, 8, 16));
                        section.addFace(new ModelFace(Face.RIGHT, texture, 8, 8, 8, 8, 0));
                        section.addFace(new ModelFace(Face.FRONT, texture, 0, 8, 16, 8, 16));
                        section.addFace(new ModelFace(Face.BACK, texture, 0, 8, 16, 8, 8));

                        int oz = alt ? 16 : 0;
                        section.addFace(new ModelFace(Face.TOP, texture, 0, oz, 8, 8, 16));
                        section.addFace(new ModelFace(Face.LEFT, texture, oz, 8, 8, 8, 8));
                        section.addFace(new ModelFace(Face.RIGHT, texture, oz, 8, 8, 8, 0));
                        section.addFace(new ModelFace(Face.FRONT, texture, 0, 8, 8, 8, oz + 8));
                        section.addFace(new ModelFace(Face.BACK, texture, 0, 8, 8, 8, oz));

                        model.join(new Model().join(section, 0, 0,
                                alt ? -8 : 0
                        )
                                .rotateY(facing.rotation * 90));
                        break;
                    case OUTER_LEFT:
                    case OUTER_RIGHT:
                        section = new Model();
                        section.addFace(new ModelFace(Face.TOP, texture, 0, 8, 8, 8, 16));
                        section.addFace(new ModelFace(Face.LEFT, texture, 8, 8, 8, 8, 8));
                        section.addFace(new ModelFace(Face.RIGHT, texture, 8, 8, 8, 8, 0));
                        section.addFace(new ModelFace(Face.FRONT, texture, 0, 8, 8, 8, 16));
                        section.addFace(new ModelFace(Face.BACK, texture, 0, 8, 8, 8, 8));

                        model.join(new Model().join(section, 0, 0,
                                alt ? -8 : 0
                        )
                                .rotateY(facing.rotation * 90));
                        break;
                }

                if (this.<Boolean>getState(TOP)) {
                    model.flipModel();
                }
                model.realignTextures();

            }
            return model;
        }

        @Override
        public Block update(World world, int x, int y, int z) {
            StateMap stateMap = new StateMap(state);

            stateMap.set(SHAPE, Shape.STRAIGHT);
            Facing facing = getState(FACING);
            Block other = world.getBlock(x + facing.directionX, y, z + facing.directionZ);
            boolean set = false;
            if (other instanceof BlockImpl) {
                BlockImpl otherStairs = (BlockImpl) other;
                Facing otherFacing = otherStairs.getState(FACING);
                if (otherStairs.getState(TOP) == getState(TOP) &&
                        (otherFacing.ordinal() == facing.acceptedDirections[0]
                                || otherFacing.ordinal() == facing.acceptedDirections[1])
                        && !isMatching(world.getBlock(x + facing.blockX, y, z + facing.blockZ))) {
                    stateMap.set(SHAPE, otherFacing.ordinal() == facing.acceptedDirections[0] ?
                            Shape.OUTER_LEFT : Shape.OUTER_RIGHT);
                    set = true;
                }
            }
            if (!set) {
                other = world.getBlock(x - facing.directionX, y, z - facing.directionZ);
                if (other instanceof BlockImpl) {
                    BlockImpl otherStairs = (BlockImpl) other;
                    Facing otherFacing = otherStairs.getState(FACING);
                    if (otherStairs.getState(TOP) == getState(TOP) &&
                            (otherFacing.ordinal() == facing.acceptedDirections[0]
                                    || otherFacing.ordinal() == facing.acceptedDirections[1])
                            && !isMatching(world.getBlock(x - facing.blockX, y, z - facing.blockZ))) {
                        stateMap.set(SHAPE, otherFacing.ordinal() == facing.acceptedDirections[0] ?
                                Shape.INNER_LEFT : Shape.INNER_RIGHT);
                    }
                }
            }

            return world.getMapViewer().getBlockRegistry().get(fullName, stateMap);
        }

        private boolean isMatching(Block block) {
            if (block instanceof BlockImpl) {
                BlockImpl stair = (BlockImpl) block;
                return stair.getState(FACING) == getState(FACING)
                        && stair.getState(TOP) == getState(TOP);
            }
            return false;
        }
    }
}
