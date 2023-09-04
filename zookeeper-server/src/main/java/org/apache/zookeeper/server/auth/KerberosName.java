package org.apache.zookeeper.server.auth;

/**
 * @deprecated Use {@link org.apache.zookeeper.util.KerberosName} instead
 */
@Deprecated
public class KerberosName extends org.apache.zookeeper.util.KerberosName {

    public KerberosName(String name) {
        super(name);
    }

}
