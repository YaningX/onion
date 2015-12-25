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

import java.io.*;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Conf {
    public Properties properties = new Properties();

    public void loadConf(String source) {
        loadConf(new File(source));
    }

    public void loadConf(File source) {
        if (source.exists()) {
            try {
                InputStream input = new FileInputStream(source);
                properties.load(input);
                input.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadConf(Map<String, String> props) {
        if (props != null) {
            properties.putAll(props);
        }
    }

    public void loadConf(Properties props) {
        if (props != null) {
            properties.putAll(props);
        }
    }

    public void saveConf(File dest) throws IOException{
        OutputStream out = new FileOutputStream(dest);
        properties.save(out, "store the worker message");
    }

    public void setInt(String key, int value) {
        properties.setProperty(key, String.valueOf(value));
    }

    public int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    public String getString(String key) {
        return properties.getProperty(key);
    }

    public void setString(String key, String value) {
        properties.setProperty(key, value);
    }

    public Set<Object> getKeys() {
        return properties.keySet();
    }

    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }
}
