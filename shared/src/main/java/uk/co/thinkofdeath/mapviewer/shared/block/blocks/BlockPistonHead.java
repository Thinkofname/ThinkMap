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
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;
import uk.co.thinkofdeath.mapviewer.shared.model.Model;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelFace;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelVertex;

public class BlockPistonHead extends BlockFactory {

    public static final String TYPE = "type";
    public static final String FACING = "facing";

    private final Texture pistonTopNormal;
    private final Texture pistonTopSticky;
    private final Texture pistonSide;

    public BlockPistonHead(IMapViewer iMapViewer) {
        super(iMapViewer);
        addState(FACING, new EnumState(Facing.class));
        addState(TYPE, new EnumState(Type.class));

        pistonTopNormal = iMapViewer.getTexture("piston_top_normal");
        pistonTopSticky = iMapViewer.getTexture("piston_top_sticky");
        pistonSide = iMapViewer.getTexture("piston_side");
    }

    public static enum Type {
        DEFAULT,
        STICKY;

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
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

    /**
     * {@inheritDoc}
     */
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


                model.join(BlockPiston.createHeadPart(
                        (this.<Type>getState(TYPE) == Type.DEFAULT ? pistonTopNormal :
                                pistonTopSticky), pistonTopNormal, pistonSide
                ), 0, 0, 12);

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
            if (this.<Type>getState(TYPE) == Type.STICKY) {
                val |= 0x8;
            }
            return val;
        }

    }
}
