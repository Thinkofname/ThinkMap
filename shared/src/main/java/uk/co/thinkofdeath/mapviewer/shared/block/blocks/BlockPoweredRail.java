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
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;
import uk.co.thinkofdeath.mapviewer.shared.model.Model;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelFace;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelVertex;

public class BlockPoweredRail extends BlockFactory {

    public static final String POWERED = "powered";
    public static final String SHAPE = "shape";

    private final Texture texture;
    private final Texture texturePowered;

    public BlockPoweredRail(IMapViewer iMapViewer, String textureName) {
        super(iMapViewer);
        addState(POWERED, new BooleanState());
        addState(SHAPE, new EnumState(Shape.class));

        texture = iMapViewer.getTexture(textureName);
        texturePowered = iMapViewer.getTexture(textureName + "_powered");
    }

    public static enum Shape {
        NORTH_SOUTH,
        EAST_WEST,
        ASCENDING_EAST,
        ASCENDING_WEST,
        ASCENDING_NORTH,
        ASCENDING_SOUTH;

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    /**
     * {@inheritDoc}
     */
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
                if (this.<Boolean>getState(POWERED)) {
                    tex = texturePowered;
                } else {
                    tex = texture;
                }

                model = new Model();
                switch (this.<Shape>getState(SHAPE)) {
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
            int val = this.<Shape>getState(SHAPE).ordinal();
            if (this.<Boolean>getState(POWERED)) {
                val |= 0x8;
            }
            return val;
        }
    }
}
