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

package org.apache.zookeeper.server.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

public class ControllerClientServerTest extends ControllerTestBase {
    @Test
    public void verifyPingCommand() {
        assertTrue(commandClient.trySendCommand(ControlCommand.Action.PING));
    }

    @Test
    public void verifyCloseConnectionCommand() {
        // Valid long session ids should be accepted.
        assertTrue(commandClient.trySendCommand(ControlCommand.Action.CLOSECONNECTION, "0x1234"));
        assertTrue(commandClient.trySendCommand(ControlCommand.Action.CLOSECONNECTION, "1234"));

        // Invalid session id format should fail.
        assertFalse(commandClient.trySendCommand(ControlCommand.Action.CLOSECONNECTION, "hanm"));

        // No parameter should be accepted.
        assertTrue(commandClient.trySendCommand(ControlCommand.Action.CLOSECONNECTION));
    }

    @Test
    public void verifyExpireSessionCommand() {
        // Valid long session ids should be accepted.
        assertTrue(commandClient.trySendCommand(ControlCommand.Action.EXPIRESESSION, "0x1234"));

        // Invalid session id format should fail.
        assertFalse(commandClient.trySendCommand(ControlCommand.Action.EXPIRESESSION, "hanm"));

        // No parameter should be accepted.
        assertTrue(commandClient.trySendCommand(ControlCommand.Action.EXPIRESESSION));
    }

    @Test
    public void verifyAddResetDelayCommands() {
        // Valid longs should be parsed.
        assertTrue(commandClient.trySendCommand(ControlCommand.Action.ADDDELAY, "0x1234"));

        // Invalid longs should fail.
        assertFalse(commandClient.trySendCommand(ControlCommand.Action.ADDDELAY, "hanm"));

        // No parameter should be accepted.
        assertTrue(commandClient.trySendCommand(ControlCommand.Action.ADDDELAY));

        // Reset delay should work.
        assertTrue(commandClient.trySendCommand(ControlCommand.Action.RESET));
    }

    @Test
    public void verifyBadResponseCommands() {
        // Valid longs should be parsed.
        assertTrue(commandClient.trySendCommand(ControlCommand.Action.FAILREQUESTS, "0x1234"));

        // Invalid longs should fail.
        assertFalse(commandClient.trySendCommand(ControlCommand.Action.FAILREQUESTS, "hanm"));

        // No parameter should be accepted.
        assertTrue(commandClient.trySendCommand(ControlCommand.Action.FAILREQUESTS));

        // Reset should work.
        assertTrue(commandClient.trySendCommand(ControlCommand.Action.RESET));
    }

    @Test
    public void verifyEatResponseCommands() {
        // Valid longs should be parsed.
        assertTrue(commandClient.trySendCommand(ControlCommand.Action.NORESPONSE, "0x1234"));

        // Invalid longs should fail.
        assertFalse(commandClient.trySendCommand(ControlCommand.Action.NORESPONSE, "hanm"));

        // No parameter should be accepted.
        assertTrue(commandClient.trySendCommand(ControlCommand.Action.NORESPONSE));

        // Reset should work.
        assertTrue(commandClient.trySendCommand(ControlCommand.Action.RESET));
    }

    @Test
    public void verifyLeaderElectionCommand() {
        assertTrue(commandClient.trySendCommand(ControlCommand.Action.ELECTNEWLEADER));
    }

}
