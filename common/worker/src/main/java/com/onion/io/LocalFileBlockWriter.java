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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * This class provides write access to a temp block data file locally stored in data node.
 * <p>
 * This class does not provide thread-safety. Corresponding lock must be acquired.
 */
public class LocalFileBlockWriter implements BlockWriter {
    private final String filePath;
    private final RandomAccessFile localFile;
    private final FileChannel localFileChannel;
    private final Closer closer = Closer.create();

    public LocalFileBlockWriter(String filePath) throws FileNotFoundException {
        this.filePath = Preconditions.checkNotNull(filePath);
        this.localFile = closer.register(new RandomAccessFile(this.filePath, "rw"));
        this.localFileChannel = closer.register(localFile.getChannel());
    }

    public long append(ByteBuffer inputBuf) throws IOException {
        try {
            return write(localFileChannel.size(), inputBuf);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private long write(long offset, ByteBuffer inputBuf) throws
            IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        int inputBufLength = inputBuf.limit();
        ByteBuffer outputBuf =
                localFileChannel.map(FileChannel.MapMode.READ_WRITE, offset, inputBufLength);
        outputBuf.put(inputBuf);
        int bytesWritten = outputBuf.limit();
        if (outputBuf.isDirect()) {
            cleanDirectBuffer(outputBuf);
        }
        return bytesWritten;
    }

    private void cleanDirectBuffer(ByteBuffer buffer) throws
            NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method bufCleanMethod = buffer.getClass().getMethod("cleaner");
        bufCleanMethod.setAccessible(true);
        final Object cleaner = bufCleanMethod.invoke(buffer);
        if (cleaner == null) {
            return;
        }

        Method cleanerCleanMethod = cleaner.getClass().getMethod("clean");
        cleanerCleanMethod.invoke(cleaner);
    }

    public WritableByteChannel getChannel() {
        return localFileChannel;
    }

    public void close() throws IOException {
        closer.close();
    }
}
