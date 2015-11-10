import com.google.common.base.Preconditions;
import com.yaningx.onion.ErasureCoder;
import com.yaningx.onion.VandermondeRSCoder;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;

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
    private void runWith(ErasureCoder coder, File oriFile, int k, int m) throws IOException {
        long dataSize = oriFile.length();
        Preconditions.checkArgument(dataSize <= Integer.MAX_VALUE, "The original file is too large.");
        int blockSize = (int) dataSize / k;
        int paddingSize = 0;

        if (dataSize != blockSize * k) {
            blockSize++;
            paddingSize = blockSize * k - (int) dataSize;
        }

        byte[][] data = new byte[k][blockSize];
        InputStream inputStream = new FileInputStream(oriFile);
        for (int i = 0; i < data.length - 1; i++) {
            inputStream.read(data[i], i * blockSize, i * blockSize + blockSize - 1);
        }
        inputStream.read(data[k - 1]);
        Arrays.fill(data, blockSize - paddingSize - 1, blockSize - 1, (byte) 0);

        byte[][] parity = coder.encode(data);
        /**
         * Write data blocks into files.
         */
        for (int i = 0; i < k; i++) {

        }

        /**
         * Write parity blocks into files.
         */
        for (int i = 0; i < m; i++) {

        }
    }

    @Test
    public void vandermonTest() throws IOException {
        this.k = 6;
        this.m = 3;
        this.wordSize = 8;
        this.coder = new VandermondeRSCoder(k, m, wordSize);
        File oriFile = new File("");
        runWith(coder, oriFile, k, m);
    }

}
