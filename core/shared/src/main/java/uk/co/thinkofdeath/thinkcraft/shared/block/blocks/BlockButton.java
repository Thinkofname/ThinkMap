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
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.Facing;
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.NoVerticalFacing;
import uk.co.thinkofdeath.thinkcraft.shared.block.helpers.RedstoneConnectible;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.BooleanState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;

public class BlockButton extends BlockFactory {

    public final StateKey<Boolean> PRESSED = stateAllocator.alloc("pressed", new BooleanState());
    public final StateKey<Facing> FACING = stateAllocator.alloc("facing", new EnumState<>(Facing.class, new NoVerticalFacing()));

    private final Texture texture;

    public BlockButton(IMapViewer iMapViewer, String texture) {
        super(iMapViewer);

        this.texture = mapViewer.getTexture(texture);
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block implements RedstoneConnectible {

        BlockImpl(StateMap state) {
            super(BlockButton.this, state);
        }

        @Override
        public int getLegacyData() {
            int val = 4 - getState(FACING).getNSWEOrder();
            if (getState(PRESSED)) {
                val |= 0x8;
            }
            return val;
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                float offset = getState(PRESSED) ? 1 : 2;

                model.addFace(new ModelFace(Face.FRONT, texture, 5, 6, 6, 4, offset));
                model.addFace(new ModelFace(Face.LEFT, texture, 0, 6, offset, 4, 11));
                model.addFace(new ModelFace(Face.RIGHT, texture, 0, 6, offset, 4, 5));
                model.addFace(new ModelFace(Face.TOP, texture, 5, 0, 6, offset, 10));
                model.addFace(new ModelFace(Face.BOTTOM, texture, 5, 0, 6, offset, 6));

                Facing facing = getState(FACING);
                model.rotateY(facing.getClockwiseRotation() * 90);
            }
            return model;
        }

        @Override
        public boolean isRedstoneConnectible() {
            return true;
        }
    }
}
