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
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;

public class BlockEnchantingTable extends BlockFactory {

    private final Texture top;
    private final Texture side;
    private final Texture bottom;

    public BlockEnchantingTable(IMapViewer iMapViewer) {
        super(iMapViewer);

        top = mapViewer.getBlockTexture("minecraft:enchanting_table_top");
        side = mapViewer.getBlockTexture("minecraft:enchanting_table_side");
        bottom = mapViewer.getBlockTexture("minecraft:enchanting_table_bottom");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockEnchantingTable.this, state);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                model.addFace(new ModelFace(Face.TOP, top, 0, 0, 16, 16, 12));
                model.addFace(new ModelFace(Face.BOTTOM, bottom, 0, 0, 16, 16, 0, true));
                model.addFace(new ModelFace(Face.LEFT, side, 0, 0, 16, 12, 16, true)
                        .setTextureSize(0, 4, 16, 12));
                model.addFace(new ModelFace(Face.RIGHT, side, 0, 0, 16, 12, 0, true)
                        .setTextureSize(0, 4, 16, 12));
                model.addFace(new ModelFace(Face.FRONT, side, 0, 0, 16, 12, 16, true)
                        .setTextureSize(0, 4, 16, 12));
                model.addFace(new ModelFace(Face.BACK, side, 0, 0, 16, 12, 0, true)
                        .setTextureSize(0, 4, 16, 12));
            }
            return model;
        }
    }
}
