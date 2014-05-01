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

package uk.co.thinkofdeath.mapviewer.shared.block;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StateMap {

    private final HashMap<String, Object> state = new HashMap<>();

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
        state.putAll(stateMap.state);
    }

    /**
     * Changes the value of a state in the map
     *
     * @param key
     *         The state name
     * @param value
     *         The new value
     */
    public void set(String key, Object value) {
        state.put(key, value);
    }

    /**
     * Returns the state for the given key
     *
     * @param key
     *         The state's key
     * @return The state's value or null
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) state.get(key);
    }

    /**
     * Returns the number of states in the map
     *
     * @return The number of states
     */
    public int size() {
        return state.size();
    }

    /**
     * Returns a set of the all the states in this map
     *
     * @return A set of all states
     */
    public Set<Map.Entry<String, Object>> entrySet() {
        return state.entrySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StateMap stateMap = (StateMap) o;

        return state.equals(stateMap.state);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return state.hashCode();
    }
}
