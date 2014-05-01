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

import uk.co.thinkofdeath.mapviewer.shared.Face;
import uk.co.thinkofdeath.mapviewer.shared.IMapViewer;
import uk.co.thinkofdeath.mapviewer.shared.Texture;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockFactory;
import uk.co.thinkofdeath.mapviewer.shared.block.StateMap;
import uk.co.thinkofdeath.mapviewer.shared.block.states.EnumState;
import uk.co.thinkofdeath.mapviewer.shared.model.Model;

public class BlockTallGrass extends BlockFactory {

    public static final String TYPE = "type";

    private final Texture[] textures;

    public BlockTallGrass(IMapViewer iMapViewer) {
        super(iMapViewer);
        addState(TYPE, new EnumState(Type.class));

        textures = new Texture[Type.values().length];
        for (Type type : Type.values()) {
            textures[type.ordinal()] = iMapViewer.getTexture(type.toString());
        }
    }

    public static enum Type {
        DEADBUSH,
        TALLGRASS,
        FERN;


        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }


    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {
        public BlockImpl(StateMap states) {
            super(BlockTallGrass.this, states);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                int colour = 0xFFFFFF;
                if (this.<Type>getState(TYPE) != Type.DEADBUSH) {
                    colour = 0xA7D389;
                }
                model = BlockModels.createCross(getTexture(Face.FRONT), colour);
            }
            return model;
        }

        @Override
        public Texture getTexture(Face face) {
            return textures[this.<Type>getState(TYPE).ordinal()];
        }

        @Override
        public int getLegacyData() {
            return this.<Type>getState(TYPE).ordinal();
        }
    }
}
