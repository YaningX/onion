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

package com.onion.master;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import tachyon.Constants;
import tachyon.network.protocol.*;
import tachyon.network.protocol.databuffer.DataByteArrayChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Write data to a worker data server using Netty.
 */
public final class MasterBlockWriter {

  private final Bootstrap mClientBootstrap;
  private final ClientHandler mHandler;

  private boolean mOpen;
  private InetSocketAddress mAddress;
  private long mBlockId;
  private long mSessionId;

  // Total number of bytes written to the remote block.
  private long mWrittenBytes;

  /**
   * Creates a new {@link MasterBlockWriter}.
   */
  public MasterBlockWriter() {
    mHandler = new ClientHandler();
    mClientBootstrap = NettyClient.createClientBootstrap(mHandler);
    mOpen = false;
  }

  public void open(InetSocketAddress address, long blockId, long sessionId) throws IOException {
    if (mOpen) {
      throw new IOException();
      //  ExceptionMessage.WRITER_ALREADY_OPEN.getMessage(mAddress, mBlockId, mSessionId));
    }
    mAddress = address;
    mBlockId = blockId;
    mSessionId = sessionId;
    mWrittenBytes = 0;
    mOpen = true;
  }

  public void close() {
    if (mOpen) {
      mOpen = false;
    }
  }

  public void write(byte[] bytes, int offset, int length) throws IOException {
    SingleResponseListener listener = new SingleResponseListener();
    try {
      // TODO(hy): keep connection open across multiple write calls.
      ChannelFuture f = mClientBootstrap.connect(mAddress).sync();

      Channel channel = f.channel();
      mHandler.addListener(listener);
      channel.writeAndFlush(new RPCBlockWriteRequest(mSessionId, mBlockId, mWrittenBytes, length,
              new DataByteArrayChannel(bytes, offset, length)));

      RPCResponse response = listener.get(NettyClient.TIMEOUT_MS, TimeUnit.MILLISECONDS);
      channel.close().sync();

      switch (response.getType()) {
        case RPC_BLOCK_WRITE_RESPONSE:
          RPCBlockWriteResponse resp = (RPCBlockWriteResponse) response;
          RPCResponse.Status status = resp.getStatus();

          if (status != RPCResponse.Status.SUCCESS) {
            throw new IOException( status.getMessage());
          }
          mWrittenBytes += length;
          break;
        case RPC_ERROR_RESPONSE:
          RPCErrorResponse error = (RPCErrorResponse) response;
          throw new IOException(error.getStatus().getMessage());
        default:
          throw new IOException();
      }
    } catch (Exception e) {
      throw new IOException(e);
    } finally {
      mHandler.removeListener(listener);
    }
  }

}
