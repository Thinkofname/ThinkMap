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
import uk.co.thinkofdeath.mapviewer.shared.model.ModelFace;

public class BlockChest extends BlockFactory {

    public static final String FACING = "facing";

    private final Texture chestSideFront;
    private final Texture chestSide;
    private final Texture chestBottomTop;
    private final Texture chestTop;
    private final Texture chestTopBottom;
    private final Texture chestLock;

    public BlockChest(IMapViewer iMapViewer) {
        super(iMapViewer);
        addState(FACING, new EnumState(Facing.class));

        chestSideFront = iMapViewer.getTexture("chest_side_front");
        chestSide = iMapViewer.getTexture("chest_side");
        chestBottomTop = iMapViewer.getTexture("chest_bottom_top");
        chestTop = iMapViewer.getTexture("chest_top");
        chestTopBottom = iMapViewer.getTexture("chest_top_bottom");
        chestLock = iMapViewer.getTexture("chest_lock");
    }

    public static enum Facing {
        NORTH(2),
        SOUTH(0),
        WEST(1),
        EAST(3);

        public final int rotation;

        Facing(int rotation) {
            this.rotation = rotation;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {

        BlockImpl(StateMap state) {
            super(BlockChest.this, state);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                Facing facing = getState(FACING);
                model = new Model();

                // Bottom
                model.addFace(new ModelFace(Face.FRONT, chestSideFront, 1, 0, 14, 10, 15)
                        .setTextureSize(0, 0, 14, 10));
                model.addFace(new ModelFace(Face.BACK, chestSide, 1, 0, 14, 10, 1)
                        .setTextureSize(0, 0, 14, 10));
                model.addFace(new ModelFace(Face.LEFT, chestSide, 1, 0, 14, 10, 15)
                        .setTextureSize(0, 0, 14, 10));
                model.addFace(new ModelFace(Face.RIGHT, chestSide, 1, 0, 14, 10, 1)
                        .setTextureSize(0, 0, 14, 10));
                model.addFace(new ModelFace(Face.TOP, chestBottomTop, 1, 1, 14, 14, 10)
                        .setTextureSize(0, 0, 14, 14));
                model.addFace(new ModelFace(Face.BOTTOM, chestTop, 1, 1, 14, 14, 0, true)
                        .setTextureSize(0, 0, 14, 14));

                // Top
                Model modelTop = new Model();
                modelTop.addFace(new ModelFace(Face.FRONT, chestSideFront, 1, 0, 14, 5, 15)
                        .setTextureSize(0, 10, 14, 5));
                modelTop.addFace(new ModelFace(Face.BACK, chestSide, 1, 0, 14, 5, 1)
                        .setTextureSize(0, 10, 14, 5));
                modelTop.addFace(new ModelFace(Face.LEFT, chestSide, 1, 0, 14, 5, 15)
                        .setTextureSize(0, 10, 14, 5));
                modelTop.addFace(new ModelFace(Face.RIGHT, chestSide, 1, 0, 14, 5, 1)
                        .setTextureSize(0, 10, 14, 5));
                modelTop.addFace(new ModelFace(Face.TOP, chestTop, 1, 1, 14, 14, 5)
                        .setTextureSize(0, 0, 14, 14));
                modelTop.addFace(new ModelFace(Face.BOTTOM, chestTopBottom, 1, 1, 14, 14, 0, true)
                        .setTextureSize(0, 0, 14, 14));


                // Lock
                modelTop.addFace(new ModelFace(Face.FRONT, chestLock, 7, -2, 2, 4, 16, true)
                        .setTextureSize(1, 1, 2, 4));
                modelTop.addFace(new ModelFace(Face.LEFT, chestLock, 15, -2, 1, 4, 9)
                        .setTextureSize(0, 1, 1, 4));
                modelTop.addFace(new ModelFace(Face.RIGHT, chestLock, 15, -2, 1, 4, 7)
                        .setTextureSize(0, 1, 1, 4));
                modelTop.addFace(new ModelFace(Face.TOP, chestLock, 7, 15, 2, 1, 2)
                        .setTextureSize(1, 0, 2, 1));
                modelTop.addFace(new ModelFace(Face.BOTTOM, chestLock, 7, 15, 2, 1, -2)
                        .setTextureSize(3, 0, 2, 1));

                model.join(modelTop, 0, 9, 0);
                model.rotateY(90 * facing.rotation);
            }
            return model;
        }

        @Override
        public int getLegacyData() {
            return this.<Facing>getState(FACING).ordinal() + 2;
        }
    }
}
