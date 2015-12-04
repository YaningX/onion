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
package com.onion.worker.net;

import java.util.HashMap;
import java.util.Map;

public class RPCResponse {
    // The possible types of status for RPC responses.
    public enum Status {
        // Success.
        SUCCESS(0),
        // Generic errors.
        UNEXPECTED_STATUS_CODE(1),
        DECODE_ERROR(2),
        UNKNOWN_MESSAGE_ERROR(3),
        // Specific errors.
        FILE_DNE(100),
        BLOCK_LOCK_ERROR(101),
        WRITE_ERROR(102);

        private static final String DEFAULT_ERROR_STRING = "Unknown error.";
        private static final Map<Short, Status> SHORT_TO_STATUS_MAP = new HashMap<Short, Status>();
        private static final Map<Status, String> STATUS_TO_MESSAGE_MAP = new HashMap<Status, String>();
        static {
            // Populate the mappings.
            for (Status status : Status.values()) {
                SHORT_TO_STATUS_MAP.put(status.getId(), status);
                STATUS_TO_MESSAGE_MAP.put(status, statusToMessage(status));
            }
        }

        private final short mId;

        Status(int id) {
            mId = (short) id;
        }

        /**
         * Returns the short identifier of the status.
         *
         * @return the short representing the status
         */
        public short getId() {
            return mId;
        }

        /**
         * Returns the message for the status.
         *
         * @return A string representing the status
         */
        public String getMessage() {
            String message = STATUS_TO_MESSAGE_MAP.get(this);
            if (message == null) {
                return DEFAULT_ERROR_STRING;
            }
            return message;
        }

        public static Status fromShort(short id) {
            Status status = SHORT_TO_STATUS_MAP.get(id);
            if (status == null) {
                return UNEXPECTED_STATUS_CODE;
            }
            return status;
        }

        /**
         * This method must be updated when a new Status is added.
         */
        private static String statusToMessage(Status status) {
            switch (status) {
                case SUCCESS:
                    return "Success!";
                case UNEXPECTED_STATUS_CODE:
                    return "There was an unexpected status code.";
                case DECODE_ERROR:
                    return "Decode error. Possible Client/DataServer version incompatibility.";
                case UNKNOWN_MESSAGE_ERROR:
                    return "Unknown message error. Possible Client/DataServer version incompatibility.";
                case FILE_DNE:
                    return "File does not exist.";
                case BLOCK_LOCK_ERROR:
                    return "Failed to lock block.";
                case WRITE_ERROR:
                    return "Failed to write block.";
                default:
                    return DEFAULT_ERROR_STRING;
            }
        }
    }
}
