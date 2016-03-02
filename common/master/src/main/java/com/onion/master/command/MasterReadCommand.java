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
package com.onion.master.command;

import com.onion.master.Master;

public class MasterReadCommand extends MasterCommand {
    @Override
    public void execute(Master master, String input) {
        String[] inputSet = input.split(" ");
        if (inputSet.length != 3) {
            System.out.println("Please input correct read command.");
        }
        //the function read has been updated in class Master;
        boolean result = master.read(inputSet[0]);
        if (result == false) {
            System.out.println("Read failed.");
        } else {
            System.out.println("Read success.");
        }
    }
}
