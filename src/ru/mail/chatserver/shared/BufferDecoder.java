package ru.mail.chatserver.shared;

import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * User: magic2k
 * Date: 08.09.12
 * Кодирует и декодирует байтовый буфер в символьный и наоборот (charset UTF8).
 */

public class BufferDecoder {
    private Charset charset;
    private CharsetDecoder decoder;
    private CharsetEncoder encoder;

    public BufferDecoder() {
        charset = Charset.forName("UTF-8");
        decoder = charset.newDecoder();
        encoder = charset.newEncoder();
        decoder.reset();
        encoder.reset();
    }

    public CharBuffer byteBufferToCharBuffer(ByteBuffer byteBuffer) throws CharacterCodingException {
        decoder.reset();

        return decoder.decode(byteBuffer);
    }

    public ByteBuffer charBufferToByteBuffer(CharBuffer charBuffer) throws CharacterCodingException {
        decoder.reset();

        return encoder.encode(charBuffer);
    }

    public ByteBuffer jsonToByteBuffer(JSONObject jsonObject) throws CharacterCodingException {

        return ByteBuffer.wrap(jsonObject.toString().getBytes(charset));
    }
}
