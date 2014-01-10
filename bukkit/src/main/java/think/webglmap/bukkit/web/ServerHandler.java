package think.webglmap.bukkit.web;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.RequiredArgsConstructor;
import think.webglmap.bukkit.WebglMapPlugin;

@RequiredArgsConstructor
public class ServerHandler extends ChannelInitializer<SocketChannel>
{

    private final WebglMapPlugin plugin;

    @Override
    protected void initChannel( SocketChannel socketChannel ) throws Exception
    {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast( "codec-http", new HttpServerCodec() );
        pipeline.addLast( "aggregator", new HttpObjectAggregator( 65536 ) );
        pipeline.addLast( "handler", new WebSocketHandler( plugin ) );
    }
}
