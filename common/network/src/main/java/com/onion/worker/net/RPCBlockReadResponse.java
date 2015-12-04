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

public class RPCBlockReadResponse extends RPCResponse {
    private final long blockId;
    private final long offSet;
    private final long length;
    private final Status status;

    public RPCBlockReadResponse(long blockId, long offSet, long length, Status status) {
        this.blockId = blockId;
        this.offSet = offSet;
        this.length = length;
        this.status = status;
    }

    @Override
    public RPCMessage.Type getType() {
        return RPCMessage.Type.RPC_BLOCK_READ_RESPONSE;
    }
}
