package ru.mail.chatserver.shared.util;

import org.json.JSONException;
import org.json.JSONObject;
import ru.mail.chatserver.shared.BufferDecoder;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;

/**
 * User: magic2k
 * Date: 22.09.12
 */
public class InboundMessageReceiver {

    private int inboundMessageLength = 0;
    private ByteBuffer inMessageByteBuffer;
    private CharBuffer inCharBuffer;
    private BufferDecoder bufferDecoder;


    public InboundMessageReceiver() {
        bufferDecoder = new BufferDecoder();
        inMessageByteBuffer = ByteBuffer.allocate(2048);
    }


    public JSONObject checkInboundBuffer(ByteBuffer inByteBuffer) throws JSONException {

        if (inboundMessageLength == 0) {

            if (inByteBuffer.position() < 4) {

                return null;
            } else {
                inboundMessageLength = getInboundMessageSize(inByteBuffer);
                processInBuffer(inByteBuffer);
                return convertToJson();
            }

        } else {
            processInBuffer(inByteBuffer);
            return convertToJson();
        }
    }


    private void processInBuffer(ByteBuffer inByteBuffer) throws JSONException {
        if (inboundMessageLength > inByteBuffer.position()) {

            return;
        } else {

            inMessageByteBuffer.put(inByteBuffer.array(), 0, inboundMessageLength);
            decodeReceivedMessage(inMessageByteBuffer);

            inByteBuffer.flip();
            inByteBuffer.position(inboundMessageLength);
            inByteBuffer.compact();
            inMessageByteBuffer.clear();
            inboundMessageLength = 0;
        }
    }


    private int getInboundMessageSize(ByteBuffer inByteBuffer) {
        inByteBuffer.flip();
        int messageSize = inByteBuffer.getInt();
        inByteBuffer.compact();

        return messageSize;
    }


    private void decodeReceivedMessage(ByteBuffer inboundMessageByteBuffer) {

        try {
            inboundMessageByteBuffer.flip();
            inCharBuffer = bufferDecoder.byteBufferToCharBuffer(inboundMessageByteBuffer);
//            System.out.println("msg: " + new String(inCharBuffer.toString()) );

        } catch (CharacterCodingException e) {
            throw new IllegalStateException(e);
        }

    }


    private JSONObject convertToJson() throws JSONException {

        if (inCharBuffer == null) {
            return null;
        }
        JSONObject jsonReceivedMsg = new JSONObject(inCharBuffer.toString());
        inCharBuffer.clear();
        return jsonReceivedMsg;
    }

}
