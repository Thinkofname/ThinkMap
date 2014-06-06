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

import uk.co.thinkofdeath.mapviewer.shared.bit.BitAllocator;
import uk.co.thinkofdeath.mapviewer.shared.bit.BitKey;

import java.util.Map;

public class StateAllocator {

    private final BitAllocator bitAllocator = new BitAllocator();
    private final Map<String, StateKey> states;

    public StateAllocator(Map<String, StateKey> states) {
        this.states = states;
    }

    public <T> StateKey<T> alloc(String name, BlockState<T> state) {
        T[] values = state.getStates();
        BitKey bitKey = bitAllocator.alloc(values.length);
        StateKey<T> key = new StateKey<>(name, bitKey, state);
        states.put(name, key);
        return key;
    }
}
