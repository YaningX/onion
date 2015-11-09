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

/**
 * To understand the codebase, it's better to read the manual of Jerasure.
 * There are three methods to implement coding and decoding operations:
 *      1) Matrix-Based Coding;
 *      2) Bit-Matrix Coding;
 *      3) Using a schedule rather than a bit-matrix
 * There are some important parameters: k, m, w (different implementations in the above three methods), packetsize, blocksize. The detailed description is in the manual of Jerasure.
 *
 * In an ErasureCoder interface, there are three operations:
 *      1) encode();
 *      2) decode();
 *      3) repair();
 */
public interface ErasureCoder {
    /**
     * Encodes specified data blocks.
     *
     * @param data The data blocks matrix
     * @return The coding blocks matrix
     */
    public byte[][] encode(byte[][] data);

    /**
     * Decodes specified failed data blocks.
     *
     * @param erasures The failed data blocks list
     * @param data     The data blocks matrix
     * @param coding   The coding blocks matrix
     */
    public void decode(int[] erasures, byte[][] data, byte[][] coding);

    /**
     * Repair the failed block, actually single failure.
     * @param failedBlock The failed block index
     * @param data The existing block set
     */
    public byte[] repair(int failedBlock, byte[][] data);

    /**
     * Naive method to repair the failed blocks, (n - k) failures at most.
     * @param failedBlock The failed block set
     * @param data The existing block set
     */
    public byte[] naiveRepair(int failedBlock, byte[][] data);
}
