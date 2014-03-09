package uk.co.thinkofdeath.thinkmap.bukkit.web;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import uk.co.thinkofdeath.thinkmap.bukkit.ThinkMapPlugin;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class WebSocketHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {

    private final static Logger logger = Logger.getLogger(WebSocketHandler.class.getName());

    private final ThinkMapPlugin plugin;
    private final int id;

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {
        ByteBuf data = msg.content();
        switch (data.readUnsignedByte()) {
            case 0: // Start
                plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Location spawn = plugin.targetWorld.getSpawnLocation();
                        ctx.writeAndFlush(new BinaryWebSocketFrame(
                                Packets.writeSpawnPosition(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ())
                        ));
                        ctx.writeAndFlush(new BinaryWebSocketFrame(
                                Packets.writeTimeUpdate((int) plugin.targetWorld.getTime())
                        ));
                    }
                });
                break;
        }
    }
}
