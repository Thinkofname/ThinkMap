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
import uk.co.thinkofdeath.thinkcraft.shared.ForEachIterator;
import uk.co.thinkofdeath.thinkcraft.shared.IMapViewer;
import uk.co.thinkofdeath.thinkcraft.shared.Texture;
import uk.co.thinkofdeath.thinkcraft.shared.block.Block;
import uk.co.thinkofdeath.thinkcraft.shared.block.BlockFactory;
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.BedPart;
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.Facing;
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.NoVerticalFacing;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.BooleanState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelVertex;

public class BlockBed extends BlockFactory {

    private final StateKey<Facing> FACING = stateAllocator.alloc("facing", new EnumState<>(Facing.class, new NoVerticalFacing()));
    private final StateKey<Boolean> OCCUPIED = stateAllocator.alloc("occupied", new BooleanState());
    private final StateKey<BedPart> PART = stateAllocator.alloc("part", new EnumState<>(BedPart.class));

    private final Texture bedHeadTop;
    private final Texture bedHeadEnd;
    private final Texture bedHeadSide;

    private final Texture bedFeetTop;
    private final Texture bedFeetEnd;
    private final Texture bedFeetSide;


    public BlockBed(IMapViewer iMapViewer) {
        super(iMapViewer);

        bedHeadTop = iMapViewer.getTexture("bed_head_top");
        bedHeadEnd = iMapViewer.getTexture("bed_head_end");
        bedHeadSide = iMapViewer.getTexture("bed_head_side");

        bedFeetTop = iMapViewer.getTexture("bed_feet_top");
        bedFeetEnd = iMapViewer.getTexture("bed_feet_end");
        bedFeetSide = iMapViewer.getTexture("bed_feet_side");
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {
        public BlockImpl(StateMap states) {
            super(BlockBed.this, states);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = new Model();
                if (getState(PART) == BedPart.HEAD) {
                    model.addFace(new ModelFace(Face.TOP, bedHeadTop, 0, 0, 16, 16,
                            9));
                    model.addFace(new ModelFace(Face.LEFT, bedHeadEnd, 0, 0, 16, 9, 16, true)
                            .setTextureSize(0, 7, 16, 9));
                    model.addFace(new ModelFace(Face.FRONT, bedHeadSide, 0, 0, 16, 9, 16,
                            true)
                            .setTextureSize(0, 7, 16, 9));
                    model.addFace(new ModelFace(Face.BACK, bedHeadSide, 0, 0, 16, 9, 0, true)
                            .setTextureSize(0, 7, 16, 9)
                            .forEach(new ForEachIterator<ModelVertex>() {
                                @Override
                                public void run(ModelVertex v) {
                                    v.setTextureX(1 - v.getTextureX());
                                }
                            }));

                } else {
                    model.addFace(new ModelFace(Face.TOP, bedFeetTop, 0, 0, 16, 16, 9));
                    model.addFace(new ModelFace(Face.RIGHT, bedFeetEnd, 0, 0, 16, 9, 0, true)
                            .setTextureSize(0, 7, 16, 9));
                    model.addFace(new ModelFace(Face.FRONT, bedFeetSide, 0, 0, 16, 9, 16,
                            true)
                            .setTextureSize(0, 7, 16, 9));
                    model.addFace(new ModelFace(Face.BACK, bedFeetSide, 0, 0, 16, 9, 0, true)
                            .setTextureSize(0, 7, 16, 9)
                            .forEach(new ForEachIterator<ModelVertex>() {
                                @Override
                                public void run(ModelVertex v) {
                                    v.setTextureX(1 - v.getTextureX());
                                }
                            }));
                }
                model.rotateY(90 + getState(FACING).getClockwiseRotation() * 90);
            }
            return model;
        }

        @Override
        public int getLegacyData() {
            int val = getState(FACING).getClockwiseRotation();
            if (getState(OCCUPIED)) {
                val |= 0x4;
            }
            if (getState(PART) == BedPart.HEAD) {
                val |= 0x8;
            }
            return val;
        }
    }
}
