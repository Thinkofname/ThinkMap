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

package uk.co.thinkofdeath.thinkmap.bukkit.web;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import uk.co.thinkofdeath.thinkmap.bukkit.ThinkMapPlugin;

import java.util.logging.Logger;

public class WebSocketHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {

    private final static Logger logger = Logger.getLogger(WebSocketHandler.class.getName());

    private final ThinkMapPlugin plugin;
    private final int id;

    public WebSocketHandler(ThinkMapPlugin plugin, int id) {
        super(false);
        this.plugin = plugin;
        this.id = id;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {
        ByteBuf data = msg.content();
        switch (data.readUnsignedByte()) {
            case 0: // Start
                plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Location spawn = plugin.getTargetWorld().getSpawnLocation();
                        ctx.writeAndFlush(new BinaryWebSocketFrame(
                                Packets.writeSpawnPosition(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ())
                        ));
                        ctx.writeAndFlush(new BinaryWebSocketFrame(
                                Packets.writeTimeUpdate((int) plugin.getTargetWorld().getTime())
                        ));
                    }
                });
                break;
        }
        msg.release();
    }
}
