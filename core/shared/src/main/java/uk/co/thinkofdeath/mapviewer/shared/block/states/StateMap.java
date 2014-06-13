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

package uk.co.thinkofdeath.mapviewer.shared.block.states;

import uk.co.thinkofdeath.mapviewer.shared.bit.BitField;

public class StateMap {

    private final BitField state = new BitField();

    /**
     * Creates an empty state map
     */
    public StateMap() {

    }

    /**
     * Creates an state map with the same values as the passed map
     *
     * @param stateMap
     *         The map to copy from
     */
    public StateMap(StateMap stateMap) {
        state.copy(stateMap.state);
    }

    /**
     * Changes the value of a state in the map
     *
     * @param key
     *         The state name
     * @param value
     *         The new value
     */
    public <T> void set(StateKey<T> key, T value) {
        int v = key.getState().indexOf(value);
        state.set(key.key, v);
    }

    /**
     * Returns the state for the given key
     *
     * @param key
     *         The state's key
     * @return The state's value or null
     */
    public <T> T get(StateKey<T> key) {
        return key.getState().getStates()[state.get(key.key)];
    }

    public int asInt() {
        return state.asInt();
    }
}
