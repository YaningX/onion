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

import com.onion.master.command.MasterCommand;
import com.onion.master.command.MasterDeleteCommand;
import com.onion.master.command.MasterReadCommand;
import com.onion.master.command.MasterWriteCommand;

import java.io.File;
import java.util.Scanner;

/**
 * This is onion master command line tool.
 */
public class MasterTool {
        private static File confDir;

        private static final String PROMPT = MasterTool.class.getSimpleName() + ".local";

        private static final String REQUEST_LIST = "Available " + PROMPT + " requests:\n"
                + "?                        List all available requests.\n"
                + "\n"
                + "write  filename\n"
                + "                         Write a file to onion erasure system\n"
                + "read, file\n"
                + "                         Read a file from onion\n"
                + "quit, exit, q            Exit program.";

        private static final String USAGE = ("Usage: sh bin/onion.sh")
                + " [conf_dir]\n"
                + "\n";

        private static void printUsage(String error) {
                System.err.println(error + "\n");
                System.err.println(USAGE);
                System.exit(-1);
        }


        private static void execute(Master master, String command) {
                //Omit the leading and trailing whitespace.
                command = command.trim();
                if (command.equals("list_requests")
                        || command.equals("?")) {
                        System.out.println(REQUEST_LIST);
                        return;
                }

                MasterCommand executor = null;
                if (command.startsWith("write")) {
                    executor = new MasterWriteCommand();
                } else if (command.startsWith("read")) {
                    executor = new MasterReadCommand();
                } else if (command.startsWith("delete")) {
                    executor = new MasterDeleteCommand();
                }

                if (executor == null) {
                        System.out.println("Unknown request \"" + command + "\". Type \"?\" for a request list.");
                        return;
                }
                executor.execute(master, command);
        }

        private static File getConfDir(String[] args) {
                confDir = new File(args[0]);
                //Set the default conf directory
                if (confDir == null || !confDir.exists()) {
                        confDir = new File("/etc/onion/");
                }

                if (!confDir.exists()) {
                        System.err.println("Can not locate master configuration directory");
                        throw new RuntimeException("Can not locate master configuration directory "
                                + confDir.getAbsolutePath());
                }

                return confDir;
        }

        public static void main(String[] args) throws Exception{
                if (args.length < 1) {
                        System.err.println(USAGE);
                        return;
                }

                Master master = new Master(getConfDir(args));
                System.out.println(REQUEST_LIST);
                Scanner scanner = new Scanner(System.in, "UTF-8");
                String input = scanner.nextLine();
                while (!(input.equals("quit") || input.equals("exit")
                        || input.equals("q"))) {
                        execute(master, input);
                        System.out.print(PROMPT + ": ");
                        input = scanner.nextLine();
                }

        }


}
