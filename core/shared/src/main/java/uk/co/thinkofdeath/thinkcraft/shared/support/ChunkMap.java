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

package uk.co.thinkofdeath.thinkcraft.shared.support;

import uk.co.thinkofdeath.thinkcraft.shared.world.Chunk;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ChunkMap<T extends Chunk> implements Iterable<T> {

    private int size;
    private int resizeThreshold;
    private Entry<T>[] entries;
    private int mask;
    private int maximumDistanceFromIdeal;

    public ChunkMap() {
        this(200);
    }

    public ChunkMap(int capacity) {
        capacity = nextPowerOfTwo(capacity);
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

    public T put(int x, int z, T value) {
        Entry<T> replaced = insert(new Entry<>(x, z, value));
        if (replaced != null) {
            return replaced.value;
        }
        size++;
        if (size >= resizeThreshold) {
            resize();
        }
        return null;
    }

    public T remove(int x, int z) {
        Entry<T> removed = take(x, z);
        if (removed != null) {
            size--;
            return removed.value;
        }
        return null;
    }

    public T get(int x, int z) {
        Entry<T> ret = lookup(x, z);
        if (ret != null) {
            return ret.value;
        }
        return null;
    }

    public boolean contains(int x, int z) {
        return get(x, z) != null;
    }

    public int size() {
        return size;
    }

    private static int nextPowerOfTwo(int num) {
        if (num == 0) return 1;
        num--;
        num |= num >> 1;
        num |= num >> 2;
        num |= num >> 4;
        num |= num >> 8;
        return ((num | num >> 16) + 1);
    }

    private void allocate(int capacity) {
        entries = new Entry[capacity];
        mask = capacity - 1;
        resizeThreshold = capacity >> 1;
        maximumDistanceFromIdeal = 0;
    }

    private void resize() {
        Entry<T>[] oldEntries = entries;
        allocate(entries.length << 1);
        for (int i = 0; i < oldEntries.length; i++) {
            Entry<T> entry = oldEntries[i];
            if (entry != null) {
                insert(entry);
            }
        }
    }

    private Entry<T> insert(Entry<T> e) {
        int index = hash(e.x, e.z) & mask;

        while (true) {
            maximumDistanceFromIdeal = Math.max(e.distance, maximumDistanceFromIdeal);
            Entry<T> entry = entries[index];

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

    private Entry<T> lookup(int x, int z) {
        int index = hash(x, z) & mask;
        int distance = 0;

        while (true) {
            Entry<T> entry = entries[index];
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

    private Entry<T> take(int x, int z) {
        int index = hash(x, z) & mask;

        while (true) {
            Entry<T> entry = entries[index];
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

    @Override
    public Iterator<T> iterator() {
        return new ChunkIterator();
    }

    private static class Entry<T> {
        private int x;
        private int z;
        private T value;
        private int distance = 0;

        private Entry(int x, int z, T value) {
            this.x = x;
            this.z = z;
            this.value = value;
        }
    }

    private class ChunkIterator implements Iterator<T> {

        private int index = 0;

        public ChunkIterator() {
            while (index < entries.length && entries[index] == null) {
                index++;
            }
        }

        @Override
        public boolean hasNext() {
            return index < entries.length;
        }

        @Override
        public T next() {
            if (index >= entries.length) {
                throw new NoSuchElementException();
            }
            T val = entries[index].value;
            index++;
            while (index < entries.length && entries[index] == null) {
                index++;
            }
            return val;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}
