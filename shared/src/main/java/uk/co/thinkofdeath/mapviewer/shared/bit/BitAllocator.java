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

public class BitAllocator {

    private int allocatedBits = 0;

    public BitKey alloc(int range) {
        return allocKey(nearestPowerOfTwo(range));
    }

    private BitKey allocKey(int requiredValues) {
        int requiredBits = (int) (Math.log(requiredValues) / Math.log(2));
        if (allocatedBits + requiredBits > 32) {
            throw new RuntimeException("Out of bits");
        }
        int shift = allocatedBits;
        allocatedBits += requiredBits;
        int mask = requiredValues;
        if (mask != 1) {
            mask--;
        }
        return new BitKey(shift, mask << shift);
    }

    // From: http://graphics.stanford.edu/~seander/bithacks.html#RoundUpPowerOf2
    private static int nearestPowerOfTwo(int v) {
        v--;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        v++;
        return v;
    }
}
