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
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tachyon.Constants;
import tachyon.conf.TachyonConf;
import tachyon.network.protocol.*;
import tachyon.network.protocol.databuffer.DataBuffer;
import tachyon.network.protocol.databuffer.DataByteBuffer;
import tachyon.network.protocol.databuffer.DataFileChannel;
import tachyon.worker.block.BlockDataManager;
import tachyon.worker.block.io.BlockReader;
import tachyon.worker.block.io.BlockWriter;
import tachyon.worker.block.io.LocalFileBlockWriter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


@ChannelHandler.Sharable
public final class DataServerHandler extends SimpleChannelInboundHandler<RPCMessage> {
    private static final Logger LOG = LoggerFactory.getLogger(Constants.LOGGER_TYPE);

    private final TachyonConf mTachyonConf;
    private final FileTransferType mTransferType;

    public DataServerHandler(final BlockDataManager dataManager, TachyonConf tachyonConf) {
        mTachyonConf = Preconditions.checkNotNull(tachyonConf);
        mTransferType = mTachyonConf.getEnum(Constants.WORKER_NETWORK_NETTY_FILE_TRANSFER_TYPE,
                FileTransferType.class);
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final RPCMessage msg)
            throws IOException {
        switch (msg.getType()) {
            case RPC_BLOCK_READ_REQUEST:
                assert msg instanceof RPCBlockReadRequest;
                handleBlockReadRequest(ctx, (RPCBlockReadRequest) msg);
                break;
            case RPC_BLOCK_WRITE_REQUEST:
                assert msg instanceof RPCBlockWriteRequest;
                handleBlockWriteRequest(ctx, (RPCBlockWriteRequest) msg);
                break;
            default:
                RPCErrorResponse resp = new RPCErrorResponse(RPCResponse.Status.UNKNOWN_MESSAGE_ERROR);
                ctx.writeAndFlush(resp);
                throw new IllegalArgumentException(
                        "No handler implementation for rpc msg type: " + msg.getType());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.warn("Exception thrown while processing request", cause);
        ctx.close();
    }

    private void handleBlockReadRequest(final ChannelHandlerContext ctx,
                                        final RPCBlockReadRequest req) throws IOException {
    }



    private void handleBlockWriteRequest(final ChannelHandlerContext ctx,
                                         final RPCBlockWriteRequest req) throws IOException {
        final long sessionId = req.getSessionId();
        final long blockId = req.getBlockId();
        final long offset = req.getOffset();
        final long length = req.getLength();
        final DataBuffer data = req.getPayloadDataBuffer();

        BlockWriter blockWriter = null;
        try {
            blockWriter = new LocalFileBlockWriter(System.getProperty("user.dir") + "/target/" + blockId);
            req.validate();
            ByteBuffer buffer = data.getReadOnlyByteBuffer();
            blockWriter.append(buffer);
            RPCBlockWriteResponse resp =
                    new RPCBlockWriteResponse(sessionId, blockId, offset, length, RPCResponse.Status.SUCCESS);
            ChannelFuture future = ctx.writeAndFlush(resp);
            future.addListener(ChannelFutureListener.CLOSE);

        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Error writing remote block : {}", e.getMessage(), e);
            RPCBlockWriteResponse resp =
                    RPCBlockWriteResponse.createErrorResponse(req, RPCResponse.Status.WRITE_ERROR);
            ChannelFuture future = ctx.writeAndFlush(resp);
            future.addListener(ChannelFutureListener.CLOSE);
            if (blockWriter != null) {
                blockWriter.close();
            }
        }
    }

    /**
     * Returns how much of a file to read. When {@code len} is {@code -1}, then
     * {@code fileLength - offset} is used.
     */
    private long returnLength(final long offset, final long len, final long fileLength) {
        return (len == -1) ? fileLength - offset : len;
    }

    private void validateBounds(final RPCBlockReadRequest req, final long fileLength) {
        Preconditions.checkArgument(req.getOffset() <= fileLength,
                "Offset(%s) is larger than file length(%s)", req.getOffset(), fileLength);
        Preconditions.checkArgument(
                req.getLength() == -1 || req.getOffset() + req.getLength() <= fileLength,
                "Offset(%s) plus length(%s) is larger than file length(%s)", req.getOffset(),
                req.getLength(), fileLength);
    }

    /**
     * Returns the appropriate DataBuffer representing the data to send, depending on the configurable
     * transfer type.
     *
     * @param req The initiating RPCBlockReadRequest
     * @param reader The BlockHandler for the block to read
     * @param readLength The length, in bytes, of the data to read from the block
     * @return a DataBuffer representing the data
     * @throws IOException
     * @throws IllegalArgumentException
     */
    private DataBuffer getDataBuffer(RPCBlockReadRequest req, BlockReader reader, long readLength)
            throws IOException, IllegalArgumentException {
        switch (mTransferType) {
            case MAPPED:
                ByteBuffer data = reader.read(req.getOffset(), (int) readLength);
                return new DataByteBuffer(data, readLength);
            case TRANSFER: // intend to fall through as TRANSFER is the default type.
            default:
                if (reader.getChannel() instanceof FileChannel) {
                    return new DataFileChannel((FileChannel) reader.getChannel(), req.getOffset(),
                            readLength);
                }
                reader.close();
                throw new IllegalArgumentException("Only FileChannel is supported!");
        }
    }
}
