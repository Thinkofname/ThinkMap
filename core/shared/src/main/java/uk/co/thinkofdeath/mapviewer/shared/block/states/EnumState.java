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

public class EnumState<T extends Enum<T>> implements BlockState<T> {

    private final T[] states;
    private Class<T> e;

    /**
     * A creates an Enum factory for the given enum
     */
    public EnumState(Class<T> e) {
        this.e = e;
        states = e.getEnumConstants();
    }

    @Override
    public T[] getStates() {
        return states;
    }

    @Override
    public int indexOf(T value) {
        for (int i = 0; i < states.length; i++) {
            if (states[i] == value) {
                return i;
            }
        }
        return -1;
    }
}
