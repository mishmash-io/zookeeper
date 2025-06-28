/*
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

package org.apache.zookeeper.server;

import javax.security.auth.Subject;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

import org.apache.zookeeper.common.Login;
import org.apache.zookeeper.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZooKeeperSaslServer {

    /**
     * Configures a SASL mechanism.
     *
     * If not set will use the default 'DIGEST-MD5' when no Principal is present on the
     * logged in Subject; or 'GSSAPI' (Kerberos) if a Principal is present.
     */
    public static final String SASL_MECHANISM_NAME = "zookeeper.sasl.server.mechanism";

    private static final Logger LOG = LoggerFactory.getLogger(ZooKeeperSaslServer.class);
    private SaslServer saslServer;

    ZooKeeperSaslServer(final Login login) {
        saslServer = createSaslServer(login);
    }

    private SaslServer createSaslServer(final Login login) {
        synchronized (login) {
            Subject subject = login.getSubject();
            return SecurityUtils.createSaslServer(
                    subject,
                    "zookeeper",
                    "zk-sasl-md5",
                    login.newCallbackHandler(),
                    LOG,
                    System.getProperty(SASL_MECHANISM_NAME));
        }
    }

    public byte[] evaluateResponse(byte[] response) throws SaslException {
        return saslServer.evaluateResponse(response);
    }

    public boolean isComplete() {
        return saslServer.isComplete();
    }

    public String getAuthorizationID() {
        return saslServer.getAuthorizationID();
    }

}




