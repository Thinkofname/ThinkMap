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

package uk.co.thinkofdeath.thinkcraft.html.client.network;

import elemental.html.ArrayBuffer;
import uk.co.thinkofdeath.thinkcraft.html.shared.buffer.JavascriptViewBuffer;
import uk.co.thinkofdeath.thinkcraft.protocol.PacketStream;
import uk.co.thinkofdeath.thinkcraft.shared.building.DynamicBuffer;
import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.ViewBuffer;

public class DataPacketStream implements PacketStream {

    private ArrayBuffer data;
    private ViewBuffer reader;
    private int readerOffset = 0;

    private DynamicBuffer buffer;

    public DataPacketStream(ArrayBuffer data) {
        this.data = data;
        reader = JavascriptViewBuffer.create(data, false, 0, data.getByteLength());
    }

    public DataPacketStream() {
        buffer = new DynamicBuffer(16);
    }

    @Override
    public void writeString(String str) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public String readString() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void writeInt(int i) {
        buffer.addInt(i);
    }

    @Override
    public int readInt() {
        int o = readerOffset;
        readerOffset += 4;
        return reader.getInt32(o);
    }

    @Override
    public void writeUByte(int b) {
        buffer.add(b);
    }

    @Override
    public int readUByte() {
        return reader.getUInt8(readerOffset++);
    }

    @Override
    public void writeByte(byte b) {
        buffer.add(b & 0xFF);
    }

    @Override
    public byte readByte() {
        return (byte) reader.getInt8(readerOffset++);
    }

    @Override
    public void writeBoolean(boolean b) {
        buffer.add(b ? 1 : 0);
    }

    @Override
    public boolean readBoolean() {
        return readUByte() != 0;
    }

    public DynamicBuffer getBuffer() {
        return buffer;
    }
}
