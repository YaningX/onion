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
import tachyon.client.RemoteBlockWriter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;

public class MiniReplicationClusterTest {
    private static WorkerDataServer workerDataServer1;
    private static WorkerDataServer workerDataServer2;
    private static WorkerDataServer workerDataServer3;
    private static String backend1 = System.getProperty("user.dir") + "/target/onionBackend1";
    private static String backend2 = System.getProperty("user.dir") + "/target/onionBackend2";
    private static String backend3 = System.getProperty("user.dir") + "/target/onionBackend3";

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

        workerDataServer1 = new WorkerDataServer(new InetSocketAddress("127.0.0.1", 29998),
                backend1);
        workerDataServer2 = new WorkerDataServer(new InetSocketAddress("127.0.0.1", 2999),
                backend2);
    }

    @AfterClass
    public static void clear() throws IOException {
        workerDataServer1.close();
    }

    private static void clearGeneratedFile() {
        File writeFile = new File(backend1);
        // if ()
    }

    @Test
    public void remoteBlockWriteTest() throws IOException {
        RemoteBlockWriter writer = new NettyRemoteBlockWriter();
        writer.open(new InetSocketAddress("127.0.0.1", 29998), 100, 100);
        RandomAccessFile file = new RandomAccessFile(System.getProperty("user.dir") + "/pom.xml", "rw");
        byte[] sendData = new byte[(int) file.length()];
        file.read(sendData);
        writer.write(sendData, 0, sendData.length);
        writer.close();

        writer.open(new InetSocketAddress("127.0.0.1", 2999), 100, 100);
        writer.write(sendData, 0, sendData.length);
    }
}
