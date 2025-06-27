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

package org.apache.zookeeper;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import org.apache.zookeeper.common.ZKConfig;

/**
 * @deprecated Use {@link org.apache.zookeeper.common.Login} instead
 */
@Deprecated
public class Login extends org.apache.zookeeper.common.Login {

    @Override
    public Login(final String loginContextName, Supplier<CallbackHandler> callbackHandlerSupplier, final ZKConfig zkConfig) throws LoginException {
        super(loginContextName, callbackHandlerSupplier, zkConfig);
    }

    // this method also visible for unit tests, to make sure kerberos state cleaned up
    protected synchronized void logout() throws LoginException {
        // We need to make sure not to call LoginContext.logout() when we
        // are not logged in. Since Java 9 this could result in an NPE.
        // See ZOOKEEPER-4477 for more details.
        if (subject != null && !subject.getPrincipals().isEmpty()) {
            login.logout();
        }
    }

    // this method is overwritten in unit tests to test concurrency
    protected void sleepBeforeRetryFailedRefresh() throws InterruptedException {
        Thread.sleep(10 * 1000);
    }
}
