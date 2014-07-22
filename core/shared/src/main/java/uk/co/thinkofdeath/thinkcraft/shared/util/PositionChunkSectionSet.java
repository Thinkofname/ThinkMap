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

package uk.co.thinkofdeath.thinkcraft.shared.util;

public class PositionChunkSectionSet {

    private final PositionMap<boolean[]> map = new PositionMap<>();

    public PositionChunkSectionSet() {

    }

    public void add(int x, int y, int z) {
        boolean[] vals = map.get(x, z);
        if (vals == null) {
            vals = new boolean[16];
            map.put(x, z, vals);
        }
        vals[y] = true;
    }

    public boolean contains(int x, int y, int z) {
        boolean[] vals = map.get(x, z);
        return vals != null && vals[y];
    }
}
