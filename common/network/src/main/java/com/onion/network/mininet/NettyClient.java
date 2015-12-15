package com.onion.network.mininet;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by gkq on 15-12-6.
 */
public class NettyClient {
    private ChannelFuture future;
    private Bootstrap bootstrap;
    private EventLoopGroup group;
    private String msg = "this is a test";

    public NettyClient() {

        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();

    }

    public void connect(String host, int port) throws InterruptedException {

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new SimpleClientHandler());
                    }
                });

        future = bootstrap.connect(host, port).sync();
        while(true) {
            future.channel().writeAndFlush("gkq");
            sendData("test by gukunqi");
        }
//        future.channel().closeFuture().sync();

    }

    public void sendData(String msg) {
        Channel ch = future.channel();
        ch.writeAndFlush(msg);
    }

    public synchronized void stop() {
        group.shutdownGracefully();
    }

    public static void main(String[] strings) {
        NettyClient client = new NettyClient();
        try {
            client.connect("localhost", 80);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            client.stop();
        }
    }
}