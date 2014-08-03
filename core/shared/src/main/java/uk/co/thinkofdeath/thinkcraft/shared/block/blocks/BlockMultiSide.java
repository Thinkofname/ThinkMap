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
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;

public class BlockMultiSide extends BlockFactory {

    private final Texture[] textures = new Texture[Face.values().length];

    public BlockMultiSide(IMapViewer iMapViewer,
                          String top, String bottom,
                          String left, String right,
                          String front, String back) {
        super(iMapViewer);

        textures[Face.TOP.ordinal()] = mapViewer.getBlockTexture("minecraft:" + top);
        textures[Face.BOTTOM.ordinal()] = mapViewer.getBlockTexture("minecraft:" + bottom);
        textures[Face.LEFT.ordinal()] = mapViewer.getBlockTexture("minecraft:" + left);
        textures[Face.RIGHT.ordinal()] = mapViewer.getBlockTexture("minecraft:" + right);
        textures[Face.FRONT.ordinal()] = mapViewer.getBlockTexture("minecraft:" + front);
        textures[Face.BACK.ordinal()] = mapViewer.getBlockTexture("minecraft:" + back);
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockMultiSide.this, state);
        }

        @Override
        public Texture getTexture(Face face) {
            return textures[face.ordinal()];
        }
    }
}
