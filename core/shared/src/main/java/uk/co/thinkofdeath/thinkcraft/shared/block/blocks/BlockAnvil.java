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

public class BlockAnvil extends BlockFactory {

    public final StateKey<Integer> DAMAGE = stateAllocator.alloc("damage", new IntegerState(0, 2));
    public final StateKey<Integer> ROTATION = stateAllocator.alloc("rotation", new IntegerState(0, 3));

    private final Texture base;
    private final Texture[] top = new Texture[3];

    public BlockAnvil(IMapViewer iMapViewer) {
        super(iMapViewer);

        base = mapViewer.getTexture("anvil_base");
        for (int i = 0; i < top.length; i++) {
            top[i] = mapViewer.getTexture("anvil_top_damaged_" + i);
        }
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockAnvil.this, state);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                model.addFace(new ModelFace(Face.TOP, top[getState(DAMAGE)], 3, 0, 10, 16, 16, true));
                model.addFace(new ModelFace(Face.TOP, base, 4, 3, 8, 10, 5));
                model.addFace(new ModelFace(Face.TOP, base, 2, 2, 12, 12, 4));

                model.addFace(new ModelFace(Face.LEFT, base, 0, 10, 16, 6, 13));
                model.addFace(new ModelFace(Face.LEFT, base, 4, 5, 8, 5, 10));
                model.addFace(new ModelFace(Face.LEFT, base, 3, 4, 10, 1, 12));
                model.addFace(new ModelFace(Face.LEFT, base, 2, 0, 12, 4, 14));

                model.addFace(new ModelFace(Face.FRONT, base, 3, 10, 10, 6, 16));
                model.addFace(new ModelFace(Face.FRONT, base, 6, 5, 4, 5, 12));
                model.addFace(new ModelFace(Face.FRONT, base, 4, 4, 8, 1, 13));
                model.addFace(new ModelFace(Face.FRONT, base, 2, 0, 12, 4, 14));

                model.addFace(new ModelFace(Face.RIGHT, base, 0, 10, 16, 6, 3));
                model.addFace(new ModelFace(Face.RIGHT, base, 4, 5, 8, 5, 6));
                model.addFace(new ModelFace(Face.RIGHT, base, 3, 4, 10, 1, 4));
                model.addFace(new ModelFace(Face.RIGHT, base, 2, 0, 12, 4, 2));

                model.addFace(new ModelFace(Face.BACK, base, 3, 10, 10, 6, 0));
                model.addFace(new ModelFace(Face.BACK, base, 6, 5, 4, 5, 4));
                model.addFace(new ModelFace(Face.BACK, base, 4, 4, 8, 1, 3));
                model.addFace(new ModelFace(Face.BACK, base, 2, 0, 12, 4, 2));

                model.addFace(new ModelFace(Face.BOTTOM, base, 2, 2, 12, 12, 0, true));
                model.addFace(new ModelFace(Face.BOTTOM, base, 3, 0, 10, 16, 10));

                model.rotateY(getState(ROTATION) * 90);
            }
            return model;
        }

        @Override
        public int getLegacyData() {
            return getState(ROTATION) | (getState(DAMAGE) << 2);
        }
    }
}
