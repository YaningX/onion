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
package com.onion.network.protocol;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import com.onion.network.databuffer.DataBuffer;
import com.onion.network.databuffer.DataNettyBuffer;
import io.netty.buffer.ByteBuf;

public class RPCBlockReadResponse extends RPCResponse {
    private final long blockId;
    private final long offSet;
    private final long length;
    private final DataBuffer dataBuffer;
    private final Status status;

    public RPCBlockReadResponse(long blockId, long offSet, long length, DataBuffer dataBuffer, Status status) {
        this.blockId = blockId;
        this.offSet = offSet;
        this.length = length;
        this.status = status;
        this.dataBuffer = dataBuffer;
    }

    @Override
    public Type getType() {
        return RPCMessage.Type.RPC_BLOCK_READ_RESPONSE;
    }

    public int getEncodedLength() {
        // 3 longs (mBLockId, mOffset, mLength) + 1 short (mStatus)
        return Longs.BYTES * 3 + Shorts.BYTES;
    }

    public void encode(ByteBuf out) {
        out.writeLong(blockId);
        out.writeLong(offSet);
        out.writeLong(length);
        out.writeLong(status.getId());
    }

    /**
     * Creates a {@link RPCBlockReadResponse} object that indicates an error for the given {@link RPCBlockReadRequest}.
     * @param request
     * @param status
     * @return
     */
    public static RPCBlockReadResponse createErrorResponse(final RPCBlockReadRequest request,
                                                           final Status status) {
        Preconditions.checkArgument(status != Status.SUCCESS);
        // The response has no payload, so length must be 0.
        return new RPCBlockReadResponse(request.getBlockId(), request.getOffSet(), 0, null, status);
    }

    public static RPCBlockReadResponse decode(ByteBuf in) {
        long blockId = in.readLong();
        long offset = in.readLong();
        long length = in.readLong();
        short status = in.readShort();
        DataBuffer data = null;
        if (length > 0) {
            // use DataNettyBuffer instead of DataByteBuffer to avoid copying
            data = new DataNettyBuffer(in, (int) length);
        }
        return new RPCBlockReadResponse(blockId, offset, length, data, Status.fromShort(status));
    }

    @Override
    public DataBuffer getPayloadDataBuffer() {
        return dataBuffer;
    }


    public long getBlockId() {
        return blockId;
    }

    public long getLength() {
        return length;
    }

    public long getOffSet() {
        return offSet;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "RPCBlockReadResponse(" + blockId + ", " + offSet
                + ", " + length + ", " + status + ")";
    }

}
