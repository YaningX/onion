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

import java.io.IOException;

/**
 * A cluster based on erasure code.
 */
public class MiniECClusterTest {
    private static Worker[] workers = new Worker[9];

    public void doWrite(byte[][] encodeData, Worker[] workers) throws IOException {
        int count = 0;
        MasterBlockWriter writer = new MasterBlockWriter();
        for (int i = 0; i < encodeData.length; i++) {
            int nextWorker = count % workers.length;
            writer.open(workers[nextWorker].getWorkerAddress(), i, 0);
            writer.write(encodeData[i], 0, encodeData[i].length);
            writer.close();
            count++;
        }
    }

    @BeforeClass
    public static void step() {
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker(System.getProperty("user.dir") +
                    "/src/test/conf/worker" + (i+1) + ".conf");
            workers[i].process();
        }
    }

    @Test
    public void remoteEncodeBlockWriteTest() throws IOException{
        ErasureCoder coder = new CauchyGoodRSCoder(6, 3, 8, 8);
        ECHandler handler = new ECHandler(6, 3, coder, 8, 8);
        byte[][] encodeData = handler.encode(System.getProperty("user.dir") + "/pom.xml");
        doWrite(encodeData, workers);
    }
}
