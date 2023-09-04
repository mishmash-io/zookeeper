package org.apache.zookeeper.server;

import java.nio.ByteBuffer;

/**
 * @deprecated Use {@link org.apache.zookeeper.common.ByteBufferOutputStream} instead
 */
@Deprecated
public class ByteBufferOutputStream extends org.apache.zookeeper.common.ByteBufferOutputStream {

    public ByteBufferOutputStream(ByteBuffer bb) {
        super(bb);
    }

}
