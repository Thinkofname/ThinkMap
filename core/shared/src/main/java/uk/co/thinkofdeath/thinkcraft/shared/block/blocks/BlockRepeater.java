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
import uk.co.thinkofdeath.thinkcraft.shared.block.helpers.BlockModels;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.IntegerState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;

public class BlockRepeater extends BlockFactory {

    public final StateKey<Facing> FACING = stateAllocator.alloc("facing", new EnumState<>(Facing.class, new NoVerticalFacing()));
    public final StateKey<Integer> DELAY = stateAllocator.alloc("delay", new IntegerState(1, 4));

    private final Texture repeater;
    private final Texture torch;

    public BlockRepeater(IMapViewer iMapViewer, boolean powered) {
        super(iMapViewer);

        repeater = mapViewer.getTexture("repeater_" + (powered ? "on" : "off"));
        torch = mapViewer.getTexture("redstone_torch_" + (powered ? "on" : "off"));
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockRepeater.this, state);
        }

        @Override
        public int getLegacyData() {
            int val = (2 + getState(FACING).getClockwiseRotation()) % 4;
            int delay = getState(DELAY);
            val |= (delay - 1) << 2;
            return val;
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                Facing facing = getState(FACING);
                int delay = getState(DELAY);
                delay--;

                model.addFace(new ModelFace(Face.TOP, repeater, 0, 0, 16, 16, 2));
                model.addFace(new ModelFace(Face.BOTTOM, repeater, 0, 0, 16, 16, 0, true));
                model.addFace(new ModelFace(Face.LEFT, repeater, 0, 0, 16, 2, 16, true));
                model.addFace(new ModelFace(Face.RIGHT, repeater, 0, 0, 16, 2, 0, true));
                model.addFace(new ModelFace(Face.FRONT, repeater, 0, 0, 16, 2, 16, true));
                model.addFace(new ModelFace(Face.BACK, repeater, 0, 0, 16, 2, 0, true));

                model.join(BlockModels.createTorch(torch), 0, -3, -5);
                model.join(BlockModels.createTorch(torch), 0, -3, -1 + 2 * delay);
                model.rotateY(facing.ordinal() * 90);
            }
            return model;
        }
    }
}
