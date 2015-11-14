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

package com.yaningx.onion;

import com.google.common.base.Preconditions;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;

public class ECTest {
    private ErasureCoder coder;
    private int k;
    private int m;
    private int wordSize;

    /**
     * Suppose that the size of original file is not larger than 32GB.
     * If the size is larger than 32GB, it is to be implemented by being divided into several pieces.
     * This is not done in this test, but will be done in the main code base.
     * @param oriFile
     * @param k
     * @param m
     * @throws IOException
     */
    private void runWith(File backupDir, File oriFile, int k, int m) throws IOException {
        long dataSize = oriFile.length();
        Preconditions.checkArgument(dataSize <= Integer.MAX_VALUE, "The original file is too large.");
        int blockSize = (int) dataSize / k;
        int wholeSize = blockSize * k;
        if (dataSize != wholeSize) {
            blockSize++;
            wholeSize = blockSize * k;
        }

        /**
         *  Read data from a file into a two-dimension array.
         */
        byte[][] data = new byte[k][blockSize];
        byte[] wholeData = new byte[wholeSize];
        readFile(wholeData, oriFile);
        Arrays.fill(wholeData, (int) dataSize, wholeSize - 1, (byte) 0);
        for (int i = 0; i < k; i++) {
            System.arraycopy(wholeData, i * blockSize, data[i], 0, blockSize);
        }

        /**
         * Encode and generate the parity blocks.
         */
        byte[][] parity = coder.encode(data);
        /**
         * Write data blocks into files.
         */
        for (int i = 0; i < k; i++) {
            writeFile(data[i], backupDir, oriFile.getName() + "_k" + (i + 1));
        }

        /**
         * Write parity blocks into files.
         */
        for (int i = 0; i < m; i++) {
            writeFile(parity[i], backupDir, oriFile.getName() + "_m" + (i + 1));
        }
    }

    private void checkAndLoadFile(int erasures[], byte[][] data, byte[][] parity, File oriFile) throws IOException {

    }

    private void writeFile(byte[] data, File backupDir, String fileName) throws IOException {
        if (!backupDir.exists() && !backupDir.mkdir()) {
            throw new IOException("Cannot make backup directory");
        }
        File file = new File(backupDir, fileName);
        OutputStream outputStream = new FileOutputStream(file);
        outputStream.write(data);
        outputStream.close();
    }


    private void readFile(byte[] data, File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        inputStream.read(data);
        inputStream.close();
    }

    @Test
    public void vandermonTest() throws IOException {
        this.k = 6;
        this.m = 3;
        this.wordSize = 8;
        this.coder = new VandermondeRSCoder(k, m, wordSize);
        /**
         * Write data blocks into files.
         */
        File backupDir = new File("/Users/xuyaning/work/backup");
        File oriFile = new File(backupDir, "origin.txt");
        runWith(backupDir, oriFile, k, m);

    }
    
}
