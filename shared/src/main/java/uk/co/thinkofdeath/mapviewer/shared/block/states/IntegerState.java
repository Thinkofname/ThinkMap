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

import uk.co.thinkofdeath.mapviewer.shared.block.BlockState;

public class IntegerState implements BlockState {

    private final int min;
    private final int max;

    /**
     * A creates a integer factory for the given range of values
     *
     * @param min
     *         The minimum value
     * @param max
     *         The maximum value
     */
    public IntegerState(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public Object[] getStates() {
        Object[] states = new Object[max - min];
        for (int i = min; i <= max; i++) {
            states[i - min] = i;
        }
        return states;
    }
}
