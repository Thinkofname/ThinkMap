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
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.MushroomVariant;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;

public class BlockMushroom extends BlockFactory {

    public static final String PORES = "mushroom_block_inside";
    public static final String SKIN = "mushroom_block_skin_$type";
    public static final String STEM = "mushroom_block_skin_stem";

    public final StateKey<MushroomVariant> VARIANT = stateAllocator.alloc("variant", new EnumState<>(MushroomVariant.class));

    private final Texture[] textures = new Texture[MushroomVariant.values().length * 6];

    public BlockMushroom(IMapViewer iMapViewer, String type) {
        super(iMapViewer);

        MushroomVariant[] values = MushroomVariant.values();
        for (int i = 0; i < values.length; i++) {
            for (Face face : Face.values()) {
                textures[i * 6 + face.ordinal()] = mapViewer.getBlockTexture("minecraft:" + values[i].getTextures()[face.ordinal()].replaceAll("\\$type", type));
            }
        }
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockMushroom.this, state);
        }

        @Override
        public int getLegacyData() {
            return getState(VARIANT).getId();
        }

        @Override
        public Texture getTexture(Face face) {
            return textures[getState(VARIANT).ordinal() * 6 + face.ordinal()];
        }
    }
}
