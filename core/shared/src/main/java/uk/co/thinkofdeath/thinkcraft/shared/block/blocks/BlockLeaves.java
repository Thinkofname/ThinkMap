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
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.TreeVariant;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.BooleanState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;

public class BlockLeaves extends BlockFactory {

    public final StateKey<? extends Enum> VARIANT;
    public final StateKey<Boolean> CHECK_DECAY = stateAllocator.alloc("check_decay", new BooleanState());
    public final StateKey<Boolean> DECAYABLE = stateAllocator.alloc("decayable", new BooleanState());

    private final Texture[] textures;

    public BlockLeaves(IMapViewer iMapViewer, Class<? extends Enum> v) {
        super(iMapViewer);
        VARIANT = stateAllocator.alloc("variant", new EnumState<>(v));

        textures = new Texture[TreeVariant.values().length];
        for (TreeVariant variant : TreeVariant.values()) {
            textures[variant.ordinal()] = iMapViewer.getBlockTexture("minecraft:leaves_" + variant);
        }
    }


    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {
        public BlockImpl(StateMap states) {
            super(BlockLeaves.this, states);
        }

        @Override
        public Texture getTexture(Face face) {
            return textures[getState(VARIANT).ordinal()];
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = super.getModel();

                for (ModelFace face : model.getFaces()) {
                    face.useFoliageBiomeColour();
                }
                model.forceShade();
            }
            return model;
        }

        @Override
        public int getLegacyData() {
            int val = getState(VARIANT).ordinal();
            if (!getState(DECAYABLE)) {
                val |= 0x4;
            }
            if (getState(CHECK_DECAY)) {
                val |= 0x8;
            }
            return val;
        }

        @Override
        public int getColour(Face face) {
            return 0x52941C;
        }
    }
}
