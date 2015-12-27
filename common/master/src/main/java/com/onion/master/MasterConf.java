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

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * Master configuration from a master configuration file for init a master.
 */
public class MasterConf {
    private int dataWorkerAmount;
    private int parityWorkerAmount;
    private int wordSize;
    private int packetSize;
    private String erasureCodeType;

    private SAXReader reader = new SAXReader();
    private Document document;
    private Element root;

    private List<InetSocketAddress> workerAddresses = new LinkedList<InetSocketAddress>();

    public MasterConf(File confDir) {
        reader = new SAXReader();
        try {
            document = reader.read(confDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        root = document.getRootElement();
    }

    public List<InetSocketAddress> getWorkerAddresses() throws Exception{
        Element address = root.element("InetAddress");
        List<Element> addList = address.elements();
        for (Element element : addList) {
            String[] addStr = element.getText().split(":");
            if (addStr.length < 2) {
                throw new Exception("The type of InetAddress is no correct!");
            }
            workerAddresses.add(new InetSocketAddress(addStr[0] ,Integer.parseInt(addStr[1])));
        }
        return workerAddresses;
    }

    public int getDataWorkerAmount() {
        dataWorkerAmount = Integer.parseInt(root.element("dataWorkerAmount").getText());
        return dataWorkerAmount;
    }

    public int getWordSize() {
        wordSize = Integer.parseInt(root.element("wordSize").getText());
        return wordSize;
    }

    public int getPacketSize() {
        packetSize = Integer.parseInt(root.element("packetSize").getText());
        return packetSize;
    }

    public int getParityWorkerAmount() {
        parityWorkerAmount = Integer.parseInt(root.element("parityWorkerAmount").getText());
        return parityWorkerAmount;
    }

    public String getErasureCodeType() {
        erasureCodeType = root.element("erasureCodeType").getText();
        return erasureCodeType;
    }
}
