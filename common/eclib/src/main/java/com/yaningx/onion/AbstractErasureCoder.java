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
 * Abstract class to implement ErasureCoder.
 */
public class AbstractErasureCoder implements ErasureCoder {

    /** {@inheritDoc} */
    public byte[][] encode(byte[][] data) {
        return new byte[0][];
    }

    /** {@inheritDoc} */
    public void decode(int[] erasures, byte[][] data, byte[][] coding) {

    }

    /** {@inheritDoc} */
    public byte[] repair(int failedBlock, byte[][] data) {
        return new byte[0];
    }

    /** {@inheritDoc} */
    public byte[] naiveRepair(int failedBlock, byte[][] data) {
        return new byte[0];
    }
}
