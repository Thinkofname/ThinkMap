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
import uk.co.thinkofdeath.thinkcraft.shared.block.states.BooleanState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;

public class BlockPressurePlate extends BlockFactory {

    public final StateKey<Boolean> POWERED = stateAllocator.alloc("powered", new BooleanState());

    private final Texture texture;

    public BlockPressurePlate(IMapViewer iMapViewer, String texture) {
        super(iMapViewer);

        this.texture = mapViewer.getTexture(texture);
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockPressurePlate.this, state);
        }

        @Override
        public int getLegacyData() {
            return getState(POWERED) ? 1 : 0;
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                boolean powered = getState(POWERED);
                float height = powered ? 0.5f : 1f;
                model.addFace(new ModelFace(Face.TOP, texture, 1, 1, 14, 14, height));
                model.addFace(new ModelFace(Face.LEFT, texture, 1, 0, 14, height, 15));
                model.addFace(new ModelFace(Face.RIGHT, texture, 1, 0, 14, height, 1));
                model.addFace(new ModelFace(Face.FRONT, texture, 1, 0, 14, height, 15));
                model.addFace(new ModelFace(Face.BACK, texture, 1, 0, 14, height, 1));
            }
            return model;
        }
    }
}
