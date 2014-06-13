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
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;
import uk.co.thinkofdeath.mapviewer.shared.block.states.StateKey;
import uk.co.thinkofdeath.mapviewer.shared.block.states.StateMap;
import uk.co.thinkofdeath.mapviewer.shared.model.Model;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelFace;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelVertex;

public class BlockChest extends BlockFactory {

    public final StateKey<Facing> FACING = stateAllocator.alloc("facing", new EnumState<>(Facing.class));

    private final Texture chest;

    public BlockChest(IMapViewer iMapViewer, String type) {
        super(iMapViewer);

        chest = iMapViewer.getTexture("chest_" + type);
    }

    public static enum Facing {
        NORTH(2),
        SOUTH(0),
        WEST(1),
        EAST(3);

        public final int rotation;

        Facing(int rotation) {
            this.rotation = rotation;
        }

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
            super(BlockChest.this, state);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                Facing facing = getState(FACING);
                model = new Model();

                // Bottom
                model.addFace(new ModelFace(Face.FRONT, chest, 1, 0, 14, 10, 15)
                        .setTextureSize(0, 0, 14, 10)
                        .forEach(new ForEachIterator<ModelVertex>() {
                            @Override
                            public void run(ModelVertex v) {
                                v.setTextureX(v.getTextureX() / 4f + 14f / 64f);
                                v.setTextureY(v.getTextureY() / 4f + 33f / 64f);
                            }
                        }));
                model.addFace(new ModelFace(Face.BACK, chest, 1, 0, 14, 10, 1)
                        .setTextureSize(0, 0, 14, 10).forEach(sideTexture));
                model.addFace(new ModelFace(Face.LEFT, chest, 1, 0, 14, 10, 15)
                        .setTextureSize(0, 0, 14, 10).forEach(sideTexture));
                model.addFace(new ModelFace(Face.RIGHT, chest, 1, 0, 14, 10, 1)
                        .setTextureSize(0, 0, 14, 10).forEach(sideTexture));
                model.addFace(new ModelFace(Face.TOP, chest, 1, 1, 14, 14, 10)
                        .setTextureSize(0, 0, 14, 14)
                        .forEach(new ForEachIterator<ModelVertex>() {
                            @Override
                            public void run(ModelVertex v) {
                                v.setTextureX(v.getTextureX() / 4f + 14f / 64f);
                                v.setTextureY(v.getTextureY() / 4f + 19f / 64f);
                            }
                        }));
                model.addFace(new ModelFace(Face.BOTTOM, chest, 1, 1, 14, 14, 0, true)
                        .setTextureSize(0, 0, 14, 14).forEach(bottomTexture));

                // Top
                Model modelTop = new Model();
                modelTop.addFace(new ModelFace(Face.FRONT, chest, 1, 0, 14, 5, 15)
                        .setTextureSize(0, 0, 14, 5)
                        .forEach(new ForEachIterator<ModelVertex>() {
                            @Override
                            public void run(ModelVertex v) {
                                v.setTextureX(v.getTextureX() / 4f + 14f / 64f);
                                v.setTextureY(v.getTextureY() / 4f + 14f / 64f);
                            }
                        }));
                modelTop.addFace(new ModelFace(Face.BACK, chest, 1, 0, 14, 5, 1)
                        .setTextureSize(0, 0, 14, 5).forEach(sideSmallTexture));
                modelTop.addFace(new ModelFace(Face.LEFT, chest, 1, 0, 14, 5, 15)
                        .setTextureSize(0, 0, 14, 5).forEach(sideSmallTexture));
                modelTop.addFace(new ModelFace(Face.RIGHT, chest, 1, 0, 14, 5, 1)
                        .setTextureSize(0, 0, 14, 5).forEach(sideSmallTexture));
                modelTop.addFace(new ModelFace(Face.TOP, chest, 1, 1, 14, 14, 5)
                        .setTextureSize(0, 0, 14, 14).forEach(bottomTexture));
                modelTop.addFace(new ModelFace(Face.BOTTOM, chest, 1, 1, 14, 14, 0, true)
                        .setTextureSize(0, 0, 14, 14)
                        .forEach(new ForEachIterator<ModelVertex>() {
                            @Override
                            public void run(ModelVertex v) {
                                v.setTextureX(v.getTextureX() / 4f + 28f / 64f);
                                v.setTextureY(v.getTextureY() / 4f);
                            }
                        }));


                // Lock
                modelTop.addFace(new ModelFace(Face.FRONT, chest, 7, -2, 2, 4, 16, true)
                        .setTextureSize(1, 1, 2, 4).forEach(lockTexture));
                modelTop.addFace(new ModelFace(Face.LEFT, chest, 15, -2, 1, 4, 9)
                        .setTextureSize(0, 1, 1, 4).forEach(lockTexture));
                modelTop.addFace(new ModelFace(Face.RIGHT, chest, 15, -2, 1, 4, 7)
                        .setTextureSize(0, 1, 1, 4).forEach(lockTexture));
                modelTop.addFace(new ModelFace(Face.TOP, chest, 7, 15, 2, 1, 2)
                        .setTextureSize(1, 0, 2, 1).forEach(lockTexture));
                modelTop.addFace(new ModelFace(Face.BOTTOM, chest, 7, 15, 2, 1, -2)
                        .setTextureSize(3, 0, 2, 1).forEach(lockTexture));

                model.join(modelTop, 0, 9, 0);
                model.rotateY(90 * facing.rotation);
            }
            return model;
        }

        @Override
        public int getLegacyData() {
            return getState(FACING).ordinal() + 2;
        }
    }

    private static final ForEachIterator<ModelVertex> lockTexture = new ForEachIterator<ModelVertex>() {
        @Override
        public void run(ModelVertex v) {
            v.setTextureX(v.getTextureX() / 4f);
            v.setTextureY(v.getTextureY() / 4f);
        }
    };
    private static final ForEachIterator<ModelVertex> sideTexture = new ForEachIterator<ModelVertex>() {
        @Override
        public void run(ModelVertex v) {
            v.setTextureX(v.getTextureX() / 4f);
            v.setTextureY(v.getTextureY() / 4f + 33f / 64f);
        }
    };
    private static final ForEachIterator<ModelVertex> bottomTexture = new ForEachIterator<ModelVertex>() {
        @Override
        public void run(ModelVertex v) {
            v.setTextureX(v.getTextureX() / 4f + 14f / 64f);
            v.setTextureY(v.getTextureY() / 4f);
        }
    };
    private static final ForEachIterator<ModelVertex> sideSmallTexture = new ForEachIterator<ModelVertex>() {
        @Override
        public void run(ModelVertex v) {
            v.setTextureX(v.getTextureX() / 4f);
            v.setTextureY(v.getTextureY() / 4f + 14f / 64f);
        }
    };
}
