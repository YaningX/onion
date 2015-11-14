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
package com.yaningx.onion;

import com.google.common.base.Preconditions;
import com.sun.jna.Pointer;

/**
 * Abstract class to implement ErasureCoder.
 */
public abstract class AbstractErasureCoder implements ErasureCoder {
    private int dataBlockNum;
    private int parityBlockNum;
    private int wordSize;

    public AbstractErasureCoder(int dataBlockNum, int parityBlockNum, int wordSize) {
        Preconditions.checkArgument(dataBlockNum > 0);
        Preconditions.checkArgument(parityBlockNum > 0);
        this.dataBlockNum = dataBlockNum;
        this.parityBlockNum = parityBlockNum;
        this.wordSize = wordSize;
    }


    /**
     * There are several coding techniques, and this method is to be implemented by subclass
     */
    protected abstract void doEncode(Pointer[] dataPointer, Pointer[] parityPointer,
                                  int dataBlockNum, int parityBlockNum, int wordSize, int blockSize);

    /** {@inheritDoc} */
    public byte[][] encode(byte[][] data) {
        Preconditions.checkArgument(data.length > 0);
        Pointer[] dataPointer = ECUtils.toPointerArray(data);
        int blockSize = data[0].length;
        byte[][] parity = new byte[parityBlockNum][blockSize];
        Pointer[] parityPointer = ECUtils.toPointerArray(parity);
        doEncode(dataPointer, parityPointer, dataBlockNum, parityBlockNum, wordSize, blockSize);
        ECUtils.toByteArray(parityPointer, parity);
        return parity;
    }

    /**
     * There are several decoding techniques, and this method is to be implemented by subclass
     */
    protected abstract boolean doDecode(Pointer[] dataPointer, Pointer[] parityPointer, int[] erasures,
                                        int dataBlockNum, int parityBlockNum, int wordSize, int blockSize);

    /** {@inheritDoc} */
    public void decode(int[] erasures, byte[][] data, byte[][] parity) {
        Preconditions.checkArgument(data.length > 0);

        Pointer[] dataPointer = ECUtils.toPointerArray(data);
        Pointer[] parityPointer = ECUtils.toPointerArray(parity);

        int blockSize = data[0].length;

        boolean ret = doDecode(dataPointer, parityPointer,
                erasures, dataBlockNum, parityBlockNum, wordSize, blockSize);
        if (ret == true) {

        } else {

        }
    }

    /** {@inheritDoc} */
    public byte[] repair(int failedBlock, byte[][] data) {
        //TODO
        return new byte[0];
    }

    /** {@inheritDoc} */
    public byte[] naiveRepair(int failedBlock, byte[][] data) {
        //TODO
        return new byte[0];
    }
}
