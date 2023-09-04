package org.apache.zookeeper;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import org.apache.zookeeper.common.ZKConfig;

/**
 * @deprecated Use {@link org.apache.zookeeper.common.Login} instead
 */
@Deprecated
public class Login extends org.apache.zookeeper.common.Login {

    public Login(String loginContextNameKey, String loginContextName, CallbackHandler callbackHandler,
            ZKConfig zkConfig) throws LoginException {
        super(loginContextNameKey, loginContextName, callbackHandler, zkConfig);
    }

}
