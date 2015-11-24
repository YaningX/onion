/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.onion.worker;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * A netty data server. Each worker is a data server, master sends request to data server to read or write data.
 */
public class DataServer {
    private InetSocketAddress tcpAddress;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap bootstrap;
    private ChannelFuture channelFuture;
    private static final Logger LOG = LoggerFactory.getLogger(DataServer.class);

    public DataServer(InetSocketAddress tcpAddress) {
        this.tcpAddress = tcpAddress;
        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
    }

    public void start() throws InterruptedException {
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new DataServerDecoder());
                        p.addLast(new DefaultEventExecutorGroup(10), //TODO: to configure.
                                "KDC_HANDLER",
                                new DataServerHandler());
                    }
                });
        channelFuture = bootstrap.bind(tcpAddress.getPort()).sync();
    }

    static class DataServerDecoder extends LengthFieldBasedFrameDecoder {
        public DataServerDecoder() {
            super(1024 * 1024, 0, 4, 0, 4, true);
        }
    }

    public synchronized void stop() {
        channelFuture.removeListeners().channel().close().awaitUninterruptibly();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public static void main(String[] strings) throws InterruptedException {
        DataServer server = new DataServer(new InetSocketAddress(10000));
        server.start();
    }
}
