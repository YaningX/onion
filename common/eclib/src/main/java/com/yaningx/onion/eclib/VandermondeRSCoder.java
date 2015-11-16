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
package com.yaningx.onion.eclib;

import com.google.common.base.Preconditions;
import com.sun.jna.Pointer;

/**
 * A vandermonde Reed Solomon code implementation.
 */
public class VandermondeRSCoder extends AbstractErasureCoder {
    private int[] vandermondMatrix;

    public VandermondeRSCoder(int dataBlockNum, int parityBlockNum, int wordSize) {
        super(dataBlockNum, parityBlockNum, wordSize);
        Preconditions.checkArgument(wordSize == 8 || wordSize == 16 ||
                wordSize == 32, "For Matrix-Based Coding, wordSize must be 8, 16 or 32.");
        this.vandermondMatrix = JerasureLibrary.INSTANCE.
                reed_sol_vandermonde_coding_matrix(dataBlockNum, parityBlockNum, wordSize).
        getIntArray(0, dataBlockNum * parityBlockNum);
    }

    @Override
    protected void doEncode(Pointer[] dataPointer, Pointer[] parityPointer,
                            int dataBlockNum, int parityBlockNum, int wordSize, int blockSize) {
        JerasureLibrary.INSTANCE.jerasure_matrix_encode(dataBlockNum,
                parityBlockNum, wordSize, vandermondMatrix,
                dataPointer, parityPointer, blockSize);
    }

    @Override
    protected boolean doDecode(Pointer[] dataPointer, Pointer[] parityPointer, int[] jerasures,
                               int dataBlockNum, int parityBlockNum, int wordSize, int blockSize) {
        int ret = JerasureLibrary.INSTANCE.jerasure_matrix_decode(dataBlockNum, parityBlockNum, wordSize,
                vandermondMatrix, 1, jerasures,
                dataPointer, parityPointer, blockSize);
        return ret == 0 ? true : false;
    }
}
