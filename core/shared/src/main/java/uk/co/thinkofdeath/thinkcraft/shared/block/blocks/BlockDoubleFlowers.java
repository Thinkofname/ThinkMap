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
import uk.co.thinkofdeath.thinkcraft.shared.block.enums.DoubleFlowerType;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.BooleanState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.EnumState;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;
import uk.co.thinkofdeath.thinkcraft.shared.model.ModelFace;
import uk.co.thinkofdeath.thinkcraft.shared.world.World;

public class BlockDoubleFlowers extends BlockFactory {

    public final StateKey<DoubleFlowerType> TYPE = stateAllocator.alloc("type", new EnumState<>(DoubleFlowerType.class));
    public final StateKey<Boolean> TOP = stateAllocator.alloc("top", new BooleanState());

    private final Texture[] textures;
    private final Texture sunflowerBack;
    private final Texture sunflowerFront;

    public BlockDoubleFlowers(IMapViewer iMapViewer) {
        super(iMapViewer);

        textures = new Texture[DoubleFlowerType.values().length * 2];
        for (DoubleFlowerType type : DoubleFlowerType.values()) {
            textures[type.ordinal() * 2] = iMapViewer.getTexture("double_plant_" + type + "_bottom");
            textures[type.ordinal() * 2 + 1] = iMapViewer.getTexture("double_plant_" + type + "_top");
        }

        sunflowerBack = mapViewer.getTexture("double_plant_sunflower_back");
        sunflowerFront = mapViewer.getTexture("double_plant_sunflower_front");
    }


    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {
        public BlockImpl(StateMap states) {
            super(BlockDoubleFlowers.this, states);
        }

        @Override
        public Model getModel() {
            if (model == null) {
                model = BlockModels.createCross(getTexture(Face.FRONT), getState(TYPE).getColour());

                if (getState(TYPE) == DoubleFlowerType.SUNFLOWER && getState(TOP)) {
                    Model flower = new Model();
                    flower.addFace(new ModelFace(Face.LEFT, sunflowerFront, 0, 0, 16, 16, 8));
                    flower.addFace(new ModelFace(Face.RIGHT, sunflowerBack, 0, 0, 16, 16, 8));
                    flower.rotateZ(-22.5f);
                    model.join(flower, 2, 0, 0);
                }
            }
            return model;
        }

        @Override
        public Texture getTexture(Face face) {
            return textures[getState(TYPE).ordinal() * 2 + (getState(TOP) ? 1 : 0)];
        }

        @Override
        public Block update(World world, int x, int y, int z) {
            if (getState(TOP)) {
                StateMap stateMap = new StateMap(state);
                Block block = world.getBlock(x, y - 1, z);
                if (block instanceof BlockImpl) {
                    stateMap.set(TYPE, block.getState(TYPE));
                }
                return mapViewer.getBlockRegistry().get(fullName, stateMap);
            }
            return this;
        }

        @Override
        public int getLegacyData() {
            return getState(TYPE).ordinal() | (getState(TOP) ? 0x8 : 0x0);
        }
    }
}
