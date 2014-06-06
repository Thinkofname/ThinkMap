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

public class BlockFarmland extends BlockFactory {

    public final StateKey<Integer> WETNESS = stateAllocator.alloc("wetness", new IntegerState(0, 8));

    private final Texture dry;
    private final Texture wet;
    private final Texture dirt;

    public BlockFarmland(IMapViewer iMapViewer) {
        super(iMapViewer);

        dry = mapViewer.getTexture("farmland_dry");
        wet = mapViewer.getTexture("farmland_wet");
        dirt = mapViewer.getTexture("dirt");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockFarmland.this, state);
        }

        @Override
        public int getLegacyData() {
            return getState(WETNESS);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                Texture top = getState(WETNESS) == 0 ? dry : wet;

                model.addFace(new ModelFace(Face.TOP, top, 0, 0, 16, 16, 15));
                model.addFace(new ModelFace(Face.LEFT, dirt, 0, 0, 16, 15, 16, true));
                model.addFace(new ModelFace(Face.RIGHT, dirt, 0, 0, 16, 15, 0, true));
                model.addFace(new ModelFace(Face.FRONT, dirt, 0, 0, 16, 15, 16, true));
                model.addFace(new ModelFace(Face.BACK, dirt, 0, 0, 16, 15, 0, true));
                model.addFace(new ModelFace(Face.BOTTOM, dirt, 0, 0, 16, 15, 0, true));
            }
            return model;
        }
    }
}
