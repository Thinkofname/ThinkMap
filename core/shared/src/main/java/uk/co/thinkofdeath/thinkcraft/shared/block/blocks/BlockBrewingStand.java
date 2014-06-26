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
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelVertex;

public class BlockBrewingStand extends BlockFactory {

    public final StateKey<Boolean> EAST = stateAllocator.alloc("east", new BooleanState());
    public final StateKey<Boolean> SOUTH_WEST = stateAllocator.alloc("SOUTH_WEST", new BooleanState());
    public final StateKey<Boolean> NORTH_WEST = stateAllocator.alloc("NORTH_WEST", new BooleanState());

    private final Texture base;
    private final Texture stand;

    public BlockBrewingStand(IMapViewer iMapViewer) {
        super(iMapViewer);

        base = mapViewer.getTexture("brewing_stand_base");
        stand = mapViewer.getTexture("brewing_stand");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }


    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockBrewingStand.this, state);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                Model b = new Model();
                b.addFace(new ModelFace(Face.TOP, base, 0, 0, 6, 6, 2));
                b.addFace(new ModelFace(Face.BOTTOM, base, 0, 0, 6, 6, 0, true));
                b.addFace(new ModelFace(Face.LEFT, base, 0, 0, 6, 2, 6));
                b.addFace(new ModelFace(Face.RIGHT, base, 0, 0, 6, 2, 0));
                b.addFace(new ModelFace(Face.FRONT, base, 0, 0, 6, 2, 6));
                b.addFace(new ModelFace(Face.BACK, base, 0, 0, 6, 2, 0));

                model.join(b, 2, 0, 1);
                model.join(b, 2, 0, 9);
                model.join(b, 9, 0, 5);

                // Stand center

                model.addFace(new ModelFace(Face.TOP, stand, 7, 7, 2, 2, 14));
                model.addFace(new ModelFace(Face.BOTTOM, stand, 7, 7, 2, 2, 0));
                model.addFace(new ModelFace(Face.LEFT, stand, 7, 0, 2, 14, 9));
                model.addFace(new ModelFace(Face.RIGHT, stand, 7, 0, 2, 14, 7));
                model.addFace(new ModelFace(Face.FRONT, stand, 7, 0, 2, 14, 9));
                model.addFace(new ModelFace(Face.BACK, stand, 7, 0, 2, 14, 7));

                model.realignTextures();

                // Potions

                model.join(createPotion(getState(EAST)));
                model.join(createPotion(getState(NORTH_WEST)).rotateY(90 + 45));
                model.join(createPotion(getState(SOUTH_WEST)).rotateY(270 - 45));

            }
            return model;
        }

        private Model createPotion(final boolean isPotion) {
            Model model = new Model();
            model.addFace(new ModelFace(Face.FRONT, stand, 9, 0, 7, 16, 8)
                    .forEach(new ForEachIterator<ModelVertex>() {
                        @Override
                        public void run(ModelVertex v) {
                            if (isPotion) {
                                v.setTextureX(1 - v.getTextureX());
                            }
                        }
                    }));
            model.addFace(new ModelFace(Face.BACK, stand, 9, 0, 7, 16, 8)
                    .forEach(new ForEachIterator<ModelVertex>() {
                        @Override
                        public void run(ModelVertex v) {
                            if (!isPotion) {
                                v.setTextureX(1 - v.getTextureX() + (9f / 16f));
                            } else {
                                v.setTextureX(v.getTextureX() + (7f / 16f));
                            }
                        }
                    }));
            return model;
        }

        @Override
        public int getLegacyData() {
            int val = 0;
            if (getState(EAST)) {
                val |= 0x1;
            }
            if (getState(SOUTH_WEST)) {
                val |= 0x2;
            }
            if (getState(NORTH_WEST)) {
                val |= 0x4;
            }
            return val;
        }
    }
}
