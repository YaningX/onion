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

import com.google.common.base.Preconditions;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This is a hanlder to encode and decode a file. A wrapper for ErasureCoder.java.
 */
public class ECHandler {
    private ErasureCoder coder;
    private int k;
    private int m;
    private int wordSize = 8;
    private int packetSize = 8;
    private byte[][] storageData;
    private int origSize;
    /**
     * A constructor for ECHandler, parameter is not determined.
     */
    public ECHandler(int k, int m, int codeStyle, int wordSize, int packetSize) throws IOException {

        this.k = k;
        this.m = m;
        this.wordSize = wordSize;
        this.packetSize = packetSize;

        switch (codeStyle) {
            case 0 :
                this.coder = new CauchyRSCoder(k, m, wordSize, packetSize);
                break;
            case 1 :
                this.coder = new CauchyGoodRSCoder(k, m, wordSize, packetSize);
                break;
            case 2 :
                this.coder = new VandermondeRSCoder(k, m, wordSize);
                break;
            default: throw new IOException("can not create ECHandler!");
        }

    }


    /**
     * Encode a file to k byte arrays
     * @param dataPath file path
     */
    public  byte[][] encode(String dataPath) {
        try {
            encode(new File(dataPath));

        }
        catch(IOException e){
            e.printStackTrace();
        }
        return this.storageData;
    }

    /**
     *  Encode a file to k byte arrays
     * @param dataFile
     */
    private void encode(File dataFile) throws IOException {
        long dataSize = dataFile.length();
        Preconditions.checkArgument(dataSize <= Integer.MAX_VALUE, "The original file is too large.");
        this.origSize = (int) dataSize;
        int blockSize = (int) dataSize / k;
        blockSize = (blockSize / (packetSize * wordSize + 1) + 1) * (packetSize * wordSize);
        int wholeSize = blockSize * k;
        /**
         *  Read data from a file into a two-dimension array.
         */
        byte[][] data = new byte[k][blockSize];
        byte[] wholeData = new byte[wholeSize];
        readFile(wholeData, dataFile);
        Arrays.fill(wholeData, (int) dataSize, wholeSize - 1, (byte) 0);
        for (int i = 0; i < k; i++) {
            System.arraycopy(wholeData, i * blockSize, data[i], 0, blockSize);
        }
        byte[][] parity = coder.encode(data);

        /**
         * Encode and generate the parity blocks.
         */
        this.storageData = new byte[k+m][blockSize];
        for (int i = 0; i < k; i++) {
            System.arraycopy(data[i], 0, storageData[i], 0, blockSize);
        }
        for (int i = 0; i < m; i++) {
            System.arraycopy(parity[i], 0, storageData[i+k], 0, blockSize);
        }

    }

    private void readFile(byte[] data, File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        inputStream.read(data);
        inputStream.close();
    }


    /**
     * Decode a file from a group of byte arrays
     * @param filePath,
     * @param erasures
     */
    public void decode(String filePath, int fileSize, int[] erasures, byte data[][]) {
        File recoverFile = new File(filePath);
        this.origSize = fileSize;
        int blockSize = data[0].length;
        this.storageData = new byte[m+k][blockSize];

        for (int i = 0; i < k+m; i++) {
            System.arraycopy(data[i], 0, storageData[i], 0, blockSize);
        }

        try {

            decode(recoverFile, erasures);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    private void decode(File file, int[] erasures) throws IOException {
        int blockSize = this.storageData[0].length;
        byte[][] newData = new byte[k][blockSize];
        byte[][] newParity = new byte[m][blockSize];

        for(int i = 0; i < k; i++) {
                System.arraycopy(storageData[i], 0, newData[i], 0, blockSize);
        }
        for(int i = 0; i < m; i++) {
                System.arraycopy(storageData[i+k], 0, newParity[i], 0, blockSize);
        }

        coder.decode(erasures, newData, newParity);

        OutputStream outputStream = new FileOutputStream(file, true);
        int writeSize = 0;
        int i = 0;
        while (origSize - writeSize > blockSize) {
            outputStream.write(newData[i]);
            writeSize += blockSize;
            i++;
        }
        outputStream.write(newData[i], 0, origSize - writeSize);
        outputStream.close();
    }
    public static void main(String[] strings) {
        int k = 6;
        int m = 3;
        int codeStyle = 2;
        int wordSize = 8;
        int packetSize = 8;
        try {
            ECHandler test = new ECHandler(k, m, codeStyle,wordSize,packetSize);
            String filepath = System.getProperty("user.dir") + "/pom.xml";
            byte[][] data = null;
            data = test.encode(filepath);

            String outpath = System.getProperty("user.dir") + "/recover_"+"pom.xml";
            int[] erasures = {1, 2, 3};
            ECHandler decodeTest = new ECHandler(k,m,codeStyle,wordSize,packetSize);

            int fileSize = k * data[0].length;
            decodeTest.decode(outpath, fileSize, erasures, data);
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }


}
