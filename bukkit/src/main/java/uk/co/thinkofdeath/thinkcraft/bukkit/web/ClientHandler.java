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

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.bukkit.Location;
import uk.co.thinkofdeath.thinkcraft.bukkit.ThinkMapPlugin;
import uk.co.thinkofdeath.thinkcraft.protocol.ClientPacketHandler;
import uk.co.thinkofdeath.thinkcraft.protocol.Packet;
import uk.co.thinkofdeath.thinkcraft.protocol.packets.InitConnection;
import uk.co.thinkofdeath.thinkcraft.protocol.packets.ServerSettings;
import uk.co.thinkofdeath.thinkcraft.protocol.packets.SpawnPosition;
import uk.co.thinkofdeath.thinkcraft.protocol.packets.TimeUpdate;

public class ClientHandler extends SimpleChannelInboundHandler<Packet<ClientPacketHandler>> implements ClientPacketHandler {

    private final Channel channel;
    private final ThinkMapPlugin plugin;

    public ClientHandler(Channel channel, ThinkMapPlugin plugin) {
        this.channel = channel;
        this.plugin = plugin;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Packet<ClientPacketHandler> msg) throws Exception {
        msg.handle(this);
    }

    @Override
    public void handle(InitConnection initConnection) {
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                channel.write(new ServerSettings(
                        plugin.getConfiguration().shouldHideOres()
                ));
                Location spawn = plugin.getTargetWorld().getSpawnLocation();
                channel.write(new SpawnPosition(
                        spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()
                ));
                channel.writeAndFlush(new TimeUpdate((int) plugin.getTargetWorld().getTime()));
            }
        });
    }
}
