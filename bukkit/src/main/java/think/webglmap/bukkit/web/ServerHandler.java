package think.webglmap.bukkit.web;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.RequiredArgsConstructor;
import think.webglmap.bukkit.WebglMapPlugin;

@RequiredArgsConstructor
public class ServerHandler extends ChannelInitializer<SocketChannel> {

    private final WebglMapPlugin plugin;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        int id = plugin.lastConnectionId.getAndIncrement();
        plugin.activeConnections.put(id, socketChannel);
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("codec-http", new HttpServerCodec());
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("deflater", new HttpContentCompressor());
        pipeline.addLast("handler", new HTTPHandler(plugin, id));
        pipeline.addLast("websocket", new WebSocketServerProtocolHandler("/server"));
        pipeline.addLast("websocket-handler", new WebSocketHandler(plugin, id));
    }
}
