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
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;

public class BlockSign extends BlockFactory {

    public final StateKey<Facing> FACING = stateAllocator.alloc("facing", new EnumState<>(Facing.class, new NoVerticalFacing()));

    private final Texture texture;

    public BlockSign(IMapViewer iMapViewer) {
        super(iMapViewer);

        texture = mapViewer.getTexture("planks_oak");
    }

    public static Model createModel(Texture texture) {
        Model model = new Model();
        model.addFace(new ModelFace(Face.FRONT, texture, 0, 4, 16, 8, 1)
                .setTextureSize(18, 4, 24, 12));
        model.addFace(new ModelFace(Face.BACK, texture, 0, 4, 16, 8, 0)
                .setTextureSize(18, 4, 24, 12));
        model.addFace(new ModelFace(Face.LEFT, texture, 0, 4, 1, 8, 16)
                .setTextureSize(0, 0, 2, 12));
        model.addFace(new ModelFace(Face.RIGHT, texture, 0, 4, 1, 8, 0)
                .setTextureSize(0, 0, 2, 12));
        model.addFace(new ModelFace(Face.TOP, texture, 0, 0, 16, 1, 12)
                .setTextureSize(18, 0, 24, 2));
        model.addFace(new ModelFace(Face.BOTTOM, texture, 0, 0, 16, 1, 4)
                .setTextureSize(18, 0, 24, 2));
        return model;
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockSign.this, state);
        }

        @Override
        public int getLegacyData() {
            return getState(FACING).getNSWEOrder() + 2;
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = createModel(texture);
                Facing facing = getState(FACING);
                model.rotateY(facing.getClockwiseRotation() * 90);
            }
            return model;
        }
    }
}
