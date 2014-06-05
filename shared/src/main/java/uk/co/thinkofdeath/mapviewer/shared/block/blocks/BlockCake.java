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
import uk.co.thinkofdeath.mapviewer.shared.IMapViewer;
import uk.co.thinkofdeath.mapviewer.shared.Texture;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockFactory;
import uk.co.thinkofdeath.mapviewer.shared.block.StateMap;
import uk.co.thinkofdeath.mapviewer.shared.block.states.IntegerState;
import uk.co.thinkofdeath.mapviewer.shared.model.Model;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelFace;

public class BlockCake extends BlockFactory {

    public static final String EATEN = "eaten";

    private final Texture top;
    private final Texture side;
    private final Texture sideInner;
    private final Texture bottom;

    public BlockCake(IMapViewer iMapViewer) {
        super(iMapViewer);
        addState(EATEN, new IntegerState(0, 7));

        top = mapViewer.getTexture("cake_top");
        side = mapViewer.getTexture("cake_side");
        sideInner = mapViewer.getTexture("cake_inner");
        bottom = mapViewer.getTexture("cake_bottom");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockCake.this, state);
        }

        @Override
        public int getLegacyData() {
            return this.<Integer>getState(EATEN);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                int remaining = 8 - this.<Integer>getState(EATEN);
                int len = (int) (14f / 8f * remaining);

                model.addFace(new ModelFace(Face.TOP, top, 1, 1, len, 14, 8));
                model.addFace(new ModelFace(Face.LEFT,
                        remaining == 8 ? side : sideInner,
                        1, 0, 14, 8, 1 + len));
                model.addFace(new ModelFace(Face.RIGHT, side, 1, 0, 14, 8, 1));
                model.addFace(new ModelFace(Face.FRONT, side, 1, 0, len, 8, 15));
                model.addFace(new ModelFace(Face.BACK, side, 1, 0, len, 8, 1));
                model.addFace(new ModelFace(Face.BOTTOM, bottom, 1, 1, len, 14, 0, true));

                // Because lazy
                model.rotateY(180);
                model.realignTextures();
            }
            return model;
        }
    }
}
