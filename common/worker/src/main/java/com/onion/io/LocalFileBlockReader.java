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
package com.onion.io;

import com.google.common.base.Preconditions;
import com.google.common.io.Closer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * This class provides read access to a block data file locally stored in managed storage.
 * <p>
 * This class does not provide thread-safety. Corresponding lock must be acquired.
 */
public class LocalFileBlockReader implements BlockReader {
    private final String filePath;
    private final RandomAccessFile localFile;
    private final FileChannel localFileChannel;
    private final Closer closer = Closer.create();
    private final long fileSize;

    public LocalFileBlockReader(String filePath) throws IOException {
        this.filePath = Preconditions.checkNotNull(filePath);
        this.localFile = closer.register(new RandomAccessFile(this.filePath, "r"));
        this.localFileChannel = closer.register(localFile.getChannel());
        this.fileSize = localFile.length();
    }

    public ByteBuffer read(long offset, long length) throws IOException {
        Preconditions.checkArgument(offset + length <= fileSize,
                "offset=%s, length=%s, exceeding fileSize=%s", offset, length, fileSize);
        if (length == -1L) {
            length = fileSize - offset;
        }
        return localFileChannel.map(FileChannel.MapMode.READ_ONLY, offset, length);
    }

    public long getLength() {
        return fileSize;
    }

    public ReadableByteChannel getChannel() {
        return localFileChannel;
    }

    public void close() throws IOException {
        closer.close();
    }
}
