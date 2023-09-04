package org.apache.zookeeper.server;

import java.nio.ByteBuffer;

/**
 * @deprecated Use {@link org.apache.zookeeper.common.ByteBufferInputStream} instead
 */
@Deprecated
public class ByteBufferInputStream extends org.apache.zookeeper.common.ByteBufferInputStream {

    public ByteBufferInputStream(ByteBuffer bb) {
        super(bb);
    }

}
