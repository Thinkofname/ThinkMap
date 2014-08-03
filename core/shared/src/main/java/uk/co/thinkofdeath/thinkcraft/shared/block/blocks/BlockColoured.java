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

public class BlockColoured extends BlockFactory {

    public final StateKey<Colour> COLOUR = stateAllocator.alloc("color", new EnumState<>(Colour.class));

    private final Texture[] textures;

    public BlockColoured(IMapViewer mapViewer, String prefix) {
        super(mapViewer);

        textures = new Texture[Colour.values().length];
        for (Colour colour : Colour.values()) {
            textures[colour.ordinal()] = mapViewer.getBlockTexture("minecraft:" + prefix + colour.texture);
        }
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {
        public BlockImpl(StateMap states) {
            super(BlockColoured.this, states);
        }

        @Override
        public Texture getTexture(Face face) {
            return textures[getState(COLOUR).ordinal()];
        }

        @Override
        public int getLegacyData() {
            return getState(COLOUR).ordinal();
        }
    }
}
