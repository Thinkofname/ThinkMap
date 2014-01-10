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
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
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
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@RequiredArgsConstructor
public class WebSocketHandler extends SimpleChannelInboundHandler<Object>
{

    private final static Logger logger = Logger.getLogger( WebSocketHandler.class.getName() );
    private final static HashMap<String, String> mimeTypes = new HashMap<String, String>();

    static
    {
        mimeTypes.put( "html", "text/html" );
        mimeTypes.put( "js", "application/javascript" );
        mimeTypes.put( "css", "text/css" );
    }

    private static final String WEBSOCKET_PATH = "/server";

    private final WebglMapPlugin plugin;

    private WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0( ChannelHandlerContext context, Object msg ) throws Exception
    {
        if ( msg instanceof FullHttpRequest )
        {
            httpRequest( context, (FullHttpRequest) msg );
        } else if ( msg instanceof WebSocketFrame )
        {
            websocketMessage( context, (WebSocketFrame) msg );
        }
    }

    private void websocketMessage( ChannelHandlerContext context, WebSocketFrame frame )
    {
        if ( frame instanceof CloseWebSocketFrame )
        {
            handshaker.close( context.channel(), (CloseWebSocketFrame) frame.retain() );
            return;
        }
        if ( frame instanceof PingWebSocketFrame )
        {
            context.channel().write( new PongWebSocketFrame( frame.content().retain() ) );
            return;
        }

        if ( !( frame instanceof BinaryWebSocketFrame ) )
        {
            throw new UnsupportedOperationException( String.format( "%s frame types not supported", frame.getClass()
                    .getName() ) );
        }

        BinaryWebSocketFrame bin = (BinaryWebSocketFrame) frame;

    }

    public void httpRequest( ChannelHandlerContext context, FullHttpRequest request ) throws IOException
    {
        if ( !request.getDecoderResult().isSuccess() )
        {
            sendHttpResponse( context, request, new DefaultFullHttpResponse( HTTP_1_1, BAD_REQUEST ) );
            return;
        }

        if ( request.getMethod() != GET )
        {
            sendHttpResponse( context, request, new DefaultFullHttpResponse( HTTP_1_1, FORBIDDEN ) );
            return;
        }

        if ( request.getUri().equals( WEBSOCKET_PATH ) )
        {
            WebSocketServerHandshakerFactory webSocketServerHandshakerFactory = new WebSocketServerHandshakerFactory(
                    getWebSocketLocation( request ),
                    null, false
            );
            handshaker = webSocketServerHandshakerFactory.newHandshaker( request );
            if ( handshaker == null )
            {
                WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse( context.channel() );
            } else
            {
                handshaker.handshake( context.channel(), request );
            }
            return;
        }

        if ( request.getUri().equals( "/" ) )
        {
            request.setUri( "/index.html" );
        }

        InputStream stream = this.getClass().getClassLoader().getResourceAsStream( "www" + request.getUri() );
        if ( stream == null )
        {
            logger.info( "404 - www" + request.getUri() );
            sendHttpResponse( context, request, new DefaultFullHttpResponse( HTTP_1_1, NOT_FOUND ) );
            return;
        }
        ByteBufOutputStream out = new ByteBufOutputStream( Unpooled.buffer() );
        IOUtils.copy( stream, out );
        stream.close();
        out.close();
        FullHttpResponse response = new DefaultFullHttpResponse( HTTP_1_1, OK, out.buffer() );

        String ext = request.getUri().substring( request.getUri().lastIndexOf( '.' ) + 1 );
        String type = mimeTypes.containsKey( ext ) ? mimeTypes.get( ext ) : "text/plain";
        if ( type.startsWith( "text/" ) )
        {
            type += "; charset=UTF-8";
        }
        response.headers().set( CONTENT_TYPE, type );
        setContentLength( response, response.content().readableBytes() );
        sendHttpResponse( context, request, response );

    }

    public void sendHttpResponse( ChannelHandlerContext context, FullHttpRequest request, FullHttpResponse response )
    {
        if ( response.getStatus().code() != 200 )
        {
            ByteBuf buf = Unpooled.copiedBuffer( response.getStatus().toString(), CharsetUtil.UTF_8 );
            response.content().writeBytes( buf );
            buf.release();
            setContentLength( response, response.content().readableBytes() );
        }

        ChannelFuture future = context.channel().writeAndFlush( response );
        if ( !isKeepAlive( request ) || response.getStatus().code() != 200 )
        {
            future.addListener( ChannelFutureListener.CLOSE );
        }
    }

    private String getWebSocketLocation( FullHttpRequest req )
    {
        return "ws://" + req.headers().get( HOST ) + WEBSOCKET_PATH;
    }
}
