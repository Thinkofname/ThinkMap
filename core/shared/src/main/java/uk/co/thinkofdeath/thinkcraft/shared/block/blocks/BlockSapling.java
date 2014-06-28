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
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.SaplingType;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.IntegerState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;

public class BlockSapling extends BlockFactory {

    public final StateKey<SaplingType> TYPE = stateAllocator.alloc("type", new EnumState<>(SaplingType.class));
    public final StateKey<Integer> STAGE = stateAllocator.alloc("stage", new IntegerState(0, 1));

    private final Texture[] textures;

    public BlockSapling(IMapViewer iMapViewer) {
        super(iMapViewer);

        textures = new Texture[SaplingType.values().length];
        for (SaplingType type : SaplingType.values()) {
            textures[type.ordinal()] = iMapViewer.getTexture("sapling_" + type);
        }
    }


    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {
        public BlockImpl(StateMap states) {
            super(BlockSapling.this, states);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = BlockModels.createCross(getTexture(Face.FRONT));
            }
            return model;
        }

        @Override
        public Texture getTexture(Face face) {
            return textures[getState(TYPE).ordinal()];
        }

        @Override
        public int getLegacyData() {
            return getState(TYPE).ordinal() | (getState(STAGE) * 8);
        }
    }
}
