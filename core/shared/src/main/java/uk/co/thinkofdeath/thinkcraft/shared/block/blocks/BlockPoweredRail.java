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

import uk.co.thinkofdeath.thinkcraft.shared.*;
import uk.co.thinkofdeath.thinkcraft.shared.block.Block;
import uk.co.thinkofdeath.thinkcraft.shared.block.BlockFactory;
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.RailShape;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.BooleanState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelVertex;

public class BlockPoweredRail extends BlockFactory {

    public final StateKey<Boolean> POWERED = stateAllocator.alloc("powered", new BooleanState());
    public final StateKey<RailShape> SHAPE = stateAllocator.alloc("shape", new EnumState<>(RailShape.class,
            new Test<RailShape>() {
                @Override
                public boolean test(RailShape railShape) {
                    return !railShape.isCorner();
                }
            }));

    private final Texture texture;
    private final Texture texturePowered;

    public BlockPoweredRail(IMapViewer iMapViewer, String textureName) {
        super(iMapViewer);

        texture = iMapViewer.getTexture(textureName);
        texturePowered = iMapViewer.getTexture(textureName + "_powered");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockPoweredRail.this, state);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                Texture tex;
                if (getState(POWERED)) {
                    tex = texturePowered;
                } else {
                    tex = texture;
                }

                model = new Model();
                switch (getState(SHAPE)) {
                    case NORTH_SOUTH:
                        model.addFace(new ModelFace(Face.TOP, tex, 0, 0, 16, 16, 0.5f));
                        break;
                    case EAST_WEST:
                        model.addFace(new ModelFace(Face.TOP, tex, 0, 0, 16, 16, 0.5f));
                        model.rotateY(90);
                        break;
                    case ASCENDING_EAST:
                        model.addFace(new ModelFace(Face.TOP, tex, 0, 0, 16, 16, 0.5f)
                                .forEach(new ForEachIterator<ModelVertex>() {
                                    @Override
                                    public void run(ModelVertex v) {
                                        if (v.getZ() == 0) {
                                            v.setY(1 + 1f / 32f);
                                        }
                                    }
                                }));
                        model.rotateY(90);
                        break;
                    case ASCENDING_WEST:
                        model.addFace(new ModelFace(Face.TOP, tex, 0, 0, 16, 16, 0.5f)
                                .forEach(new ForEachIterator<ModelVertex>() {
                                    @Override
                                    public void run(ModelVertex v) {
                                        if (v.getZ() == 0) {
                                            v.setY(1 + 1f / 32f);
                                        }
                                    }
                                }));
                        model.rotateY(270);
                        break;
                    case ASCENDING_NORTH:
                        model.addFace(new ModelFace(Face.TOP, tex, 0, 0, 16, 16, 0.5f)
                                .forEach(new ForEachIterator<ModelVertex>() {
                                    @Override
                                    public void run(ModelVertex v) {
                                        if (v.getZ() == 0) {
                                            v.setY(1 + 1f / 32f);
                                        }
                                    }
                                }));
                        break;
                    case ASCENDING_SOUTH:
                        model.addFace(new ModelFace(Face.TOP, tex, 0, 0, 16, 16, 0.5f)
                                .forEach(new ForEachIterator<ModelVertex>() {
                                    @Override
                                    public void run(ModelVertex v) {
                                        if (v.getZ() == 0) {
                                            v.setY(1 + 1f / 32f);
                                        }
                                    }
                                }));
                        model.rotateY(180);
                        break;
                }
            }
            return model;
        }

        @Override
        public int getLegacyData() {
            int val = this.<RailShape>getState(SHAPE).ordinal();
            if (this.<Boolean>getState(POWERED)) {
                val |= 0x8;
            }
            return val;
        }
    }
}
