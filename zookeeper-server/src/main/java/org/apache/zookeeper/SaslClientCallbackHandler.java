package org.apache.zookeeper;

/**
 * @deprecated Use {@link org.apache.zookeeper.util.SaslClientCallbackHandler} instead
 */
@Deprecated
public class SaslClientCallbackHandler extends org.apache.zookeeper.util.SaslClientCallbackHandler {

    public SaslClientCallbackHandler(String password, String client) {
        super(password, client);
    }

}
