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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.common.ConfigException;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ControllerConfigTest {
    File configFile;

    private static final int AnyTickTime = 1234;
    private static final int AnyPort = 1234;
    private static final String AnyDataDir = "temp";

    public static File createTempFile() throws IOException {
        return File.createTempFile("temp", "cfg", new File(System.getProperty("user.dir")));
    }

    public static List<Integer> findNAvailablePorts(int n) throws IOException {
        List<ServerSocket> openedSockets = new ArrayList<>();
        List<Integer> ports = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            ServerSocket randomSocket = new ServerSocket(0);
            openedSockets.add(randomSocket);
            ports.add(randomSocket.getLocalPort());
        }

        for (ServerSocket s : openedSockets) {
            s.close();
        }

        return ports;
    }

    public static void writeRequiredControllerConfig(File file, int controllerPort, int zkServerPort, int adminServerPort) throws IOException {
        PrintWriter writer = new PrintWriter(file);
        writer.write("dataDir=anywhere\n");
        writer.write("controllerPort=" + controllerPort + "\n");
        writer.write("clientPort=" + zkServerPort + "\n");
        writer.write("adminPort=" + adminServerPort + "\n");
        writer.close();
    }

    @BeforeEach
    public void init() throws IOException {
        configFile = createTempFile();
    }

    private void writeFile(int portNumber) throws IOException {
        FileWriter writer = new FileWriter(configFile);
        writer.write("dataDir=somewhere\n");
        writer.write("ignore=me\n");
        writer.write("tickTime=" + AnyTickTime + "\n");
        writer.write("controllerPort=" + portNumber + "\n");
        writer.write("clientPort=" + portNumber + "\n");
        writer.flush();
        writer.close();
    }

    @AfterEach
    public void cleanup() {
        if (configFile != null) {
            configFile.delete();
        }
    }

    @Test
    public void parseFileSucceeds() throws Exception {
        writeFile(AnyPort);
        ControllerServerConfig config = new ControllerServerConfig(configFile.getAbsolutePath());
        assertEquals(AnyPort, config.getControllerAddress().getPort());
        assertEquals(AnyPort, config.getClientPortAddress().getPort());
        assertEquals(AnyTickTime, config.getTickTime());
    }

    @Test
    public void parseFileFailsWithMissingPort() throws Exception {
        FileWriter writer = new FileWriter(configFile);
        writer.write("dataDir=somewhere\n");
        writer.flush();
        writer.close();
        try {
            ControllerServerConfig config = new ControllerServerConfig(configFile.getAbsolutePath());
            fail("Should have thrown with missing server config");
        } catch (ConfigException ex) {
        }
    }

    @Test public void parseMissingFileThrows() {
        try {
            ControllerServerConfig config = new ControllerServerConfig("DontLookHere.missing");
            fail("should have thrown");
        } catch (ConfigException ex) {
        }
    }

    @Test
    public void parseInvalidPortThrows()throws ConfigException {
        try {
            ControllerServerConfig config = new ControllerServerConfig(configFile.getAbsolutePath());
            fail("should have thrown");
        } catch (ConfigException ex) {
        }
    }

    @Test
    public void validCtor() {
        ControllerServerConfig config = new ControllerServerConfig(AnyPort, AnyPort, AnyDataDir);
        assertEquals(AnyPort, config.getControllerAddress().getPort());
        assertEquals(AnyPort, config.getClientPortAddress().getPort());
        assertEquals(AnyDataDir, config.getDataDir().getName());
    }

    @Test
    public void invalidCtor() {
        try {
            ControllerServerConfig config = new ControllerServerConfig(-10, -10, "no where");
            fail("should have thrown");
        } catch (IllegalArgumentException ex) {
        }

    }
}
