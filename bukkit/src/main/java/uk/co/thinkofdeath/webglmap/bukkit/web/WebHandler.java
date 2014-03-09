package uk.co.thinkofdeath.webglmap.bukkit.web;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import uk.co.thinkofdeath.webglmap.bukkit.WebglMapPlugin;

public class WebHandler extends Thread {

    private WebglMapPlugin plugin;

    public WebHandler(WebglMapPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).
                    channel(NioServerSocketChannel.class).
                    childHandler(new ServerHandler(plugin));

            Channel channel = bootstrap.bind(
                    plugin.getConfig().getString("webserver.bind-address"),
                    plugin.getConfig().getInt("webserver.port")
            ).sync().channel();

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            interrupt();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
