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

public class BlockPiston extends BlockFactory {

    public static final String EXTENDED = "extended";
    public static final String FACING = "facing";

    private final Texture pistonBottom;
    private final Texture pistonInner;
    private final Texture pistonSide;
    private final Texture pistonTopNormal;
    private final Texture pistonTop;

    public BlockPiston(IMapViewer iMapViewer, String type) {
        super(iMapViewer);
        addState(EXTENDED, new BooleanState());
        addState(FACING, new EnumState(Facing.class));

        pistonBottom = iMapViewer.getTexture("piston_bottom");
        pistonInner = iMapViewer.getTexture("piston_inner");
        pistonSide = iMapViewer.getTexture("piston_side");
        pistonTopNormal = iMapViewer.getTexture("piston_top_normal");
        pistonTop = iMapViewer.getTexture("piston_top_" + type);
    }

    public static enum Facing {
        DOWN(-1),
        UP(-1),
        NORTH(2),
        SOUTH(0),
        WEST(1),
        EAST(3);

        public final int rotation;

        Facing(int rotation) {
            this.rotation = rotation;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public static Model createHeadPart(Texture pistonTop, Texture pistonTopNormal, Texture pistonSide) {
        Model model = new Model();
        model.addFace(new ModelFace(Face.BACK, pistonTopNormal, 0, 0, 16, 16, 0));
        model.addFace(new ModelFace(Face.FRONT, pistonTop, 0, 0, 16, 16, 4, true));
        model.addFace(new ModelFace(Face.LEFT, pistonSide, 0, 0, 4, 16, 16, true)
                .forEach(new ForEachIterator<ModelVertex>() {
                    @Override
                    public void run(ModelVertex v) {
                        float x = v.getTextureX();
                        v.setTextureX(v.getTextureY());
                        v.setTextureY(x);
                    }
                }));
        model.addFace(new ModelFace(Face.RIGHT, pistonSide, 0, 0, 4, 16, 0, true)
                .forEach(new ForEachIterator<ModelVertex>() {
                    @Override
                    public void run(ModelVertex v) {
                        float x = v.getTextureX();
                        v.setTextureX(v.getTextureY());
                        v.setTextureY(x);
                    }
                }));
        model.addFace(new ModelFace(Face.TOP, pistonSide, 0, 0, 16, 4, 16, true)
                .forEach(new ForEachIterator<ModelVertex>() {
                    @Override
                    public void run(ModelVertex v) {
                        v.setTextureY((1 - v.getTextureY()) - 12f / 16f);
                    }
                }));
        model.addFace(new ModelFace(Face.BOTTOM, pistonSide, 0, 0, 16, 4, 0, true)
                .forEach(new ForEachIterator<ModelVertex>() {
                    @Override
                    public void run(ModelVertex v) {
                        v.setTextureY((1 - v.getTextureY()) - 12f / 16f);
                    }
                }));
        return model;
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
            super(BlockPiston.this, state);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                Facing facing = getState(FACING);
                model = new Model();

                // Base of the model
                model.addFace(new ModelFace(Face.BACK, pistonBottom, 0, 0, 16, 16, 0, true));
                model.addFace(new ModelFace(Face.FRONT, pistonInner, 0, 0, 16, 16, 12));
                model.addFace(new ModelFace(Face.LEFT, pistonSide, 0, 0, 12, 16, 16, true)
                        .forEach(new ForEachIterator<ModelVertex>() {
                            @Override
                            public void run(ModelVertex v) {
                                float x = v.getTextureX();
                                v.setTextureX(v.getTextureY());
                                v.setTextureY(x + 4f / 16f);
                            }
                        }));
                model.addFace(new ModelFace(Face.RIGHT, pistonSide, 0, 0, 12, 16, 0, true)
                        .forEach(new ForEachIterator<ModelVertex>() {
                            @Override
                            public void run(ModelVertex v) {
                                float x = v.getTextureX();
                                v.setTextureX(v.getTextureY());
                                v.setTextureY(x + 4f / 16f);
                            }
                        }));
                model.addFace(new ModelFace(Face.TOP, pistonSide, 0, 0, 16, 12, 16, true)
                        .forEach(new ForEachIterator<ModelVertex>() {
                            @Override
                            public void run(ModelVertex v) {
                                v.setTextureY(1 - v.getTextureY());
                            }
                        }));
                model.addFace(new ModelFace(Face.BOTTOM, pistonSide, 0, 0, 16, 12, 0, true)
                        .forEach(new ForEachIterator<ModelVertex>() {
                            @Override
                            public void run(ModelVertex v) {
                                v.setTextureY(1 - v.getTextureY());
                            }
                        }));

                if (!this.<Boolean>getState(EXTENDED)) {
                    model.join(createHeadPart(pistonTop, pistonTopNormal, pistonSide), 0, 0, 12);
                }

                if (facing.rotation != -1) {
                    model.rotateY(facing.rotation * 90);
                } else {
                    if (facing == Facing.DOWN) {
                        model.rotateX(270);
                    } else {
                        model.rotateX(270 + 180);
                    }
                }
            }
            return model;
        }

        @Override
        public int getLegacyData() {
            int val = this.<Facing>getState(FACING).ordinal();
            if (this.<Boolean>getState(EXTENDED)) {
                val |= 0x8;
            }
            return val;
        }

    }
}
