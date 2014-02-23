package ru.mail.chatserver.shared.util;

import ru.mail.chatserver.shared.BufferDecoder;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;

/**
 * User: magic2k
 * Date: 22.09.12
 */
public class ByteBufferSizeHeaderMaker {

    public static ByteBuffer addMessageSize(ByteBuffer bb) {

        int size = bb.array().length;
        ByteBuffer sizeBb = ByteBuffer.allocate(4);
        sizeBb.putInt(size);
        byte[] fullMessage = new byte[size+4];
        System.arraycopy(sizeBb.array(), 0, fullMessage, 0, 4);
        System.arraycopy(bb.array(), 0, fullMessage, 4, size);

        return ByteBuffer.wrap(fullMessage);
    }
}
