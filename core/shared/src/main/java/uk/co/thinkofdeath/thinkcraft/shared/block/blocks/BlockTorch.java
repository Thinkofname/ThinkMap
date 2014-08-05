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
import uk.co.thinkofdeath.thinkcraft.shared.Test;
import uk.co.thinkofdeath.thinkcraft.shared.Texture;
import uk.co.thinkofdeath.thinkcraft.shared.block.Block;
import uk.co.thinkofdeath.thinkcraft.shared.block.BlockFactory;
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.Facing;
import uk.co.thinkofdeath.thinkcraft.shared.block.helpers.BlockModels;
import uk.co.thinkofdeath.thinkcraft.shared.block.helpers.RedstoneConnectible;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;

public class BlockTorch extends BlockFactory {

    public final StateKey<Facing> FACING = stateAllocator.alloc("facing", new EnumState<>(Facing.class,
            new Test<Facing>() {
                @Override
                public boolean test(Facing facing) {
                    return facing != Facing.DOWN;
                }
            }));

    private final Texture texture;
    private final boolean allowRedstone;

    public BlockTorch(IMapViewer iMapViewer, String texture, boolean allowRedstone) {
        super(iMapViewer);
        this.allowRedstone = allowRedstone;

        this.texture = mapViewer.getTexture(texture);
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block implements RedstoneConnectible {

        BlockImpl(StateMap state) {
            super(BlockTorch.this, state);
        }

        @Override
        public int getLegacyData() {
            int val = getState(FACING).getDUNSWEOrder() - 1;
            if (val == 0) {
                val = 5;
            }
            return val;
        }

        @Override
        public Model getModel() {
            if (model == null) {
                Facing facing = getState(FACING);

                model = BlockModels.createTorch(texture);

                if (facing.getClockwiseRotation() != -1) {
                    model = new Model().join(model.rotateX(22.5f), 0, 4, 5)
                            .rotateY(360 - facing.getClockwiseRotation() * 90 + 270);
                }
            }
            return model;
        }

        @Override
        public boolean isRedstoneConnectible() {
            return allowRedstone;
        }
    }
}
