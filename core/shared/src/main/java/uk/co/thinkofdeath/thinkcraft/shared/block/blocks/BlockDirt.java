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
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.DirtVariant;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;

public class BlockDirt extends BlockFactory {

    public final StateKey<DirtVariant> VARIANT = stateAllocator.alloc("variant", new EnumState<>(DirtVariant.class));

    private final Texture dirt;
    private final Texture dirtPodzolTop;
    private final Texture dirtPodzolSide;

    public BlockDirt(IMapViewer iMapViewer) {
        super(iMapViewer);

        dirt = iMapViewer.getBlockTexture("minecraft:dirt");
        dirtPodzolTop = iMapViewer.getBlockTexture("minecraft:dirt_podzol_top");
        dirtPodzolSide = iMapViewer.getBlockTexture("minecraft:dirt_podzol_side");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockDirt.this, state);
        }

        @Override
        public int getLegacyData() {
            return getState(VARIANT).ordinal();
        }

        @Override
        public Texture getTexture(Face face) {
            if (getState(VARIANT) == DirtVariant.PODZOL) {
                switch (face) {
                    case TOP:
                        return dirtPodzolTop;
                    case BOTTOM:
                        return dirt;
                    default:
                        return dirtPodzolSide;
                }
            } else {
                return dirt;
            }
        }
    }
}
