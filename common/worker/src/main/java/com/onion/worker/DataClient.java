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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class DataClient {
    private EventLoopGroup group;
    private InetSocketAddress tcpAddress;
    private DataClientHandler dataClientHandler;

    public DataClient(InetSocketAddress tcpAddress) {
        this.tcpAddress = tcpAddress;
        this.dataClientHandler = new DataClientHandler();
        this.group = new NioEventLoopGroup();
    }

    public void connect() throws InterruptedException {
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new DataClientPipelineHandler(dataClientHandler));
        ChannelFuture future = b.connect(tcpAddress);
        future.channel().close().sync();
    }

    public void shutdown() {
        group.shutdownGracefully();
    }

    public static void main(String[] strings) throws InterruptedException {
        InetSocketAddress tcpAddress = new InetSocketAddress("127.0.0.1", 8007);
        DataClient client = new DataClient(tcpAddress);
        client.connect();
    }

}
