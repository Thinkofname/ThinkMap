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

import uk.co.thinkofdeath.mapviewer.shared.IMapViewer;
import uk.co.thinkofdeath.mapviewer.shared.Texture;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockFactory;
import uk.co.thinkofdeath.mapviewer.shared.block.StateMap;
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;
import uk.co.thinkofdeath.mapviewer.shared.model.Model;

public class BlockTorch extends BlockFactory {

    public static final String FACING = "facing";

    private final Texture texture;

    public BlockTorch(IMapViewer iMapViewer, String texture) {
        super(iMapViewer);
        addState(FACING, new EnumState(Facing.class));
        this.texture = mapViewer.getTexture(texture);
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    public static enum Facing {
        UP,
        EAST(1),
        WEST(3),
        SOUTH(2),
        NORTH(0);

        public final int rotation;

        Facing() {
            this.rotation = -1;
        }

        Facing(int rotation) {
            this.rotation = rotation;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockTorch.this, state);
        }

        @Override
        public int getLegacyData() {
            int val = this.<Facing>getState(FACING).ordinal();
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

                if (facing.rotation != -1) {
                    model = new Model().join(model.rotateX(22.5f), 0, 4, 5)
                            .rotateY(facing.rotation * 90);
                }
            }
            return model;
        }
    }
}
