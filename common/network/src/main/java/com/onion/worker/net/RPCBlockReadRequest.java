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
package com.onion.worker.net;

import io.netty.buffer.ByteBuf;

public class RPCBlockReadRequest extends RPCRequest {
    private long blockId;
    private long offSet;
    private long length;

    public RPCBlockReadRequest(long blockId, long offSet, long length) {
        this.blockId = blockId;
        this.offSet = offSet;
        this.length = length;
    }

    /**
     * Decodes the input {@link ByteBuf} into a {@link RPCBlockReadRequest} object and returns it.
     * @param in the input {@link ByteBuf}
     * @return The decoded RPCBlockReadRequest object
     */
    public static RPCBlockReadRequest decode(ByteBuf in) {
        long blockId = in.readLong();
        long offSet = in.readLong();
        long length = in.readLong();
        return new RPCBlockReadRequest(blockId, offSet, length);
    }

    public int getEncodedLength() {
        return 0;
    }

    public void encode(ByteBuf out) {
        out.writeLong(blockId);
        out.writeLong(offSet);
        out.writeLong(length);
    }

    @Override
    public String toString() {
        return "RPCBlockReadRequest(" + blockId + ", " + offSet + ", " + length + ")";
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

    @Override
    public Type getType() {
        return Type.RPC_BLOCK_READ_REQUEST;
    }
}
