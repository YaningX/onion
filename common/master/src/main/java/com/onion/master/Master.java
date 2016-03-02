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
        ecHandler = new ECHandler(dataWorkerAmount,
                parityWorkerAmount, coder, wordSize, packetSize);
    }

    public long write(String srcPath) {
        byte[][] encodeData = ecHandler.encode(srcPath);
        long blockID = generateBlockId();
        MasterBlockWriter writer = new MasterBlockWriter();
        for (int i = 0; i < encodeData.length; i++) {
            try {
                writer.open(addresses.get(i), blockID, 0);
                writer.write(encodeData[i], 0, encodeData[i].length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer.close();
        }
        String filename = new File(srcPath).getName();
        FileConf fileConf = new FileConf(new File(masterConf.getFileInfo()));
        try {
            fileConf.setFileInfo(filename, encodeData[0].length, new File(srcPath).length(), blockID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blockID;
    }

    public boolean read(long blockId, String recoveredFile) {
        FileConf fileConf = new FileConf(new File(masterConf.getFileInfo()));
        fileConf.setFile(blockId);
        long blockSize = fileConf.getBlockSize();
        long fileSize = fileConf.getFileSize();
        byte[][] data = new byte[dataWorkerAmount + parityWorkerAmount][(int)blockSize];
        MasterBlockReader reader = new MasterBlockReader();
        for (int i = 0; i < dataWorkerAmount + parityWorkerAmount; i++) {
            try {
                ByteBuffer buffer = reader.readRemoteBlock(addresses.get(i), blockId, 0, blockSize);
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
        int erasures[] = generateRandomArray(parityWorkerAmount);
        ecHandler.decode(recoveredFile, fileSize, erasures, data);
        return true;
    }

    private int[] generateRandomArray(int arrayLen) {
        int[] randomArray = new int[arrayLen];
        List<Integer> list = new ArrayList<Integer>(dataWorkerAmount + parityWorkerAmount);
        for (int i = 0; i < dataWorkerAmount + parityWorkerAmount; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        for (int i = 0; i < parityWorkerAmount; i++) {
            randomArray[i] = list.get(i);
        }
        return randomArray;
    }

    public boolean delete(File inputFile) {
        //TODO
        return false;
    }

    private synchronized long generateBlockId() {
        Properties property = new Properties();
        try {
            property.load(new FileInputStream(masterConf.getBlockIdConf()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        long id = Long.parseLong(property.getProperty("id"));

        property.setProperty("id", String.valueOf(id + 1));
        try {
            property.store(new FileOutputStream(masterConf.getBlockIdConf()),
                    "the value of id");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return id;
    }

}
