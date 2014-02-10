package think.webglmap.bukkit.web;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import think.webglmap.bukkit.WebglMapPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Logger;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.HOST;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@RequiredArgsConstructor
public class WebSocketHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {

    private final static Logger logger = Logger.getLogger(WebSocketHandler.class.getName());

    private final WebglMapPlugin plugin;
    private final int id;

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {
        ByteBuf data = msg.content();
        switch (data.readUnsignedByte()) {
            case 0: // Start
                plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        ByteBuf buf = Unpooled.buffer();
                        buf.writeByte(1);
                        Location spawn = plugin.targetWorld.getSpawnLocation();
                        buf.writeInt(spawn.getBlockX());
                        buf.writeByte(spawn.getBlockY());
                        buf.writeInt(spawn.getBlockZ());
                        ctx.writeAndFlush(new BinaryWebSocketFrame(buf));
                    }
                });
                break;
        }
    }
}
