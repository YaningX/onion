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
package com.onion.worker;


import com.onion.conf.Conf;

import java.io.File;
import java.net.InetSocketAddress;

public class Worker {
    private InetSocketAddress workerAddress;
    private String backendDir;
    private WorkerDataServer workerDataServer;
    private Conf workerConf;

    public Worker(String confPath) {
        this(new File(confPath));
    }

    public Worker(File confDir) {
        workerConf = new Conf();
        workerConf.loadConf(confDir);
        backendDir = workerConf.getString("backendDir");
        if (!new File(backendDir).exists()) {
            new File(backendDir).mkdirs();
        }
        workerAddress = new InetSocketAddress(workerConf.getInt("port"));
        this.workerDataServer = new WorkerDataServer(workerAddress, backendDir);
    }

    public Worker(InetSocketAddress workerAddress, String backendDir) {
        this.workerAddress = workerAddress;
        this.backendDir = backendDir;
        this.workerDataServer = new WorkerDataServer(workerAddress, backendDir);
    }

    public InetSocketAddress getWorkerAddress() {
        return workerAddress;
    }

    public String getBackendDir() {
        return backendDir;
    }

    public static void main(String[] args) {
        String confPath = args[0];
        Worker worker = new Worker(confPath);
        worker.process();
    }

    public void process() {
        workerDataServer.start();
    }

}
