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

public class BlockLog extends BlockFactory {

    public static final String VARIANT = "variant";
    public static final String AXIS = "axis";
    private static final int BOTTOM = 0;
    private static final int TOP = 1;

    private final Texture[][] textures;

    public BlockLog(IMapViewer iMapViewer) {
        super(iMapViewer);
        addState(AXIS, new EnumState(Axis.class));
        addState(VARIANT, new EnumState(Variant.class));

        textures = new Texture[Variant.values().length][];
        for (Variant variant : Variant.values()) {
            textures[variant.ordinal()] = new Texture[]{
                    iMapViewer.getTexture("log_" + variant),
                    iMapViewer.getTexture("log_" + variant + "_top")
            };
        }
    }

    public static enum Axis {
        X(4),
        Y(0),
        Z(8),
        NONE(12);

        private final int legacy;

        Axis(int legacy) {
            this.legacy = legacy;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        /**
         * Returns the legacy value for this axis
         *
         * @return The legacy value
         */
        public int legacy() {
            return legacy;
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
        return new BlockImpl(this, states);
    }

    private class BlockImpl extends Block {
        public BlockImpl(BlockLog factory, StateMap states) {
            super(factory, states);
        }

        @Override
        public Texture getTexture(Face face) {
            Variant variant = getState(VARIANT);
            switch (this.<Axis>getState(AXIS)) {
                case X:
                    if (face == Face.LEFT || face == Face.RIGHT) {
                        return textures[variant.ordinal()][TOP];
                    }
                    break;
                case Y:
                    if (face == Face.TOP || face == Face.BOTTOM) {
                        return textures[variant.ordinal()][TOP];
                    }
                    break;
                case Z:
                    if (face == Face.FRONT || face == Face.BACK) {
                        return textures[variant.ordinal()][TOP];
                    }
                    break;
            }
            return textures[variant.ordinal()][BOTTOM];
        }

        @Override
        public int getLegacyData() {
            return this.<Variant>getState(VARIANT).ordinal()
                    + this.<Axis>getState(AXIS).legacy();
        }
    }
}
