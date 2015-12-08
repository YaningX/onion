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
package com.onion.worker;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * A bio socket client.
 */
public class DataClient {
    private InetSocketAddress serverAddress;

    public DataClient(InetSocketAddress serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void send(byte[] sendData) throws IOException {
        Socket socket = new Socket(serverAddress.getAddress(), serverAddress.getPort());
        OutputStream os = socket.getOutputStream();
        /**
         * |--header: 4 bytes--|--content--|
         */
        byte[] dataLength = intToByteArray(4, sendData.length);
        byte[] requestMessage = combineByteArray(dataLength, sendData);
        os.write(requestMessage);
        os.flush();
        os.close();
    }

    private byte[] intToByteArray(int byteLength, int intValue) {
        return ByteBuffer.allocate(byteLength).putInt(intValue).array();
    }

    private byte[] combineByteArray(byte[] array1, byte[] array2) {
        byte[] combinedArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, combinedArray, 0, array1.length);
        System.arraycopy(array2, 0, combinedArray, array1.length, array2.length);
        return combinedArray;
    }

    public static void main(String[] strings) throws IOException {
        RandomAccessFile file = new RandomAccessFile(System.getProperty("user.dir") + "/pom.xml", "rw");
        byte[] inputBytes = new byte[(int) file.length()];
        file.read(inputBytes);
        new DataClient(new InetSocketAddress("127.0.0.1", 8007)).send(inputBytes);
    }

}
