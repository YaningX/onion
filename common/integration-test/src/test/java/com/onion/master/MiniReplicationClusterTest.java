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


import com.onion.worker.Worker;
import com.onion.worker.WorkerDataServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class MiniReplicationClusterTest {
    private static Worker worker1 = new Worker(System.getProperty("user.dir") + "/src/test/conf/worker1.conf");
    private static Worker worker2 = new Worker(System.getProperty("user.dir") + "/src/test/conf/worker2.conf");
    private static Worker worker3 = new Worker(System.getProperty("user.dir") + "/src/test/conf/worker3.conf");
    private static long blockId1 = 100;
    private static long blockId2 = 200;
    private static long blockId3 = 300;

    @BeforeClass
    public static void setup() {
        worker1.process();
        worker2.process();
        worker3.process();
    }

    @AfterClass
    public static void clear() throws IOException {

    }

    private void doWrite(InetSocketAddress address, long blockId, String sendDataPath) throws IOException {
        MasterBlockWriter writer = new MasterBlockWriter();
        writer.open(address, blockId, 0);
        RandomAccessFile file = new RandomAccessFile(sendDataPath, "rw");
        byte[] sendData = new byte[(int) file.length()];
        file.read(sendData);
        writer.write(sendData, 0, sendData.length);
        writer.close();
    }

    private void doRead(InetSocketAddress address, long blockId, String receivedFilePath) throws IOException {
        MasterBlockReader reader = new MasterBlockReader();
        RandomAccessFile file = new RandomAccessFile(System.getProperty("user.dir") + "/pom.xml", "rw");
        ByteBuffer receivedBuf = reader.readRemoteBlock(address, blockId, 0, file.length());
        RandomAccessFile receivedFile = new RandomAccessFile(receivedFilePath, "rw");
        byte[] receivedData = new byte[receivedBuf.limit()];
        receivedBuf.get(receivedData);
        receivedFile.write(receivedData);
        receivedFile.close();
        reader.close();
    }

    @Test
    public void remoteBlockWriteTest() throws IOException {
        String sendData = System.getProperty("user.dir") + "/pom.xml";
        doWrite(worker1.getWorkerAddress(), blockId1, sendData);
        doWrite(worker2.getWorkerAddress(), blockId2, sendData);
        doWrite(worker3.getWorkerAddress(), blockId3, sendData);
    }

    @Test
    public void remoteBlockReadTest() throws IOException {
        doRead(worker1.getWorkerAddress(), blockId1, worker1.getBackendDir() + "/received.xml");
        doRead(worker2.getWorkerAddress(), blockId2, worker2.getBackendDir() + "/received.xml");
        doRead(worker3.getWorkerAddress(), blockId3, worker3.getBackendDir() + "received.xml");
        System.out.println(System.getProperty("user.dir"));
    }
}
