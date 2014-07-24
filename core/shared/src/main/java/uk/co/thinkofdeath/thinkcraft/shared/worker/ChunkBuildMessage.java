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

package uk.co.thinkofdeath.thinkcraft.shared.worker;

import uk.co.thinkofdeath.thinkcraft.shared.serializing.ReadSerializer;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.WriteSerializer;

public class ChunkBuildMessage extends WorkerMessage {

    private int x;
    private int z;
    private int sectionNumber;
    private int buildNumber;

    ChunkBuildMessage() {
    }

    /**
     * Creates a new chunk unload message
     *
     * @param x
     *         The x position of the chunk
     * @param z
     *         The z position of the chunk
     * @param sectionNumber
     *         The section number
     * @param buildNumber
     *         The build number
     */
    public ChunkBuildMessage(int x, int z, int sectionNumber, int buildNumber) {
        this.x = x;
        this.z = z;
        this.sectionNumber = sectionNumber;
        this.buildNumber = buildNumber;
    }

    /**
     * Returns the x position of the chunk
     *
     * @return The x position
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the z position of the chunk
     *
     * @return The z position
     */
    public int getZ() {
        return z;
    }

    /**
     * Returns the section number of the chunk
     *
     * @return The section number
     */
    public int getSectionNumber() {
        return sectionNumber;
    }

    /**
     * Returns this build's build number
     *
     * @return The build number
     */
    public int getBuildNumber() {
        return buildNumber;
    }

    @Override
    public void serialize(WriteSerializer serializer) {
        super.serialize(serializer);
        serializer.putInt("x", x);
        serializer.putInt("z", z);
        serializer.putInt("sectionNumber", sectionNumber);
        serializer.putInt("buildNumber", buildNumber);
    }

    @Override
    protected void read(ReadSerializer serializer) {
        x = serializer.getInt("x");
        z = serializer.getInt("z");
        sectionNumber = serializer.getInt("sectionNumber");
        buildNumber = serializer.getInt("buildNumber");
    }

    @Override
    protected WorkerMessage create() {
        return new ChunkBuildMessage();
    }

    @Override
    public void handle(MessageHandler handler) {
        handler.handle(this);
    }
}
