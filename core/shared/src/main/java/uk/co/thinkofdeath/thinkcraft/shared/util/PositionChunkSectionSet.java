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

import java.util.Arrays;

public class PositionChunkSectionSet {

    private int size;
    private int resizeThreshold;
    private Entry[] entries;
    private int mask;
    private int maximumDistanceFromIdeal;

    public PositionChunkSectionSet() {
        this(200);
    }

    public PositionChunkSectionSet(int capacity) {
        capacity = MathUtils.nextPowerOfTwo(capacity);
        allocate(capacity);
    }

    private int hash(int x, int z) {
        x = (x | (x << 8)) & 0x00FF00FF;
        x = (x | (x << 4)) & 0x0F0F0F0F;
        x = (x | (x << 2)) & 0x33333333;
        x = (x | (x << 1)) & 0x55555555;
        z = (z | (z << 8)) & 0x00FF00FF;
        z = (z | (z << 4)) & 0x0F0F0F0F;
        z = (z | (z << 2)) & 0x33333333;
        z = (z | (z << 1)) & 0x55555555;
        return (x | (z << 1));
    }

    public void add(int x, int y, int z) {
        Entry entry = addOrGet(x, z);
        int bit = 1 << y;
        entry.value &= ~bit;
        entry.value |= bit;
    }

    public boolean contains(int x, int y, int z) {
        Entry entry = lookup(x, z);
        return entry != null && (entry.value & (1 << y)) != 0;
    }

    public void remove(int x, int y, int z) {
        Entry entry = lookup(x, z);
        entry.value &= ~(1 << y);
        if (entry.value == 0) {
            take(x, z);
        }
    }

    public int size() {
        return size;
    }

    private void allocate(int capacity) {
        entries = new Entry[capacity];
        mask = capacity - 1;
        resizeThreshold = capacity >> 1;
        maximumDistanceFromIdeal = 0;
    }

    private void resize() {
        Entry[] oldEntries = entries;
        allocate(entries.length << 1);
        for (Entry entry : oldEntries) {
            if (entry != null) {
                insert(entry);
            }
        }
    }

    private Entry addOrGet(int x, int z) {
        int index = hash(x, z) & mask;

        int distance = 0;
        Entry e = null;
        while (true) {
            maximumDistanceFromIdeal = Math.max(distance, maximumDistanceFromIdeal);
            Entry entry = entries[index];

            if (entry == null) {
                if (e == null) {
                    e = new Entry(x, z);
                    size++;
                    if (size >= resizeThreshold) {
                        resize();
                    }
                }
                entry = entries[index] = e;
                entry.distance = distance;
                return entry;
            } else if (entry.x == x && entry.z == z) {
                return entry;
            } else if (distance > entry.distance) {
                if (e == null) {
                    e = new Entry(x, z);
                    size++;
                    if (size >= resizeThreshold) {
                        resize();
                    }
                }
                Entry temp = entries[index] = e;
                temp.distance = distance;
                e = entry;
                x = e.x;
                z = e.z;
                distance = e.distance;
            }
            distance++;
            index = (index + 1) & mask;
        }
    }

    private Entry insert(Entry e) {
        int index = hash(e.x, e.z) & mask;

        while (true) {
            maximumDistanceFromIdeal = Math.max(e.distance, maximumDistanceFromIdeal);
            Entry entry = entries[index];

            if (entry == null) {
                entries[index] = e;
                return null;
            } else if (entry.x == e.x && entry.z == e.z) {
                entries[index] = e;
                return entry;
            } else if (e.distance > entry.distance) {
                entries[index] = e;
                e = entry;
            }
            e.distance++;
            index = (index + 1) & mask;
        }
    }

    private Entry lookup(int x, int z) {
        int index = hash(x, z) & mask;
        int distance = 0;

        while (true) {
            Entry entry = entries[index];
            if (entry == null) {
                return null;
            } else if (distance > maximumDistanceFromIdeal) {
                return null;
            } else if (entry.x == x && entry.z == z) {
                return entry;
            }

            distance++;
            index = (index + 1) & mask;
        }
    }

    private Entry take(int x, int z) {
        int index = hash(x, z) & mask;

        while (true) {
            Entry entry = entries[index];
            if (entry == null) {
                return null;
            } else if (entry.x == x && entry.z == z) {
                int indexPrevious = index;
                int indexSwap = (index + 1) & mask;
                while (true) {
                    if (entries[indexSwap] == null || entries[indexSwap].distance == 0) {
                        entries[indexPrevious] = null;
                        return entry;
                    }
                    entries[indexPrevious] = entries[indexSwap];
                    entries[indexPrevious].distance--;
                    indexPrevious = indexSwap;
                    indexSwap = (indexSwap + 1) & mask;
                }
            }
            index = (index + 1) & mask;
        }
    }

    public void clear() {
        Arrays.fill(entries, null);
        maximumDistanceFromIdeal = 0;
        size = 0;
    }

    private static class Entry {
        private int x;
        private int z;
        private int value;
        private int distance = 0;

        private Entry(int x, int z) {
            this.x = x;
            this.z = z;
        }
    }
}
