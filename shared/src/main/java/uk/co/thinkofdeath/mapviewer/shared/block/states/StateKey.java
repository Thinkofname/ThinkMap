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

import uk.co.thinkofdeath.mapviewer.shared.bit.BitKey;

public final class StateKey<T> {

    private final String name;
    final BitKey key;
    private final BlockState<T> state;

    StateKey(String name, BitKey key, BlockState<T> state) {
        this.name = name;
        this.key = key;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public BlockState<T> getState() {
        return state;
    }

    @Override
    public String toString() {
        return "StateKey{" +
                "name='" + name + '\'' +
                ", key=" + key +
                ", state=" + state +
                '}';
    }
}
