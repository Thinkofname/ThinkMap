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

public class BlockQuartz extends BlockFactory {

    public static final String VARIANT = "variant";

    private final Texture quartzTop;
    private final Texture quartzSide;
    private final Texture quartzBottom;
    private final Texture quartzChiseledTop;
    private final Texture quartzChiseledSide;
    private final Texture quartzPillarTop;
    private final Texture quartzPillarSide;


    public BlockQuartz(IMapViewer iMapViewer) {
        super(iMapViewer);
        addState(VARIANT, new EnumState(Variant.class));
        quartzTop = iMapViewer.getTexture("quartz_block_top");
        quartzSide = iMapViewer.getTexture("quartz_block_side");
        quartzBottom = iMapViewer.getTexture("quartz_block_bottom");
        quartzChiseledTop = iMapViewer.getTexture("quartz_block_chiseled_top");
        quartzChiseledSide = iMapViewer.getTexture("quartz_block_chiseled");
        quartzPillarTop = iMapViewer.getTexture("quartz_block_lines_top");
        quartzPillarSide = iMapViewer.getTexture("quartz_block_lines");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    public static enum Variant {
        DEFAULT,
        CHISELED,
        PILLAR;

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private class BlockImpl extends Block {

        public BlockImpl(StateMap state) {
            super(BlockQuartz.this, state);
        }

        @Override
        public int getLegacyData() {
            return this.<Variant>getState(VARIANT).ordinal();
        }

        @Override
        public Texture getTexture(Face face) {
            if (getState(VARIANT) == Variant.CHISELED) {
                switch (face) {
                    case TOP:
                    case BOTTOM:
                        return quartzChiseledTop;
                    default:
                        return quartzChiseledSide;
                }
            } else if (getState(VARIANT) == Variant.PILLAR) {
                switch (face) {
                    case TOP:
                    case BOTTOM:
                        return quartzPillarTop;
                    default:
                        return quartzPillarSide;
                }
            } else {
                switch (face) {
                    case TOP:
                        return quartzTop;
                    case BOTTOM:
                        return quartzBottom;
                    default:
                        return quartzSide;
                }
            }
        }
    }
}
