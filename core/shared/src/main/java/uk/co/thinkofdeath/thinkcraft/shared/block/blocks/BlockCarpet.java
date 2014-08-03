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
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.Colour;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;

public class BlockCarpet extends BlockFactory {

    public final StateKey<Colour> COLOUR = stateAllocator.alloc("color", new EnumState<>(Colour.class));

    private final Texture[] textures;

    public BlockCarpet(IMapViewer iMapViewer) {
        super(iMapViewer);

        textures = new Texture[Colour.values().length];
        for (Colour colour : Colour.values()) {
            textures[colour.ordinal()] = mapViewer.getBlockTexture("minecraft:wool_colored_" + colour.texture);
        }
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockCarpet.this, state);
        }

        @Override
        public int getLegacyData() {
            return getState(COLOUR).ordinal();
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                Texture texture = textures[getState(COLOUR).ordinal()];

                model.addFace(new ModelFace(Face.TOP, texture, 0, 0, 16, 16, 1, false));
                model.addFace(new ModelFace(Face.BOTTOM, texture, 0, 0, 16, 16, 0, true));
                model.addFace(new ModelFace(Face.LEFT, texture, 0, 0, 16, 1, 16, true));
                model.addFace(new ModelFace(Face.RIGHT, texture, 0, 0, 16, 1, 0, true));
                model.addFace(new ModelFace(Face.FRONT, texture, 0, 0, 16, 1, 16, true));
                model.addFace(new ModelFace(Face.BACK, texture, 0, 0, 16, 1, 0, true));
            }
            return model;
        }
    }
}
