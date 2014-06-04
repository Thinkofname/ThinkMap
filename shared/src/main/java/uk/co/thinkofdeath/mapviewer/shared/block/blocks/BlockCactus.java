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

public class BlockCactus extends BlockFactory {

    public static final String GROWTH = "growth";

    private final Texture top;
    private final Texture bottom;
    private final Texture side;

    public BlockCactus(IMapViewer iMapViewer) {
        super(iMapViewer);
        addState(GROWTH, new IntegerState(0, 15));
        top = mapViewer.getTexture("cactus_top");
        bottom = mapViewer.getTexture("cactus_bottom");
        side = mapViewer.getTexture("cactus_side");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockCactus.this, state);
        }

        @Override
        public int getLegacyData() {
            return getState(GROWTH);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                model.addFace(new ModelFace(Face.TOP, top, 0, 0, 16, 16, 16, true));
                model.addFace(new ModelFace(Face.BOTTOM, bottom, 0, 0, 16, 16, 0, true));
                model.addFace(new ModelFace(Face.LEFT, side, 0, 0, 16, 16, 15));
                model.addFace(new ModelFace(Face.RIGHT, side, 0, 0, 16, 16, 1));
                model.addFace(new ModelFace(Face.FRONT, side, 0, 0, 16, 16, 15));
                model.addFace(new ModelFace(Face.BACK, side, 0, 0, 16, 16, 1));
            }
            return model;
        }
    }
}
