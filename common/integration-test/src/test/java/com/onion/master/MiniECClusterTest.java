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
package com.onion.master;

import com.onion.eclib.CauchyGoodRSCoder;
import com.onion.eclib.ECHandler;
import com.onion.eclib.ErasureCoder;
import com.onion.worker.Worker;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A cluster based on erasure code.
 */
public class MiniECClusterTest {
    private static int k;
    private static int m;
    private static ErasureCoder coder;
    private static int wordSize;
    private static int packetSize;
    private static Worker[] workers;

    private int[] generateRandomArray(int ArrayLen) {
        int[] randomArray = new int[ArrayLen];
        List<Integer> list = new ArrayList<Integer>(k + m);
        for (int i = 0; i < k + m; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        for (int i = 0; i < m; i++) {
            randomArray[i] = list.get(i);
        }
        return randomArray;
    }

    public byte[][] doRead(Worker[] workers, int blockSize) throws IOException {
        byte[][] data = new byte[k+m][blockSize];
        MasterBlockReader reader = new MasterBlockReader();
        for (int i = 0; i < workers.length; i++) {
            ByteBuffer buffer = reader.readRemoteBlock(workers[i].getWorkerAddress(), i, 0, blockSize);
            buffer.get(data[i]);
        }
        reader.close();
        return data;
    }

    public void doWrite(byte[][] encodeData, Worker[] workers) throws IOException {
        MasterBlockWriter writer = new MasterBlockWriter();
        for (int i = 0; i < encodeData.length; i++) {
            File file = new File(workers[i].getBackendDir() + "/"+ i);
            if (file.exists()) {
                if (!file.delete()) {
                    throw new IOException("Can not delete file:" + file.toString());
                }
            }
            writer.open(workers[i].getWorkerAddress(), i, 0);
            writer.write(encodeData[i], 0, encodeData[i].length);
            writer.close();
        }
    }

    @BeforeClass
    public static void step() {
        k = 6;
        m = 3;
        wordSize = 8;
        packetSize = 8;
        coder = new CauchyGoodRSCoder(k, m, wordSize, packetSize);
        workers = new Worker[k + m];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker(System.getProperty("user.dir") +
                    "/src/test/conf/worker" + (i+1) + ".conf");
            workers[i].process();
        }
    }

    @Test
    public void remoteECTest() throws IOException{
        /** remote encode test**/
        ErasureCoder coder = new CauchyGoodRSCoder(k, m, wordSize, packetSize);
        ECHandler handler = new ECHandler(k, m, coder, wordSize, packetSize);
        File srcFile = new File(System.getProperty("user.dir") + "/pom.xml");
        byte[][] encodeData = handler.encode(srcFile.toString());
        doWrite(encodeData, workers);

        /** remote decode test **/
        long fileLength = srcFile.length();
        int blockSize = encodeData[0].length;
        byte[][] decodeData = doRead(workers, blockSize);
        int erasures[] = generateRandomArray(m);
        byte[] tmpArray = new byte[blockSize];
        for (int i = 0; i < m; i++) {
            System.arraycopy(tmpArray, 0, decodeData[erasures[i]], 0, blockSize);
        }
        String destPath = System.getProperty("user.dir") + "/target/recover_" + "pom.xml";
        //delete the existed destFIle
        File destFile = new File(destPath);
        if (destFile.exists()) {
            if ( !destFile.delete()) {
                throw new IOException("can not delete file:" + destPath);
            }
        }
        handler.decode(destPath, fileLength, erasures, decodeData);
    }
}
