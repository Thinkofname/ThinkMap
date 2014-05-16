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
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;

public class BlockSand extends BlockFactory {

    public static final String VARIANT = "variant";

    private final Texture sand;
    private final Texture redSand;

    public BlockSand(IMapViewer iMapViewer) {
        super(iMapViewer);
        addState(VARIANT, new EnumState(Variant.class));
        sand = iMapViewer.getTexture("sand");
        redSand = iMapViewer.getTexture("red_sand");
    }

    public static enum Variant {
        DEFAULT,
        RED;

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
            super(BlockSand.this, states);
        }

        @Override
        public Texture getTexture(Face face) {
            return getState(VARIANT) == Variant.RED ? redSand : sand;
        }

        @Override
        public int getLegacyData() {
            return this.<Variant>getState(VARIANT).ordinal();
        }
    }
}
