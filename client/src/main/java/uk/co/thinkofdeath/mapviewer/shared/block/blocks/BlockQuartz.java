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

public class BlockQuartz extends BlockFactory {

    private final Texture quartzTop;
    private final Texture quartzSide;
    private final Texture quartzBottom;

    public BlockQuartz(IMapViewer iMapViewer) {
        super(iMapViewer);

        quartzTop = iMapViewer.getTexture("quartz_block_top");
        quartzSide = iMapViewer.getTexture("quartz_block_side");
        quartzBottom = iMapViewer.getTexture("quartz_block_bottom");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {
        public BlockImpl(StateMap states) {
            super(BlockQuartz.this, states);
        }

        @Override
        public Texture getTexture(Face face) {
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
