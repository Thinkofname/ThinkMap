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
import uk.co.thinkofdeath.mapviewer.shared.block.states.BooleanState;
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;

public class BlockLeaves extends BlockFactory {

    public static final String VARIANT = "variant";
    public static final String CHECK_DECAY = "check_decay";
    public static final String DECAYABLE = "decayable";

    private final Texture[] textures;

    public BlockLeaves(IMapViewer iMapViewer) {
        super(iMapViewer);
        addState(CHECK_DECAY, new BooleanState());
        addState(DECAYABLE, new BooleanState());
        addState(VARIANT, new EnumState(Variant.class));

        textures = new Texture[Variant.values().length];
        for (Variant variant : Variant.values()) {
            textures[variant.ordinal()] = iMapViewer.getTexture("leaves_" + variant);
        }
    }

    public static enum Variant {
        OAK,
        SPRUCE,
        BIRCH,
        JUNGLE;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }


    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {
        public BlockImpl(StateMap states) {
            super(BlockLeaves.this, states);
        }

        @Override
        public Texture getTexture(Face face) {
            return textures[this.<Variant>getState(VARIANT).ordinal()];
        }

        @Override
        public int getLegacyData() {
            int val = this.<Variant>getState(VARIANT).ordinal();
            if (!this.<Boolean>getState(DECAYABLE)) {
                val |= 0x4;
            }
            if (this.<Boolean>getState(CHECK_DECAY)) {
                val |= 0x8;
            }
            return val;
        }

        @Override
        public int getColour(Face face) {
            return 0x52941C;
        }
    }
}
