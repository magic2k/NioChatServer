package ru.mail.chatserver.server;

import org.json.JSONObject;
import ru.mail.chatserver.shared.BufferDecoder;
import ru.mail.chatserver.shared.util.ByteBufferSizeHeaderMaker;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.charset.CharacterCodingException;

/**
 * User: v.kolosov
 * Date: 21.09.12
 * Time: 12:35
 * Сообщение на отправку от обработчика сообщений для сервера.
 * Состоит из ключа получателя сообщения и байт-буфера с сообщением.
 */
public class OutboundData {

    private SelectionKey recipientSelectionKey;
    private ByteBuffer outMessageByteBuffer;


    public OutboundData(SelectionKey recipient, JSONObject outMessageJson) throws CharacterCodingException {
        formOutboundData(recipient, outMessageJson);
    }

    private void formOutboundData(SelectionKey recipient, JSONObject outMessageJson) throws CharacterCodingException {

        recipientSelectionKey = recipient;
        BufferDecoder bufferDecoder = new BufferDecoder();
        ByteBuffer outMessageByteBufferWithoutSize = bufferDecoder.jsonToByteBuffer(outMessageJson);
        outMessageByteBuffer = ByteBufferSizeHeaderMaker.addMessageSize(outMessageByteBufferWithoutSize);
    }


    public SelectionKey getRecipientSelectionKey() {
        return recipientSelectionKey;
    }


    public ByteBuffer getOutMessageByteBuffer() {
        return outMessageByteBuffer;
    }
}
