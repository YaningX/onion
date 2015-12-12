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
    private static WorkerDataServer workerDataServer1;
    private static WorkerDataServer workerDataServer2;
    private static WorkerDataServer workerDataServer3;
    private static InetSocketAddress address1 = new InetSocketAddress("127.0.0.1", 29998);
    private static InetSocketAddress address2 = new InetSocketAddress("127.0.0.1", 29999);
    private static InetSocketAddress address3 = new InetSocketAddress("127.0.0.1", 30000);
    private static String backend1 = System.getProperty("user.dir") + "/target/onionBackend1";
    private static String backend2 = System.getProperty("user.dir") + "/target/onionBackend2";
    private static String backend3 = System.getProperty("user.dir") + "/target/onionBackend3";
    private static long blockId1 = 100;
    private static long blockId2 = 200;
    private static long blockId3 = 300;

    @BeforeClass
    public static void setup() {
        File backendDir1 = new File(backend1);
        File backendDir2 = new File(backend2);
        File backendDir3 = new File(backend3);

        if (!backendDir1.exists()) {
            backendDir1.mkdirs();
        }

        if (!backendDir2.exists()) {
            backendDir2.mkdirs();
        }

        if (!backendDir3.exists()) {
            backendDir3.mkdirs();
        }

        workerDataServer1 = new WorkerDataServer(address1,
                backend1);
        workerDataServer2 = new WorkerDataServer(address2,
                backend2);
        workerDataServer3 = new WorkerDataServer(address3,
                backend3);
    }

    @AfterClass
    public static void clear() throws IOException {
        workerDataServer1.close();
        workerDataServer2.close();
        workerDataServer3.close();
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
        doWrite(address1, blockId1, sendData);
        doWrite(address2, blockId2, sendData);
        doWrite(address3, blockId3, sendData);
    }

    @Test
    public void remoteBlockReadTest() throws IOException {
        doRead(address1, blockId1, backend1 + "/received.xml");
        doRead(address2, blockId2, backend2 + "/received.xml");
        doRead(address3, blockId3, backend3 + "" +
                "received.xml");
    }
}
