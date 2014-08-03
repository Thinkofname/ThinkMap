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

package uk.co.thinkofdeath.thinkcraft.shared.block;

import uk.co.thinkofdeath.thinkcraft.shared.IMapViewer;
import uk.co.thinkofdeath.thinkcraft.shared.Texture;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateAllocator;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateKey;
import uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap;
import uk.co.thinkofdeath.thinkcraft.shared.model.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockFactory {

    Map<String, StateKey> states = new HashMap<>();
    protected StateAllocator stateAllocator = new StateAllocator(states);
    boolean renderable = true;
    boolean solid = true;
    boolean collidable = true;
    boolean transparent = false;
    String texture;
    Model model;
    boolean smoothLighting = true;
    boolean allowSelf;
    protected IMapViewer mapViewer;

    /**
     * Creates a block factory
     */
    public BlockFactory(IMapViewer mapViewer) {
        this.mapViewer = mapViewer;
    }

    /**
     * Returns all possible versions of the blocks from this factory
     *
     * @return All possible blocks
     */
    public Block[] getBlocks() {
        if (states.size() > 0) {
            List<StateMap> stateList = new ArrayList<>();
            stateList.add(new StateMap());
            for (Map.Entry<String, StateKey> state : states.entrySet()) {
                List<StateMap> newStateList = new ArrayList<>();
                for (Object blockState : state.getValue().getState().getStates()) {
                    for (StateMap stateMap : stateList) {
                        StateMap newStateMap = new StateMap(stateMap);
                        newStateMap.set(state.getValue(), blockState);
                        newStateList.add(newStateMap);
                    }
                }
                stateList = newStateList;
            }
            ArrayList<Block> blocks = new ArrayList<>();
            for (StateMap stateMap : stateList) {
                blocks.add(createBlock(stateMap));
            }
            return blocks.toArray(new Block[blocks.size()]);
        } else {
            return new Block[]{createBlock(new StateMap())};
        }
    }

    /**
     * Creates a block with the passed states
     *
     * @param states
     *         The block state
     * @return The created block
     */
    protected Block createBlock(StateMap states) {
        return new Block(this, states);
    }

    public Texture getTexture() {
        return texture == null ? null : mapViewer.getBlockTexture(texture);
    }
}
