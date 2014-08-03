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
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.Facing;
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.PistonType;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.collision.AABB;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelVertex;

public class BlockPistonHead extends BlockFactory {

    public final StateKey<PistonType> TYPE = stateAllocator.alloc("type", new EnumState<>(PistonType.class));
    public final StateKey<Facing> FACING = stateAllocator.alloc("facing", new EnumState<>(Facing.class));

    private final Texture pistonTopNormal;
    private final Texture pistonTopSticky;
    private final Texture pistonSide;

    public BlockPistonHead(IMapViewer iMapViewer) {
        super(iMapViewer);

        pistonTopNormal = iMapViewer.getBlockTexture("minecraft:piston_top_normal");
        pistonTopSticky = iMapViewer.getBlockTexture("minecraft:piston_top_sticky");
        pistonSide = iMapViewer.getBlockTexture("minecraft:piston_side");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockPistonHead.this, state);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                Facing facing = getState(FACING);
                model = new Model();

                model.addFace(new ModelFace(Face.LEFT, pistonSide, -4, 6, 16, 4, 10)
                        .forEach(new ForEachIterator<ModelVertex>() {
                            @Override
                            public void run(ModelVertex v) {
                                if (v.getTextureX() < 0) {
                                    v.setTextureX(0);
                                } else if (v.getTextureX() > 0) {
                                    v.setTextureX(1);
                                }
                                if (v.getTextureY() < 0.5f) {
                                    v.setTextureY(0);
                                } else if (v.getTextureY() > 0.5f) {
                                    v.setTextureY(4f / 16f);
                                }
                            }
                        }));
                model.addFace(new ModelFace(Face.RIGHT, pistonSide, -4, 6, 16, 4, 6)
                        .forEach(new ForEachIterator<ModelVertex>() {
                            @Override
                            public void run(ModelVertex v) {
                                if (v.getTextureX() < 0) {
                                    v.setTextureX(0);
                                } else if (v.getTextureX() > 0) {
                                    v.setTextureX(1);
                                }
                                if (v.getTextureY() < 0.5f) {
                                    v.setTextureY(0);
                                } else if (v.getTextureY() > 0.5f) {
                                    v.setTextureY(4f / 16f);
                                }
                            }
                        }));
                model.addFace(new ModelFace(Face.TOP, pistonSide, 6, -4, 4, 16, 10)
                        .forEach(new ForEachIterator<ModelVertex>() {
                            @Override
                            public void run(ModelVertex v) {
                                if (v.getTextureX() < 0.5) {
                                    v.setTextureX(0);
                                } else if (v.getTextureX() > 0.5) {
                                    v.setTextureX(4f / 16f);
                                }
                                if (v.getTextureY() < 0) {
                                    v.setTextureY(0);
                                } else if (v.getTextureY() > 0) {
                                    v.setTextureY(1);
                                }
                                float x = v.getTextureX();
                                v.setTextureX(v.getTextureY());
                                v.setTextureY(x);
                            }
                        }));
                model.addFace(new ModelFace(Face.BOTTOM, pistonSide, 6, -4, 4, 16, 6)
                        .forEach(new ForEachIterator<ModelVertex>() {
                            @Override
                            public void run(ModelVertex v) {
                                if (v.getTextureX() < 0.5) {
                                    v.setTextureX(0);
                                } else if (v.getTextureX() > 0.5) {
                                    v.setTextureX(4f / 16f);
                                }
                                if (v.getTextureY() < 0) {
                                    v.setTextureY(0);
                                } else if (v.getTextureY() > 0) {
                                    v.setTextureY(1);
                                }
                                float x = v.getTextureX();
                                v.setTextureX(v.getTextureY());
                                v.setTextureY(x);
                            }
                        }));

                Model head = new Model().join(BlockPiston.createHeadPart(
                        (getState(TYPE) == PistonType.DEFAULT ? pistonTopNormal :
                                pistonTopSticky), pistonTopNormal, pistonSide
                ), 0, 0, 12);

                if (facing.getClockwiseRotation() != -1) {
                    model.rotateY(facing.getClockwiseRotation() * 90);
                    head.rotateY(facing.getClockwiseRotation() * 90);
                } else {
                    if (facing == Facing.DOWN) {
                        model.rotateX(270);
                        head.rotateX(270);
                    } else {
                        model.rotateX(270 + 180);
                        head.rotateX(270 + 180);
                    }
                }

                hitbox = new AABB[]{
                        computeHitboxFromModel(model),
                        computeHitboxFromModel(head)
                };
                model = model.join(head);
            }
            return model;
        }

        @Override
        public AABB[] getHitbox() {
            if (hitbox == null) {
                getModel();
            }
            return hitbox;
        }

        @Override
        public int getLegacyData() {
            int val = getState(FACING).getDUNSWEOrder();
            if (getState(TYPE) == PistonType.STICKY) {
                val |= 0x8;
            }
            return val;
        }

    }
}
