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
import uk.co.thinkofdeath.mapviewer.shared.block.states.IntegerState;
import uk.co.thinkofdeath.mapviewer.shared.block.states.StateKey;
import uk.co.thinkofdeath.mapviewer.shared.block.states.StateMap;
import uk.co.thinkofdeath.mapviewer.shared.model.Model;
import uk.co.thinkofdeath.mapviewer.shared.model.ModelFace;

public class BlockFloorSign extends BlockFactory {

    public final StateKey<Integer> ROTATION = stateAllocator.alloc("rotation", new IntegerState(0, 15));

    private final Texture sign;
    private final Texture post;

    public BlockFloorSign(IMapViewer iMapViewer) {
        super(iMapViewer);

        sign = mapViewer.getTexture("planks_oak");
        post = mapViewer.getTexture("log_oak");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockFloorSign.this, state);
        }

        @Override
        public int getLegacyData() {
            return getState(ROTATION);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                Model p = new Model();
                p.addFace(new ModelFace(Face.FRONT, post, 0, 0, 1, 9, 1)
                        .setTextureSize(0, 0, 2, 12));
                p.addFace(new ModelFace(Face.BACK, post, 0, 0, 1, 9, 0)
                        .setTextureSize(0, 0, 2, 12));
                p.addFace(new ModelFace(Face.LEFT, post, 0, 0, 1, 9, 1)
                        .setTextureSize(0, 0, 2, 12));
                p.addFace(new ModelFace(Face.RIGHT, post, 0, 0, 1, 9, 0)
                        .setTextureSize(0, 0, 2, 12));

                model.join(BlockSign.createModel(sign), 0, 5, 7.5f).join(p, 7.5f, 0, 7.5f);
                int rotation = getState(ROTATION);
                model.rotateY((360f / 16f) * rotation);
            }
            return model;
        }
    }
}
