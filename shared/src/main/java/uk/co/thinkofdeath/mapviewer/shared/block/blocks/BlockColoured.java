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

public class BlockColoured extends BlockFactory {

    public static final String COLOUR = "color";

    private final Texture[] textures;

    public BlockColoured(IMapViewer mapViewer, String prefix) {
        super(mapViewer);

        addState(COLOUR, new EnumState(Colour.class));

        textures = new Texture[Colour.values().length];
        for (Colour colour : Colour.values()) {
            textures[colour.ordinal()] = mapViewer.getTexture(prefix + colour.texture);
        }
    }

    public static enum Colour {
        WHITE,
        ORANGE,
        MAGENTA,
        LIGHT_BLUE("lightBlue", "light_blue"), // Because keeping a single standard is hard
        YELLOW,
        LIME,
        PINK,
        GRAY,
        SILVER,
        CYAN,
        PURPLE,
        BLUE,
        BROWN,
        GREEN,
        RED,
        BLACK;

        public final String name;
        public final String texture;

        Colour() {
            name = name().toLowerCase();
            texture = name;
        }

        Colour(String name, String texture) {
            this.name = name;
            this.texture = texture;
        }

        @Override
        public String toString() {
            return name;
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
            return textures[this.<Colour>getState(COLOUR).ordinal()];
        }

        @Override
        public int getLegacyData() {
            return this.<Colour>getState(COLOUR).ordinal();
        }
    }
}
