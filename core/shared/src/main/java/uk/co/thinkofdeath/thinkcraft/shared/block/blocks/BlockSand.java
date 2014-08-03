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
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.SandVariant;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;

public class BlockSand extends BlockFactory {

    public final StateKey<SandVariant> VARIANT = stateAllocator.alloc("variant", new EnumState<>(SandVariant.class));

    private final Texture sand;
    private final Texture redSand;

    public BlockSand(IMapViewer iMapViewer) {
        super(iMapViewer);

        sand = iMapViewer.getBlockTexture("minecraft:sand");
        redSand = iMapViewer.getBlockTexture("minecraft:red_sand");
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
            return getState(VARIANT) == SandVariant.RED ? redSand : sand;
        }

        @Override
        public int getLegacyData() {
            return getState(VARIANT).ordinal();
        }
    }
}
