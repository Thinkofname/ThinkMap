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

import com.google.gwt.core.client.JavaScriptObject;
import uk.co.thinkofdeath.thinkcraft.shared.block.Block;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.ReadSerializer;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.WriteSerializer;
import uk.co.thinkofdeath.thinkcraft.shared.support.TUint8Array;

public class ChunkLoadedMessage extends WorkerMessage {

    private int x;
    private int z;
    private JavaScriptObject nativeVoodoo = JavaScriptObject.createObject();
    private int nextId;
    private int[] biomes = new int[256];

    ChunkLoadedMessage() {
    }

    /**
     * Creates a chunk loaded message
     *
     * @param x
     *         The x position of the loaded chunk
     * @param z
     *         The y position of the loaded chunk
     */
    public ChunkLoadedMessage(int x, int z, int[] biomes) {
        this.x = x;
        this.z = z;
        System.arraycopy(biomes, 0, this.biomes, 0, 256);
        initVoodoo();
    }

    private native void initVoodoo()/*-{
        var that = this.@uk.co.thinkofdeath.thinkcraft.shared.worker.ChunkLoadedMessage::nativeVoodoo;
        that.sections = [];
        that.idmap = [];
    }-*/;

    /**
     * Gets the x position of the loaded chunk
     *
     * @return The x position
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the z position of the loaded chunk
     *
     * @return The z position
     */
    public int getZ() {
        return z;
    }

    /**
     * Sets the section for this message
     *
     * @param i
     *         Section position
     * @param count
     *         Non-zero item count
     * @param buffer
     *         Data buffer
     */
    public final native void setSection(int i, int count, TUint8Array buffer)/*-{
        var that = this.@uk.co.thinkofdeath.thinkcraft.shared.worker.ChunkLoadedMessage::nativeVoodoo;
        that.sections[i] = {
            count: count,
            buffer: buffer
        };
    }-*/;

    /**
     * Sets the next block id for this chunk
     *
     * @param nextId
     *         The next id
     */
    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    public int getNextId() {
        return nextId;
    }

    public int[] getBiomes() {
        return biomes;
    }

    /**
     * Adds an id -> block mapping to the message
     *
     * @param key
     *         The id
     * @param value
     *         The block
     */
    public final native void addIdBlockMapping(int key, Block value)/*-{
        var that = this.@uk.co.thinkofdeath.thinkcraft.shared.worker.ChunkLoadedMessage::nativeVoodoo;
        that.idmap[key] = [
            value.@uk.co.thinkofdeath.thinkcraft.shared.block.Block::fullName,
            value.@uk.co.thinkofdeath.thinkcraft.shared.block.Block::state.@uk.co.thinkofdeath.thinkcraft.shared.block.states.StateMap::asInt()()
        ];
    }-*/;

    @Override
    public void serialize(WriteSerializer serializer) {
        super.serialize(serializer);
        serializer.putInt("x", x);
        serializer.putInt("z", z);
        serializer.putInt("nextId", nextId);
        serializer.putTemp("nativeVoodoo", nativeVoodoo);
        // FixME
        for (int i = 0; i < 256; i++) {
            serializer.putInt("biome[" + i + "]", biomes[i]);
        }
    }

    @Override
    protected void read(ReadSerializer serializer) {
        x = serializer.getInt("x");
        z = serializer.getInt("z");
        nextId = serializer.getInt("nextId");
        nativeVoodoo = (JavaScriptObject) serializer.getTemp("nativeVoodoo");
        // FixME
        for (int i = 0; i < 256; i++) {
            biomes[i] = serializer.getInt("biome[" + i + "]");
        }
    }

    @Override
    protected WorkerMessage create() {
        return new ChunkLoadedMessage();
    }

    @Override
    public void handle(MessageHandler handler) {
        handler.handle(this);
    }
}
