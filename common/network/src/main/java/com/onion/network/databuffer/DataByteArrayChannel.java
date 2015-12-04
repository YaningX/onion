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

package com.onion.network.databuffer;

import com.google.common.base.Preconditions;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;

/**
 * A DataBuffer backed by a byte[].
 */
public class DataByteArrayChannel implements DataBuffer {
    private final byte[] byteArray;
    private final long offSet;
    private final long length;

    /**
     * @param byteArray the array representing the data
     * @param offSet the offset into the byteArray
     * @param length the length of the data
     */
    public DataByteArrayChannel(byte[] byteArray, long offSet, long length) {
        this.byteArray = Preconditions.checkNotNull(byteArray);
        this.offSet = offSet;
        this.length = length;
    }

    public Object getNettyOutput() {
        return Unpooled.wrappedBuffer(byteArray, (int) offSet, (int) length);
    }

    public long getLength() {
        return length;
    }

    public ByteBuffer getReadOnlyByteBuffer() {
        return ByteBuffer.wrap(byteArray, (int) offSet, (int) length).asReadOnlyBuffer();
    }

    public void release() {
        // Nothing we need to release explicitly, let GC take care of all objects.
    }
}
