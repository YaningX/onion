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

public class MasterWriteCommand extends MasterCommand {
    @Override
    public void execute(Master master, String input) {
        String[] inputSet = input.split(" ");
        if (inputSet.length != 2) {
            System.out.println("Please input the correct write command.");
        }

        try {
            long time0 = System.currentTimeMillis();
            long id = master.write(inputSet[1]);
            System.out.println("Write success.");
            System.out.println("BlockId:   " + id);
            long time1 = System.currentTimeMillis();
            System.out.println("Writing time: " + (time1 - time0)+ " milliseconds");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Write failed!");
        }
    }
}
