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

import uk.co.thinkofdeath.thinkcraft.shared.block.Block;
import uk.co.thinkofdeath.thinkcraft.shared.platform.Platform;
import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.UByteBuffer;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.IntArraySerializer;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.Serializer;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.SerializerArraySerializer;

import java.util.ArrayList;
import java.util.List;

public class ChunkLoadedMessage extends WorkerMessage {

    private int x;
    private int z;
    private Section[] sections = new Section[16];
    private List<BlockMapping> mappingList = new ArrayList<>();
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
    }

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
    public void setSection(int i, int count, UByteBuffer buffer) {
        sections[i] = new Section(count, buffer);
    }

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

    public Section[] getSections() {
        return sections;
    }

    /**
     * Adds an id -> block mapping to the message
     *
     * @param key
     *         The id
     * @param value
     *         The block
     */
    public void addIdBlockMapping(int key, Block value) {
        mappingList.add(new BlockMapping(key, value.getFullName(), value.getRawState()));
    }

    public List<BlockMapping> getMappingList() {
        return mappingList;
    }

    @Override
    public void serialize(Serializer serializer) {
        super.serialize(serializer);
        serializer.putInt("x", x);
        serializer.putInt("z", z);
        serializer.putInt("nextId", nextId);

        SerializerArraySerializer ms = Platform.workerSerializers().createSerializerArray();
        for (BlockMapping mapping : mappingList) {
            Serializer ss = Platform.workerSerializers().create();
            ss.putInt("id", mapping.getId());
            ss.putString("name", mapping.getFullName());
            ss.putInt("state", mapping.getRawState());
            ms.add(ss);
        }
        serializer.putArray("idMap", ms);

        SerializerArraySerializer arraySerializer = Platform.workerSerializers().createSerializerArray();
        for (Section section : sections) {
            if (section == null) {
                arraySerializer.add(null);
                continue;
            }
            Serializer ss = Platform.workerSerializers().create();
            ss.putInt("count", section.getCount());
            ss.putBuffer("buffer", section.getBuffer());
            arraySerializer.add(ss);
        }
        serializer.putArray("sections", arraySerializer);

        IntArraySerializer b = Platform.workerSerializers().createIntArray();
        for (int i = 0; i < 256; i++) {
            b.add(biomes[i]);
        }
        serializer.putArray("biomes", b);
    }

    @Override
    public void deserialize(Serializer serializer) {
        super.deserialize(serializer);
        x = serializer.getInt("x");
        z = serializer.getInt("z");
        nextId = serializer.getInt("nextId");

        SerializerArraySerializer ms = (SerializerArraySerializer) serializer.getArray("idMap");
        for (int i = 0; i < ms.size(); i++) {
            Serializer ss = ms.get(i);
            mappingList.add(new BlockMapping(
                    ss.getInt("id"),
                    ss.getString("name"),
                    ss.getInt("state")
            ));
        }

        SerializerArraySerializer arraySerializer = (SerializerArraySerializer) serializer.getArray("sections");
        for (int i = 0; i < 16; i++) {
            Serializer ss = arraySerializer.get(i);
            if (ss == null) continue;
            sections[i] = new Section(ss.getInt("count"), (UByteBuffer) ss.getBuffer("buffer"));
        }

        IntArraySerializer b = (IntArraySerializer) serializer.getArray("biomes");
        for (int i = 0; i < 256; i++) {
            biomes[i] = b.getInt(i);
        }
    }

    @Override
    public ChunkLoadedMessage create() {
        return new ChunkLoadedMessage();
    }

    @Override
    public void handle(MessageHandler handler) {
        handler.handle(this);
    }

    public static class Section {
        private final int count;
        private final UByteBuffer buffer;

        public Section(int count, UByteBuffer buffer) {
            this.count = count;
            this.buffer = buffer;
        }

        public int getCount() {
            return count;
        }

        public UByteBuffer getBuffer() {
            return buffer;
        }
    }

    public static class BlockMapping {
        private final int id;
        private final String fullName;
        private final int rawState;

        private BlockMapping(int id, String fullName, int rawState) {
            this.id = id;
            this.fullName = fullName;
            this.rawState = rawState;
        }

        public int getId() {
            return id;
        }

        public String getFullName() {
            return fullName;
        }

        public int getRawState() {
            return rawState;
        }
    }
}
