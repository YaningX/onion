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
import io.netty.channel.DefaultFileRegion;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * A DataBuffer with the underlying data being a {@link FileChannel}.
 */
public class DataFileChannel implements DataBuffer {
    private final FileChannel fileChannel;
    private final long offSet;
    private final long length;

    /**
     *
     * @param fileChannel The FileChannel representing the data
     * @param offSet The offset into the FileChannel
     * @param length The length of the data to read
     */
    public DataFileChannel(FileChannel fileChannel, long offSet, long length) {
        this.fileChannel = Preconditions.checkNotNull(fileChannel);
        this.offSet = offSet;
        this.length = length;
    }

    public Object getNettyOutput() {
        return new DefaultFileRegion(fileChannel, offSet, length);
    }

    public long getLength() {
        return length;
    }

    public ByteBuffer getReadOnlyByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate((int) length);
        try {
            fileChannel.position(offSet);
            int bytesRead = 0;
            long bytesRemaining = length;
            while (bytesRemaining > 0 && (bytesRead = fileChannel.read(buffer)) >= 0) {
                bytesRemaining -= bytesRead;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        ByteBuffer readOnly = buffer.asReadOnlyBuffer();
        readOnly.position(0);
        return readOnly;
    }

    public void release() {
        // Nothing we need to release explicitly, let GC take care of all objects.
    }
}
