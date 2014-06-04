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

public class BlockSnowLayer extends BlockFactory {

    public static final String HEIGHT = "height";

    private final Texture texture;

    public BlockSnowLayer(IMapViewer iMapViewer) {
        super(iMapViewer);
        addState(HEIGHT, new IntegerState(1, 8));

        texture = mapViewer.getTexture("snow");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockSnowLayer.this, state);
        }

        @Override
        public int getLegacyData() {
            return this.<Integer>getState(HEIGHT) - 1;
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                int height = getState(HEIGHT);

                model.addFace(new ModelFace(Face.TOP, texture, 0, 0, 16, 16, height * 2, height == 8));
                model.addFace(new ModelFace(Face.BOTTOM, texture, 0, 0, 16, 16, 0));
                model.addFace(new ModelFace(Face.LEFT, texture, 0, 0, 16, height * 2, 16, true));
                model.addFace(new ModelFace(Face.RIGHT, texture, 0, 0, 16, height * 2, 0, true));
                model.addFace(new ModelFace(Face.FRONT, texture, 0, 0, 16, height * 2, 16, true));
                model.addFace(new ModelFace(Face.BACK, texture, 0, 0, 16, height * 2, 0, true));
            }
            return model;
        }
    }
}
