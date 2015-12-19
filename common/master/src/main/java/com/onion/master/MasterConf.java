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

import java.io.File;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * Master configuration from a master configuration file for init a master.
 */
public class MasterConf {
    private File confDir;
    private int dataWorkerAmount;
    private int parityWorkerAmount;
    private int wordSize;
    private int packetSize;
    private String erasureCodeType;

    private List<InetSocketAddress> workerAddresses = new LinkedList<InetSocketAddress>();

    public MasterConf(File confDir) {
        this.confDir = confDir;
    }

    private void init() {

    }

    public List<InetSocketAddress> getWorkerAddresses() {
        //TODO
        return workerAddresses;
    }

    public int getDataWorkerAmount() {
        //TODO
        return dataWorkerAmount;
    }

    public int getWordSize() {
        //TODO
        return wordSize;
    }

    public int getPacketSize() {
        //TODO
        return packetSize;
    }

    public int getParityWorkerAmount() {
        //TODO
        return parityWorkerAmount;
    }

    public String getErasureCodeType() {
        //TODO
        return erasureCodeType;
    }

}
