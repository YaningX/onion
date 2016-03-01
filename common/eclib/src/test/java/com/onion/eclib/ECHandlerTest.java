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
package com.onion.eclib;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ECHandlerTest {
    private int k;
    private int m;
    private ErasureCoder coder;
    private int wordSize;
    private int packetSize;



    @Test
    /**
     * @param
     */
    public void Test() throws IOException {
        this.k = 4;
        this.m = 3;
        this.wordSize = 8;
        this.packetSize = 8;
        this.coder = new CauchyGoodRSCoder(k, m, wordSize, packetSize);
        try {
            /** encode test **/
            ECHandler encodeTest = new ECHandler(k, m, coder,wordSize,packetSize);
            String inputFilePath = System.getProperty("user.dir") + "/pom.xml";
            byte[][] data = encodeTest.encode(inputFilePath);

            /** decode test **/
            String outputFilePath = System.getProperty("user.dir") + "/target/backup/recover_"+"pom.xml";

            int[] erasures = generateRandomArray(m);/** data deletion Simulation **/
            int blockSize = data[0].length;
            byte[] tmpArray = new byte[blockSize];
            for(int i = 0; i < m; i++) {
                System.arraycopy(tmpArray, 0, data[erasures[i]], 0, blockSize);
            }
            File f = new File(inputFilePath);
            long fileSize = f.length();

            ECHandler decodeTest = new ECHandler(k,m,coder,wordSize,packetSize);
            decodeTest.decode(outputFilePath, fileSize, erasures, data);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
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


}
