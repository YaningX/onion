/**
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package com.onion.conf;

import java.io.File;
import java.util.Map;

public class ConfigLoader {
    private File conf;
    private Map<String, String> map;

    public ConfigLoader(String confPath) {
        this(new File(confPath));
    }

    public ConfigLoader(File conf) {
        this.conf = conf;
    }

    public String getString(String name) {
        return null;
    }

    public String getString(String name, String defaultValue) {
        return null;
    }

    public Integer getInt(String name) {
        return null;
    }

    public Integer getInt(String name, int defaultValue) {
        return null;
    }
}
