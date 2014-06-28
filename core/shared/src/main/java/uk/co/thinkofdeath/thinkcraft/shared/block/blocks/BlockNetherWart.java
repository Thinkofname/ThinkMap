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
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;

public class BlockNetherWart extends BlockFactory {

    public final StateKey<Integer> STAGE = stateAllocator.alloc("stage", new IntegerState(0, 3));

    private final Texture[] textures;


    public BlockNetherWart(IMapViewer iMapViewer) {
        super(iMapViewer);

        textures = new Texture[4];
        for (int i = 0; i < 4; i++) {
            String ext = Integer.toString(
                    i >= 2 ? i - 1 : i
            );
            textures[i] = mapViewer.getTexture("nether_wart_stage_" + ext);
        }
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockNetherWart.this, state);
        }

        @Override
        public int getLegacyData() {
            return getState(STAGE);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                int stage = getState(STAGE);
                Texture texture = textures[stage];

                model.addFace(new ModelFace(Face.LEFT, texture, 0, 0, 16, 16, 4));
                model.addFace(new ModelFace(Face.RIGHT, texture, 0, 0, 16, 16, 4));

                model.addFace(new ModelFace(Face.LEFT, texture, 0, 0, 16, 16, 12));
                model.addFace(new ModelFace(Face.RIGHT, texture, 0, 0, 16, 16, 12));

                model.addFace(new ModelFace(Face.FRONT, texture, 0, 0, 16, 16, 4));
                model.addFace(new ModelFace(Face.BACK, texture, 0, 0, 16, 16, 4));

                model.addFace(new ModelFace(Face.FRONT, texture, 0, 0, 16, 16, 12));
                model.addFace(new ModelFace(Face.BACK, texture, 0, 0, 16, 16, 12));

                model = new Model().join(model, 0, -1, 0);
            }
            return model;
        }
    }
}
