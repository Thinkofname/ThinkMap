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

import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.UByteBuffer;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.Serializer;

public class ChunkLoadMessage extends WorkerMessage {

    private int x;
    private int z;
    private UByteBuffer data;

    ChunkLoadMessage() {
    }

    /**
     * Creates a new chunk load message
     *
     * @param x
     *         The x position of the chunk
     * @param z
     *         The z position of the chunk
     * @param data
     *         The data of the chunk
     */
    public ChunkLoadMessage(int x, int z, UByteBuffer data) {
        this.x = x;
        this.z = z;
        this.data = data;
    }

    /**
     * Returns the x position of the requested chunk
     *
     * @return The x position
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the z position of the requested chunk
     *
     * @return The z position
     */
    public int getZ() {
        return z;
    }

    /**
     * Returns the data needed to load this chunk
     *
     * @return The data
     */
    public UByteBuffer getData() {
        return data;
    }

    @Override
    public void serialize(Serializer serializer) {
        super.serialize(serializer);
        serializer.putInt("x", x);
        serializer.putInt("z", z);
        serializer.putBuffer("data", data);
    }

    @Override
    public void deserialize(Serializer serializer) {
        super.deserialize(serializer);
        x = serializer.getInt("x");
        z = serializer.getInt("z");
        data = (UByteBuffer) serializer.getBuffer("data");
    }

    @Override
    public ChunkLoadMessage create() {
        return new ChunkLoadMessage();
    }

    @Override
    public void handle(MessageHandler handler) {
        handler.handle(this);
    }
}
