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
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.StairFacing;
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.StairShape;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.BooleanState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.collision.AABB;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;
import uk.co.thinkofdeath.thinkcraft.shared.world.World;

public class BlockStairs extends BlockFactory {

    public final StateKey<Boolean> TOP = stateAllocator.alloc("top", new BooleanState());
    public final StateKey<StairFacing> FACING = stateAllocator.alloc("facing", new EnumState<>(StairFacing.class));
    public final StateKey<StairShape> SHAPE = stateAllocator.alloc("shape", new EnumState<>(StairShape.class));

    public BlockStairs(IMapViewer iMapViewer) {
        super(iMapViewer);
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }


    private class BlockImpl extends Block {

        private AABB slabHitbox;
        private AABB topHitbox;

        BlockImpl(StateMap state) {
            super(BlockStairs.this, state);
        }

        @Override
        public int getLegacyData() {
            if (getState(SHAPE) != StairShape.STRAIGHT) {
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

                slabHitbox = computeHitboxFromModel(model);

                StairFacing facing = getState(FACING);
                StairShape shape = getState(SHAPE);

                boolean alt = ((facing == StairFacing.EAST || facing == StairFacing.NORTH)
                        && (shape == StairShape.OUTER_RIGHT || shape == StairShape.INNER_RIGHT))
                        || ((facing == StairFacing.WEST || facing == StairFacing.SOUTH)
                        && (shape == StairShape.OUTER_LEFT || shape == StairShape.INNER_LEFT));

                Model section;
                switch (shape) {
                    case STRAIGHT:
                        section = new Model();
                        section.addFace(new ModelFace(Face.TOP, texture, 0, 0, 8, 16, 16, true));
                        section.addFace(new ModelFace(Face.LEFT, texture, 0, 8, 16, 8, 8));
                        section.addFace(new ModelFace(Face.RIGHT, texture, 0, 8, 16, 8, 0));
                        section.addFace(new ModelFace(Face.FRONT, texture, 0, 8, 8, 8, 16));
                        section.addFace(new ModelFace(Face.BACK, texture, 0, 8, 8, 8, 0));
                        section.rotateY(facing.rotation * 90);
                        topHitbox = computeHitboxFromModel(section);
                        model.join(section);
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

                        section = new Model().join(section, 0, 0,
                                alt ? -8 : 0
                        ).rotateY(facing.rotation * 90);
                        topHitbox = computeHitboxFromModel(section);
                        model.join(section);
                        break;
                    case OUTER_LEFT:
                    case OUTER_RIGHT:
                        section = new Model();
                        section.addFace(new ModelFace(Face.TOP, texture, 0, 8, 8, 8, 16));
                        section.addFace(new ModelFace(Face.LEFT, texture, 8, 8, 8, 8, 8));
                        section.addFace(new ModelFace(Face.RIGHT, texture, 8, 8, 8, 8, 0));
                        section.addFace(new ModelFace(Face.FRONT, texture, 0, 8, 8, 8, 16));
                        section.addFace(new ModelFace(Face.BACK, texture, 0, 8, 8, 8, 8));

                        section = new Model().join(section, 0, 0,
                                alt ? -8 : 0
                        ).rotateY(facing.rotation * 90);
                        topHitbox = computeHitboxFromModel(section);
                        model.join(section);
                        break;
                }

                if (this.<Boolean>getState(TOP)) {
                    model.flipModel();
                    slabHitbox.setY1(slabHitbox.getY1() + 0.5);
                    slabHitbox.setY2(slabHitbox.getY2() + 0.5);
                    topHitbox.setY1(topHitbox.getY1() - 0.5);
                    topHitbox.setY2(topHitbox.getY2() - 0.5);
                }
                model.realignTextures();

            }
            return model;
        }

        @Override
        public Block update(World world, int x, int y, int z) {
            StateMap stateMap = new StateMap(state);

            stateMap.set(SHAPE, StairShape.STRAIGHT);
            StairFacing facing = getState(FACING);
            Block other = world.getBlock(x + facing.directionX, y, z + facing.directionZ);
            boolean set = false;
            if (other instanceof BlockImpl) {
                BlockImpl otherStairs = (BlockImpl) other;
                StairFacing otherFacing = otherStairs.getState(FACING);
                if (otherStairs.getState(TOP) == getState(TOP) &&
                        (otherFacing.ordinal() == facing.acceptedDirections[0]
                                || otherFacing.ordinal() == facing.acceptedDirections[1])
                        && !isMatching(world.getBlock(x + facing.blockX, y, z + facing.blockZ))) {
                    stateMap.set(SHAPE, otherFacing.ordinal() == facing.acceptedDirections[0] ?
                            StairShape.OUTER_LEFT : StairShape.OUTER_RIGHT);
                    set = true;
                }
            }
            if (!set) {
                other = world.getBlock(x - facing.directionX, y, z - facing.directionZ);
                if (other instanceof BlockImpl) {
                    BlockImpl otherStairs = (BlockImpl) other;
                    StairFacing otherFacing = otherStairs.getState(FACING);
                    if (otherStairs.getState(TOP) == getState(TOP) &&
                            (otherFacing.ordinal() == facing.acceptedDirections[0]
                                    || otherFacing.ordinal() == facing.acceptedDirections[1])
                            && !isMatching(world.getBlock(x - facing.blockX, y, z - facing.blockZ))) {
                        stateMap.set(SHAPE, otherFacing.ordinal() == facing.acceptedDirections[0] ?
                                StairShape.INNER_LEFT : StairShape.INNER_RIGHT);
                    }
                }
            }

            return world.getMapViewer().getBlockRegistry().get(fullName, stateMap);
        }

        @Override
        public AABB[] getHitbox() {
            if (hitbox == null) {
                getModel();
                hitbox = new AABB[]{slabHitbox, topHitbox};
            }
            return hitbox;
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
