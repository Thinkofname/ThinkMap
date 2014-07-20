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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Optimized for keys that are close together
 *
 * @param <T>
 */
public class IntMap<T> implements Iterable<T> {

    private Object[] values;

    public IntMap() {
        this(16);
    }

    public IntMap(int size) {
        values = new Object[MathUtils.nextPowerOfTwo(size)];
    }

    /**
     * Returns a value from the map
     *
     * @param key
     *         The key for the value
     * @return The value or null
     */
    public T get(int key) {
        if (key >= values.length) {
            return null;
        }
        return (T) values[key];
    }

    /**
     * Stores a value into the map
     *
     * @param key
     *         The key for the value
     * @param value
     *         The value to store
     */
    public void put(int key, T value) {
        if (key >= values.length) {
            Object[] old = values;
            values = new Object[MathUtils.nextPowerOfTwo(key)];
            System.arraycopy(old, 0, values, 0, old.length);
        }
        values[key] = value;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index = 0;

            {
                while (index < values.length && values[index] == null) {
                    index++;
                }
            }

            @Override
            public boolean hasNext() {
                return index < values.length;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();

                T val = (T) values[index];
                index++;
                while (index < values.length && values[index] == null) {
                    index++;
                }
                return val;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
        };
    }

    public IntIterator intIterator() {
        return new IntIterator();
    }

    public class IntIterator {
        private int index = 0;

        private IntIterator() {
            while (index < values.length && values[index] == null) {
                index++;
            }
        }

        public boolean hasNext() {
            return index < values.length;
        }

        public int next() {
            if (!hasNext()) throw new NoSuchElementException();

            int i = index;
            index++;
            while (index < values.length && values[index] == null) {
                index++;
            }
            return i;
        }
    }
}
