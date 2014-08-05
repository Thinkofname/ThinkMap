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
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.SingleSlabOnly;
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.SlabType;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.BooleanState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;

public class BlockSlab<T extends Enum<T> & SlabType> extends BlockFactory {

    public final StateKey<T> VARIANT;
    public final StateKey<Boolean> TOP = stateAllocator.alloc("top", new BooleanState());

    private final Texture[] textures;

    public BlockSlab(IMapViewer iMapViewer, Class<T> clazz) {
        super(iMapViewer);
        VARIANT = stateAllocator.alloc("variant", new EnumState<>(clazz, new SingleSlabOnly<T>()));

        textures = new Texture[clazz.getEnumConstants().length * 6];

        int i = 0;
        for (Enum e : clazz.getEnumConstants()) {
            SlabType type = (SlabType) e;
            for (Face face : Face.values()) {
                textures[i++] = iMapViewer.getTexture(type.texture(face));
            }
        }
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockSlab.this, state);
            allowSelf = true;
        }

        @Override
        public int getLegacyData() {
            int val = getState(VARIANT).ordinal();
            if (this.<Boolean>getState(TOP)) {
                val |= 0x8;
            }
            return val;
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();

                boolean top = getState(TOP);
                SlabType type = getState(VARIANT);

                int i = type.ordinal() * 6;

                model.addFace(new ModelFace(Face.TOP,
                        textures[i + Face.TOP.ordinal()], 0, 0, 16, 16, top ? 16 : 8, top));
                model.addFace(new ModelFace(Face.BOTTOM,
                        textures[i + Face.BOTTOM.ordinal()], 0, 0, 16, 16, top ? 8 : 0, !top));
                model.addFace(new ModelFace(Face.FRONT,
                        textures[i + Face.FRONT.ordinal()], 0, top ? 8 : 0, 16, 8, 16, true));
                model.addFace(new ModelFace(Face.BACK,
                        textures[i + Face.BACK.ordinal()], 0, top ? 8 : 0, 16, 8, 0, true));
                model.addFace(new ModelFace(Face.LEFT,
                        textures[i + Face.LEFT.ordinal()], 0, top ? 8 : 0, 16, 8, 16, true));
                model.addFace(new ModelFace(Face.RIGHT,
                        textures[i + Face.RIGHT.ordinal()], 0, top ? 8 : 0, 16, 8, 0, true));
            }
            return model;
        }
    }
}
