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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Onion master to handle storage affairs.
 */
public class Master {
    //Map input file to storage node file.
    private Map<String, List<Integer>> storageMap = new HashMap<String, List<Integer>>();
    private MasterConf masterConf;
    private List<InetSocketAddress> workerAddresses;
    private int dataWorkerAmount;
    private int parityWorkerAmount;

    public Master(File confDir) {
        masterConf = new MasterConf(confDir);
        workerAddresses = masterConf.getWorkerAddresses();
        dataWorkerAmount = masterConf.getDataWorkerAmount();
        parityWorkerAmount = masterConf.getParityWorkerAmount();
    }

    public boolean write(File inputFile) {
        //TODO
        return false;
    }

    public boolean read(File inputFile) {
        //TODO
        return false;
    }

    public boolean delete(File inputFile) {
        //TODO
        return false;
    }

}
