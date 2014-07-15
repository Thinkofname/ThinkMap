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

package uk.co.thinkofdeath.thinkcraft.shared.block.states;

import uk.co.thinkofdeath.thinkcraft.shared.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class EnumState<T extends Enum<T>> implements BlockState<T> {

    private final T[] states;
    private Class<T> e;

    /**
     * A creates an Enum factory for the given enum
     */
    public EnumState(Class<T> e) {
        this(e, null);
    }

    public EnumState(Class<T> e, Test<T> predicate) {
        this.e = e;
        if (predicate == null) {
            states = e.getEnumConstants();
        } else {
            ArrayList<T> consts = new ArrayList<>();
            T[] vals = e.getEnumConstants();
            for (T v : vals) {
                if (predicate.test(v)) {
                    consts.add(v);
                }
            }
            states = consts.toArray(Arrays.copyOf(vals, consts.size()));
        }
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
