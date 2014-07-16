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

package uk.co.thinkofdeath.thinkcraft.bukkit.web;

import io.netty.buffer.ByteBuf;
import uk.co.thinkofdeath.thinkcraft.protocol.PacketStream;

import java.nio.charset.StandardCharsets;

public class ByteBufPacketStream implements PacketStream {
    private final ByteBuf in;

    public ByteBufPacketStream(ByteBuf in) {
        this.in = in;
    }

    @Override
    public void writeString(String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        writeInt(bytes.length);
        in.writeBytes(bytes);
    }

    @Override
    public String readString() {
        byte[] bytes = new byte[readInt()];
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public void writeInt(int i) {
        in.writeInt(i);
    }

    @Override
    public int readInt() {
        return in.readInt();
    }

    @Override
    public void writeUByte(int b) {
        in.writeByte(b);
    }

    @Override
    public int readUByte() {
        return in.readUnsignedByte();
    }

    @Override
    public void writeByte(byte b) {
        in.writeByte(b);
    }

    @Override
    public byte readByte() {
        return in.readByte();
    }

    @Override
    public void writeBoolean(boolean b) {
        in.writeByte(b ? 1 : 0);
    }

    @Override
    public boolean readBoolean() {
        return in.readUnsignedByte() != 0;
    }
}
