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
package com.onion.net;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import io.netty.buffer.ByteBuf;

public class RPCBlockWriteResponse extends RPCResponse {
    private final long sessionId;
    private final long blockId;
    private final long offSet;
    private final long length;
    private final Status status;

    public RPCBlockWriteResponse(long sessionId, long blockId, long offSet, long length, Status status) {
        this.sessionId = sessionId;
        this.blockId = blockId;
        this.offSet = offSet;
        this.length = length;
        this.status = status;
    }


    /**
     * Creates a {@link RPCBlockWriteResponse} object that indicates an error for the given.
     * @param request
     * @param status
     * @return
     */
    public static RPCBlockWriteResponse createErrorResponse(final RPCBlockWriteRequest request,
                                                            final Status status) {
        Preconditions.checkArgument(status != Status.SUCCESS);
        // The response has no payload, so length must be 0.
        return new RPCBlockWriteResponse(request.getSessionId(), request.getBlockId(),
                request.getOffSet(), request.getLength(), status);
    }

    public static RPCBlockWriteResponse decode(ByteBuf in) {
        long sessionId = in.readLong();
        long blockId = in.readLong();
        long offset = in.readLong();
        long length = in.readLong();
        short status = in.readShort();
        return new RPCBlockWriteResponse(sessionId, blockId, offset, length, Status.fromShort(status));
    }

    public int getEncodedLength() {
        // 4 longs (mSessionId, mBlockId, mOffset, mLength) + 1 short (mStatus)
        return Longs.BYTES * 4 + Shorts.BYTES;
    }

    @Override
    public Type getType() {
        return Type.RPC_BLOCK_WRITE_RESPONSE;
    }

    public void encode(ByteBuf out) {
        out.writeLong(sessionId);
        out.writeLong(blockId);
        out.writeLong(offSet);
        out.writeLong(length);
        out.writeShort(status.getId());
    }

    public long getSessionId() {
        return sessionId;
    }

    public long getBlockId() {
        return blockId;
    }

    public long getLength() {
        return length;
    }

    public long getOffset() {
        return offSet;
    }

    public Status getStatus() {
        return status;
    }
}
