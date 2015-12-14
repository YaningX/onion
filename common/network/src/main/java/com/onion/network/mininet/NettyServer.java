package com.onion.network.mininet;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by gkq on 15-12-6.
 */
public class NettyServer {
    private int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    private ChannelFuture future;

    public  NettyServer(int port) {
        this.port = port;
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();
    }

    public void start() throws InterruptedException {
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChildChannelHandler());

        future = serverBootstrap.bind(port).sync();
        future.channel().closeFuture().sync();
    }

    public synchronized void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        future.removeListeners().channel().close().awaitUninterruptibly();
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new SimpleServerHandler());
            }
        }

    public static void main(String[] strings) {
        NettyServer server = new NettyServer(80);
        try {
            server.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}
