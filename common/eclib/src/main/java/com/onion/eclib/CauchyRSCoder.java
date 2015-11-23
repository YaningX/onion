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

import com.sun.jna.Pointer;

/**
 * A Cauchy Reed Solomon Code implementation.
 */
public class CauchyRSCoder extends AbstractErasureCoder {
    private int packetSize;

    public CauchyRSCoder(int dataBlockNum, int parityBlockNum, int wordSize, int packetSize) {
        super(dataBlockNum, parityBlockNum, wordSize);
        this.packetSize = packetSize;
    }

    @Override
    protected void doEncode(Pointer[] dataPointer, Pointer[] parityPointer,
                            int dataBlockNum, int parityBlockNum, int wordSize, int blockSize) {
        int[] matrix = JerasureLibrary.INSTANCE.
                cauchy_original_coding_matrix(dataBlockNum, parityBlockNum, wordSize).
                getIntArray(0, dataBlockNum * parityBlockNum);
        int[] bitMatrix = JerasureLibrary.INSTANCE.
                jerasure_matrix_to_bitmatrix(dataBlockNum, parityBlockNum, wordSize, matrix).
                getIntArray(0, dataBlockNum * wordSize * parityBlockNum * wordSize);
        Pointer[] schedule = JerasureLibrary.INSTANCE.
                jerasure_smart_bitmatrix_to_schedule(dataBlockNum, parityBlockNum, wordSize, bitMatrix);
        JerasureLibrary.INSTANCE.
                jerasure_schedule_encode(dataBlockNum, parityBlockNum,
                        wordSize, schedule, dataPointer, parityPointer, blockSize, packetSize);

    }

    @Override
    protected boolean doDecode(Pointer[] dataPointer, Pointer[] parityPointer, int[] jerasures,
                               int dataBlockNum, int parityBlockNum, int wordSize, int blockSize) {
        return false;
    }

}
