package ru.mail.chatserver.client;

import org.json.JSONException;
import org.json.JSONObject;
import ru.mail.chatserver.shared.BufferDecoder;
import ru.mail.chatserver.shared.util.InboundMessageReceiver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.Date;

/**
 * User: admin
 * Date: 15.09.12
 * Time: 15:38
 */
public class RequestsHandler {

    private ByteBuffer inByteBuffer;
    private InboundMessageReceiver inboundMessageReceiver;


    public RequestsHandler() {
        inByteBuffer = ByteBuffer.allocate(2048);
        inboundMessageReceiver = new InboundMessageReceiver();
    }


    public void handle() throws JSONException {
        JSONObject jsonReceivedMsg = inboundMessageReceiver.checkInboundBuffer(inByteBuffer);
        if(jsonReceivedMsg != null) {
            JsonMessageHandler(jsonReceivedMsg);
        }
    }


    private void JsonMessageHandler(JSONObject jsonReceivedMsg) {
        try {

            String cmd = jsonReceivedMsg.get("cmd").toString();
            String recipient = jsonReceivedMsg.get("recipient").toString();
            String msg = jsonReceivedMsg.get("msg").toString();

            if( cmd.trim().equalsIgnoreCase("time") ) {
                System.out.println(msg + "\r\n");
//                return;
            }

            if( cmd.trim().equalsIgnoreCase("message") ) {
                System.out.println("Sender says: " + msg);
            }

        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }

    }


    public ByteBuffer getByteBuffer() {
        return inByteBuffer;
    }

}
