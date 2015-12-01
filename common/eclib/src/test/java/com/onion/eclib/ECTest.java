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

package com.onion.eclib;

import com.google.common.base.Preconditions;
import org.junit.Test;

import java.io.*;
import java.util.*;

public class ECTest {
    private ErasureCoder coder;
    private int k;
    private int m;
    private int wordSize;
    private int packetSize;

    /**
     * Suppose that the size of original file is not larger than 32GB.
     * If the size is larger than 32GB, it is to be implemented by being divided into several pieces.
     * This is not done in this test, but will be done in the main code base.
     * @param oriFile
     * @throws IOException
     */
    private void runWith(File backupDir, File oriFile) throws IOException {
        long dataSize = oriFile.length();
        Preconditions.checkArgument(dataSize <= Integer.MAX_VALUE, "The original file is too large.");
        int origSize = (int) dataSize;
        int blockSize = (int) dataSize / k;
//        int wholeSize = blockSize * k;
    /*    if (dataSize != wholeSize) {
            blockSize = ;
            wholeSize = blockSize * k;
        }*/
        blockSize = (blockSize / (packetSize * wordSize + 1) + 1) * (packetSize * wordSize);
        int wholeSize = blockSize * k;
        /**
         *  Read data from a file into a two-dimension array.
         */
        byte[][] data = new byte[k][blockSize];
        byte[] wholeData = new byte[wholeSize];
        readFile(wholeData, oriFile);
        Arrays.fill(wholeData, (int) dataSize, wholeSize - 1, (byte) 0);
        for (int i = 0; i < k; i++) {
            System.arraycopy(wholeData, i * blockSize, data[i], 0, blockSize);
        }

        /**
         * Encode and generate the parity blocks.
         */
        byte[][] parity = coder.encode(data);

        /**
         * Write data blocks into files.
         */
        for (int i = 0; i < k; i++) {
            writeFile(data[i], backupDir, oriFile.getName() + "_k" + (i + 1));
        }

        /**
         * Write parity blocks into files.
         */
        for (int i = 0; i < m; i++) {
            writeFile(parity[i], backupDir, oriFile.getName() + "_m" + (i + 1));
        }

        /**
         * Delete some files
         */
        deleteFile(generateRadomArray(m), backupDir, oriFile);

        byte[][] newData = new byte[k][blockSize];
        byte[][] newParity = new byte[m][blockSize];
        int[] erasures = checkAndLoadFile(newData, newParity, backupDir, oriFile);
        coder.decode(erasures, newData, newParity);
        writeRecoverFile(newData, backupDir, oriFile, origSize);
    }

    private void writeRecoverFile(byte[][] newData, File backupFile, File oriFile, int origSize) throws IOException {
        File recoverFile = new File(backupFile, "recovered" + oriFile.getName());
        if (recoverFile.exists()) {
            if (!recoverFile.delete()) {
                throw new IOException("Recovered file exists before recovering!");
            }
        }

        OutputStream outputStream = new FileOutputStream(recoverFile, true);
        int blockSize = newData[0].length;
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
    private int[] generateRadomArray(int ArrayLen) {
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

    private void deleteFile(int[] deletes, File backupDir, File oriFile) throws IOException {
        File deleteFile;
        for (int i = 0; i < deletes.length; i++) {
            if (deletes[i] < k) {
                deleteFile = new File(backupDir, oriFile.getName() + "_k" + deletes[i]);
            } else {
                deleteFile = new File(backupDir, oriFile.getName() + "_m" + (deletes[i] - k + 1));
            }
            if (deleteFile.exists()) {
                if (!deleteFile.delete()) {
                    throw new IOException();
                }
            }
        }
    }

    private int[] checkAndLoadFile(byte[][] data, byte[][] parity, File backupDir, File oriFile) throws IOException {
        List<Integer> erasureList = new LinkedList<Integer>();
        for (int i = 0; i < k; i++) {
            File dataFile = new File(backupDir, oriFile.getName() + "_k" + (i + 1));
            if (dataFile.exists()) {
                readFile(data[i], dataFile);
            } else {
                erasureList.add(i);
            }
        }

        for (int i = 0; i < m; i++) {
            File parityFile = new File(backupDir, oriFile.getName() + "_m" + (i + 1));
            if (parityFile.exists()) {
                readFile(parity[i], parityFile);
            } else {
                erasureList.add(i + k);
            }
        }

        //Return erasures array.
        int[] erasures = new int[erasureList.size()];
        Iterator<Integer> iterator = erasureList.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            erasures[i++] = iterator.next();
        }
        return erasures;
    }

    private void writeFile(byte[] data, File backupDir, String fileName) throws IOException {
        if (!backupDir.exists() && !backupDir.mkdir()) {
            throw new IOException("Cannot make backup directory");
        }
        File file = new File(backupDir, fileName);
        OutputStream outputStream = new FileOutputStream(file);
        outputStream.write(data);
        outputStream.close();
    }


    private void readFile(byte[] data, File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        inputStream.read(data);
        inputStream.close();
    }

    @Test
    public void vandermonTest() throws IOException {
        this.k = 6;
        this.m = 3;
        this.wordSize = 8;
        this.packetSize = 8;
<<<<<<< HEAD
        this.coder = new VandermondeRSCoder(k, m, wordSize);
=======

        this.coder = new VandermondeRSCoder(k, m, wordSize);
//        this.coder = new CauchyRSCoder(k, m, wordSize, packetSize);
//        this.coder = new CauchyGoodRSCoder(k, m, wordSize, packetSize);
>>>>>>> dev

        System.out.println(System.getProperty("user.dir"));
        File backupDir = new File(System.getProperty("user.dir") + "/target/backup");
        File oriFile = new File(System.getProperty("user.dir") + "/pom.xml");
        runWith(backupDir, oriFile);
    }
    @Test
    public void CauchyRSCoder() throws IOException {
        this.k = 6;
        this.m = 3;
        this.wordSize = 8;
        this.packetSize = 8;
        this.coder = new CauchyRSCoder(k, m, wordSize, packetSize);

<<<<<<< HEAD
        System.out.println(System.getProperty("user.dir"));
        File backupDir = new File(System.getProperty("user.dir") + "/target/backup");
=======
        /**
         * Write data blocks into files.
         */
        File backupDir = new File("/home/gkq/IdeaProjects/onion/backup");
>>>>>>> dev
        File oriFile = new File(System.getProperty("user.dir") + "/pom.xml");
        runWith(backupDir, oriFile);
    }
    @Test
    public void  CauchyGoodRSCoder() throws IOException {
        this.k = 6;
        this.m = 3;
        this.wordSize = 8;
        this.packetSize = 8;
        this.coder = new CauchyGoodRSCoder(k, m, wordSize, packetSize);

        System.out.println(System.getProperty("user.dir"));
        File backupDir = new File(System.getProperty("user.dir") + "/target/backup");
        File oriFile = new File(System.getProperty("user.dir") + "/pom.xml");
        runWith(backupDir, oriFile);
    }


}
