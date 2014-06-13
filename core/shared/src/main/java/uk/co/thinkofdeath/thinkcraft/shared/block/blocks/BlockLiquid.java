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

import uk.co.thinkofdeath.thinkcraft.shared.IMapViewer;
import uk.co.thinkofdeath.thinkcraft.shared.block.Block;
import uk.co.thinkofdeath.thinkcraft.shared.block.BlockFactory;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.BooleanState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.IntegerState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;

public class BlockLiquid extends BlockFactory {

    public final StateKey<Integer> LEVEL = stateAllocator.alloc("level", new IntegerState(0, 7));
    public final StateKey<Boolean> FALLING = stateAllocator.alloc("falling", new BooleanState());

    public BlockLiquid(IMapViewer iMapViewer) {
        super(iMapViewer);
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {
        public BlockImpl(StateMap states) {
            super(BlockLiquid.this, states);
        }

        @Override
        public int getLegacyData() {
            return getState(LEVEL)
                    | (getState(FALLING) ? 8 : 0);
        }

        @Override
        public boolean shouldRenderAgainst(Block other) {
            return super.shouldRenderAgainst(other) && !(other instanceof BlockImpl);
        }
    }
}
