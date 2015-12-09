/*
 * Licensed to the University of California, Berkeley under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.onion.worker;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import tachyon.Constants;
import tachyon.conf.TachyonConf;
import tachyon.network.ChannelType;
import tachyon.util.network.NettyUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Runs a netty data server that responses to block requests.
 */
public final class WorkerDataServer {
  private final ServerBootstrap mBootstrap;
  private final ChannelFuture mChannelFuture;
  private final TachyonConf mTachyonConf;
  private final WorkerDataServerHandler mWorkerDataServerHandler;


    public WorkerDataServer(final InetSocketAddress address,
                            final TachyonConf tachyonConf) {
        mTachyonConf = Preconditions.checkNotNull(tachyonConf);
        mWorkerDataServerHandler =
                new WorkerDataServerHandler();
        mBootstrap = createBootstrap().childHandler(new PipelineHandler(mWorkerDataServerHandler));

        try {
            mChannelFuture = mBootstrap.bind(address).sync();
        } catch (InterruptedException e) {
            throw Throwables.propagate(e);
        }
    }

  public void close() throws IOException {
    mChannelFuture.channel().close().awaitUninterruptibly();
    mBootstrap.group().shutdownGracefully();
    mBootstrap.childGroup().shutdownGracefully();
  }

  private ServerBootstrap createBootstrap() {
    final ServerBootstrap boot =
        createBootstrapOfType(mTachyonConf.getEnum(Constants.WORKER_NETWORK_NETTY_CHANNEL,
            ChannelType.class));

    // use pooled buffers
    boot.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    boot.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

    // set write buffer
    // this is the default, but its recommended to set it in case of change in future netty.
    boot.childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK,
        (int) mTachyonConf.getBytes(Constants.WORKER_NETWORK_NETTY_WATERMARK_HIGH));
    boot.childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK,
        (int) mTachyonConf.getBytes(Constants.WORKER_NETWORK_NETTY_WATERMARK_LOW));
      
    return boot;
  }

  /**
   * Gets the actual bind hostname.
   */
  public String getBindHost() {
    return ((InetSocketAddress) mChannelFuture.channel().localAddress()).getHostName();
  }

  /**
   * Gets the port listening on.
   */
  public int getPort() {
    // Return value of io.netty.channel.Channel.localAddress() must be down-cast into types like
    // InetSocketAddress to get detailed info such as port.
    return ((InetSocketAddress) mChannelFuture.channel().localAddress()).getPort();
  }

  public boolean isClosed() {
    return mBootstrap.group().isShutdown();
  }

  /**
   * Creates a default {@link io.netty.bootstrap.ServerBootstrap} where the channel and groups are
   * preset.
   *
   * @param type the channel type. Current channel types supported are nio and epoll
   * @return an instance of ServerBootstrap
   */
  private ServerBootstrap createBootstrapOfType(final ChannelType type) {
    final ServerBootstrap boot = new ServerBootstrap();
    // If number of worker threads is 0, Netty creates (#processors * 2) threads by default.
    final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    final EventLoopGroup workerGroup = new NioEventLoopGroup(0);

    final Class<? extends ServerChannel> socketChannelClass =
        NettyUtils.getServerChannelClass(type);
    boot.group(bossGroup, workerGroup).channel(socketChannelClass);

    return boot;
  }
}
