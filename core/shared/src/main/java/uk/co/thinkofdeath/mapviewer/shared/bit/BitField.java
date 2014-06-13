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

package uk.co.thinkofdeath.mapviewer.shared.bit;

public class BitField {

    private int value = 0;

    public BitField() {

    }

    public void set(BitKey key, int val) {
        value = (value & ~key.mask) | ((val << key.shift) & key.mask);
    }

    public int get(BitKey key) {
        return (value & key.mask) >> key.shift;
    }

    public int asInt() {
        return value;
    }

    public void copy(BitField state) {
        value = state.value;
    }
}
