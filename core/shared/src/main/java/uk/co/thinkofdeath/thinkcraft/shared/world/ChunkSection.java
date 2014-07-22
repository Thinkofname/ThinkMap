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

package uk.co.thinkofdeath.thinkcraft.shared.world;

import uk.co.thinkofdeath.thinkcraft.shared.Face;
import uk.co.thinkofdeath.thinkcraft.shared.support.TUint16Array;
import uk.co.thinkofdeath.thinkcraft.shared.support.TUint8Array;

public class ChunkSection {

    private static final TUint8Array emptySkySection = TUint8Array.create(16 * 16 * 16);

    static {
        for (int i = 0; i < emptySkySection.length(); i++) {
            emptySkySection.set(i, 15);
        }
    }

    private TUint16Array blocks;
    private TUint8Array light;
    private TUint8Array sky;
    private TUint8Array buffer;
    private int[] sideAccess = new int[Face.values().length];

    // Number of non-zero things in this chunk
    private int count = 0;

    /**
     * Create an empty section
     */
    public ChunkSection() {
        this(TUint8Array.create(16 * 16 * 16 * 4));
        sky.set(emptySkySection);
    }

    /**
     * Creates a section from the passed buffer
     *
     * @param buffer
     *         The buffer to create from
     */
    public ChunkSection(TUint8Array buffer) {
        this.buffer = buffer;
        blocks = TUint16Array.create(buffer.getBuffer(), 0, 16 * 16 * 16);
        light = TUint8Array.create(buffer.getBuffer(), 16 * 16 * 16 * 2, 16 * 16 * 16);
        sky = TUint8Array.create(buffer.getBuffer(), 16 * 16 * 16 * 3, 16 * 16 * 16);
    }

    /**
     * Returns the block array for this section
     *
     * @return The block array
     */
    public TUint16Array getBlocks() {
        return blocks;
    }

    /**
     * Returns the light array for this section
     *
     * @return The light array
     */
    public TUint8Array getBlockLight() {
        return light;
    }

    /**
     * Returns the sky light array for this section
     *
     * @return The sky light array
     */
    public TUint8Array getSkyLight() {
        return sky;
    }

    /**
     * Returns the buffer for this section
     *
     * @return The buffer
     */
    public TUint8Array getBuffer() {
        return buffer;
    }

    /**
     * Increases the section's internal non-zero item counter
     */
    public void increaseCount() {
        count++;
    }

    /**
     * Returns the number of non-zero items in this section
     *
     * @return Number of non-zero items
     */
    public int getCount() {
        return count;
    }

    public void setSideAccess(Face face, Face other, boolean canAccess) {
        int bit = 1 << other.ordinal();
        sideAccess[face.ordinal()] &= ~bit;
        sideAccess[face.ordinal()] |= canAccess ? bit : 0;
    }

    public boolean canAccessSide(Face face, Face other) {
        int bit = 1 << other.ordinal();
        return (sideAccess[face.ordinal()] & bit) != 0;
    }

    /**
     * Internal use
     */
    public int[] getSideAccess() {
        return sideAccess;
    }
}
