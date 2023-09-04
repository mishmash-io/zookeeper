package org.apache.zookeeper.server;

/**
 * @deprecated Use {@link org.apache.zookeeper.common.ZooKeeperThread} instead
 */
@Deprecated
public class ZooKeeperThread extends org.apache.zookeeper.common.ZooKeeperThread {

    public ZooKeeperThread(String threadName) {
        super(threadName);
    }

}
