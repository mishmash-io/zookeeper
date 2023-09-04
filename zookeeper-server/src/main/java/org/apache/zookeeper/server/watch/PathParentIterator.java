package org.apache.zookeeper.server.watch;

/**
 * @deprecated Use {@link org.apache.zookeeper.common.PathParentIterator} instead
 */
@Deprecated
public class PathParentIterator extends org.apache.zookeeper.common.PathParentIterator {

    protected PathParentIterator(String path, int maxLevel) {
        super(path, maxLevel);
    }

}
