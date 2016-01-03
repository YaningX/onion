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
package com.onion.master;

import com.onion.DBUtil.DBUtil;
import com.onion.eclib.ECHandler;
import com.onion.eclib.ErasureCoder;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Onion master to handle storage affairs.
 */
public class Master {
    //Map input file to storage node file.
//    private Map<String, List<Integer>> storageMap = new HashMap<String, List<Integer>>();
    private MasterConf masterConf;
    private int dataWorkerAmount;
    private int parityWorkerAmount;
    private int wordSize;
    private int packetSize;
    private ErasureCoder coder;
    private ECHandler ecHandler;
    private List<InetSocketAddress> addresses;

    public Master(File masterConfSrc) throws Exception {
        masterConf = new MasterConf(masterConfSrc);
        dataWorkerAmount = masterConf.getDataWorkerAmount();
        parityWorkerAmount = masterConf.getParityWorkerAmount();
        wordSize = masterConf.getWordSize();
        packetSize = masterConf.getPacketSize();
        addresses = masterConf.getWorkerAddresses();
        Class coderClass = Class.forName("com.onion.eclib." + masterConf.getErasureCodeType());
        Constructor<?> constructor = coderClass.getConstructor(int.class,
                int.class, int.class, int.class);
        coder = (ErasureCoder) constructor.newInstance(dataWorkerAmount, parityWorkerAmount,
                wordSize, packetSize);
//        coder = new CauchyGoodRSCoder(dataWorkerAmount,
//                parityWorkerAmount, wordSize, packetSize);
        //Support Cauchcy Good RS code now, more coding types will be imported.
        ecHandler = new ECHandler(dataWorkerAmount,
                parityWorkerAmount, coder, wordSize, packetSize);
    }

    public boolean write(String srcPath) {
        byte[][] encodeData = ecHandler.encode(srcPath);
        long[] blockIDs = generateBlockId(encodeData.length);
        MasterBlockWriter writer = new MasterBlockWriter();
        for (int i = 0; i < encodeData.length; i++) {
            try {
                writer.open(addresses.get(i), blockIDs[i], 0);
                writer.write(encodeData[i], 0, encodeData[i].length);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        writer.close();
        Properties property = new Properties();
        try {
            property.load(new FileInputStream(System.getProperty("user.dir") + "db.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        DBUtil util = new DBUtil(property.getProperty("url"), property.getProperty("username"),
                 property.getProperty("password"));
        util.write(srcPath, blockIDs);
        return true;
    }

    public boolean read(String inputFile, String recoveredFile) {
        Properties property = new Properties();
        DBUtil util = new DBUtil(property.getProperty("url"), property.getProperty("username"),
                property.getProperty("password"));
        long[] blockIDs = new long[dataWorkerAmount + parityWorkerAmount];
        util.read(inputFile, blockIDs);
        byte[][] data = new byte[dataWorkerAmount + parityWorkerAmount][];
        MasterBlockReader reader = new MasterBlockReader();
        for (int i = 0; i < addresses.size(); i++) {
            try {
                ByteBuffer buffer = reader.readRemoteBlock(addresses.get(i), blockIDs[i], 0, data[i].length);
                buffer.get(data[i]);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int erasures[] = generateRandomArray(dataWorkerAmount);
        ecHandler.decode(recoveredFile, new File(inputFile).length(), erasures, data);
        return true;
    }

    private int[] generateRandomArray(int ArrayLen) {
        int[] randomArray = new int[ArrayLen];
        List<Integer> list = new ArrayList<Integer>(dataWorkerAmount + parityWorkerAmount);
        for (int i = 0; i < dataWorkerAmount + parityWorkerAmount; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        for (int i = 0; i < dataWorkerAmount; i++) {
            randomArray[i] = list.get(i);
        }
        return randomArray;
    }

    public boolean delete(File inputFile) {
        //TODO
        return false;
    }

    private synchronized long[] generateBlockId(int ArrayLen) {
        Properties property = new Properties();
        try {
            property.load(new FileInputStream(System.getProperty("user.dir") + "/blockID.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        long id = Long.parseLong(property.getProperty("id"));
        long[] ids = new long[ArrayLen];
        for (int i = 1; i <= ArrayLen; i++) {
            ids[i] = id + i;
        }
        property.setProperty("id", String.valueOf(id + ArrayLen));
        try {
            property.store(new FileOutputStream(System.getProperty("user.dir") + "/blockID.properties"),
                    "the value of id");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ids;
    }
}
