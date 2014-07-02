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
import uk.co.thinkofdeath.thinkcraft.shared.block.states.IntegerState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.collision.AABB;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;

public class BlockCauldron extends BlockFactory {

    public final StateKey<Integer> WATER_LEVEL = stateAllocator.alloc("waterLevel", new IntegerState(0, 3));

    private final Texture bottom;
    private final Texture inner;
    private final Texture top;
    private final Texture side;
    private final Texture water;

    public BlockCauldron(IMapViewer iMapViewer) {
        super(iMapViewer);

        bottom = mapViewer.getTexture("cauldron_bottom");
        inner = mapViewer.getTexture("cauldron_inner");
        top = mapViewer.getTexture("cauldron_top");
        side = mapViewer.getTexture("cauldron_side");
        water = mapViewer.getTexture("water_still");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockCauldron.this, state);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                model.addFace(new ModelFace(Face.BOTTOM, bottom, 0, 0, 16, 16, 0, true));
                model.addFace(new ModelFace(Face.TOP, top, 0, 0, 16, 16, 16, true));

                model.addFace(new ModelFace(Face.LEFT, side, 0, 0, 16, 16, 16, true));
                model.addFace(new ModelFace(Face.RIGHT, side, 0, 0, 16, 16, 0, true));
                model.addFace(new ModelFace(Face.FRONT, side, 0, 0, 16, 16, 16, true));
                model.addFace(new ModelFace(Face.BACK, side, 0, 0, 16, 16, 0, true));

                model.addFace(new ModelFace(Face.LEFT, side, 0, 3, 16, 13, 2)
                        .setTextureSize(0, 0, 16, 13));
                model.addFace(new ModelFace(Face.RIGHT, side, 0, 3, 16, 13, 14)
                        .setTextureSize(0, 0, 16, 13));
                model.addFace(new ModelFace(Face.FRONT, side, 0, 3, 16, 13, 2)
                        .setTextureSize(0, 0, 16, 13));
                model.addFace(new ModelFace(Face.BACK, side, 0, 3, 16, 13, 14)
                        .setTextureSize(0, 0, 16, 13));

                model.addFace(new ModelFace(Face.TOP, inner, 2, 2, 14, 14, 3));
                model.addFace(new ModelFace(Face.BOTTOM, inner, 0, 0, 16, 16, 3));

                model.addFace(new ModelFace(Face.LEFT, side, 0, 0, 16, 3, 0)
                        .setTextureSize(0, 13, 16, 3));
                model.addFace(new ModelFace(Face.RIGHT, side, 0, 0, 16, 3, 16)
                        .setTextureSize(0, 13, 16, 3));
                model.addFace(new ModelFace(Face.FRONT, side, 0, 0, 16, 3, 0)
                        .setTextureSize(0, 13, 16, 3));
                model.addFace(new ModelFace(Face.BACK, side, 0, 0, 16, 3, 16)
                        .setTextureSize(0, 13, 16, 3));

                int waterLevel = getState(WATER_LEVEL);
                if (waterLevel != 0) {
                    model.addFace(new ModelFace(Face.TOP, water, 2, 2, 14, 14, 3 + 4 * waterLevel));
                }
            }
            return model;
        }

        @Override
        public AABB[] getHitbox() {
            if (hitbox == null) {
                hitbox = new AABB[]{
                        new AABB(0, 0, 0, 1, 3d / 16d, 1),
                        new AABB(0, 3d / 16d, 0, 1, 1, 2d / 16d),
                        new AABB(14d / 16d, 3d / 16d, 0, 1, 1, 1),
                        new AABB(0, 3d / 16d, 0, 2d / 16d, 1, 1),
                        new AABB(0, 3d / 16d, 14d / 16d, 1, 1, 1)
                };
            }
            return hitbox;
        }

        @Override
        public int getLegacyData() {
            return getState(WATER_LEVEL);
        }

    }
}
