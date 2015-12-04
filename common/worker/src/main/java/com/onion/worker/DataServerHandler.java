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

import com.onion.worker.net.RPCBlockReadRequest;
import com.onion.worker.net.RPCBlockRequest;
import com.onion.worker.net.RPCBlockWriteRequest;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Handle the request from master.
 */
@ChannelHandler.Sharable
public class DataServerHandler extends SimpleChannelInboundHandler<RPCBlockRequest> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, final RPCBlockRequest msg) {
    }

    private void handleBlockReadRequest(ChannelHandlerContext ctx, RPCBlockReadRequest readRequest) {

    }

    private void handleBlockWriteRequest(ChannelHandlerContext ctx, RPCBlockWriteRequest writeRequest) {

    }
}
