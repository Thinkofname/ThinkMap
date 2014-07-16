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
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import uk.co.thinkofdeath.thinkcraft.protocol.Packet;
import uk.co.thinkofdeath.thinkcraft.protocol.Packets;
import uk.co.thinkofdeath.thinkcraft.protocol.ServerPacketHandler;

import java.util.List;

public class PacketEncoder extends MessageToMessageEncoder<Packet<ServerPacketHandler>> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet<ServerPacketHandler> msg, List<Object> out) throws Exception {
        ByteBuf buf = ctx.alloc().buffer();
        buf.writeByte(Packets.getServerPacketId(msg));
        msg.write(new ByteBufPacketStream(buf));
        out.add(new BinaryWebSocketFrame(buf));
    }
}
