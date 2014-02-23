package ru.mail.chatserver.server;

import org.json.JSONException;
import org.json.JSONObject;
import ru.mail.chatserver.server.errors.ErrorResponsesHolder;
import ru.mail.chatserver.server.handlers.Handler;
import ru.mail.chatserver.shared.util.InboundMessageReceiver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * User: magic2k
 * Date: 07.09.12
 */
public class UserRequestsHandler implements Runnable {
    private int id;
    private ByteBuffer inByteBuffer;
    private Queue<OutboundData> outQueue;
    private InboundMessageReceiver inboundMessageReceiver;
    private MessageSender messageSender;

    public UserRequestsHandler() {
        inByteBuffer = ByteBuffer.allocate(2048);     //allocateDirect then
        outQueue = new ConcurrentLinkedQueue<OutboundData>();
        inboundMessageReceiver = new InboundMessageReceiver();
        messageSender = new MessageSender();
    }


    public synchronized void run() {
        JSONObject jsonReceivedMsg;
        try {
            jsonReceivedMsg = inboundMessageReceiver.checkInboundBuffer(inByteBuffer);
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }

        if(jsonReceivedMsg != null) {
            processMessage(jsonReceivedMsg);
        }
    }


    private void processMessage(JSONObject jsonReceivedMsg) {
        try {

            String cmd = jsonReceivedMsg.get("cmd").toString();
            String recipient = jsonReceivedMsg.get("recipient").toString();
            String msg = jsonReceivedMsg.get("msg").toString();
            System.out.println("JSON test: " + cmd + " " + recipient + " " + msg);
            System.out.println(jsonReceivedMsg.toString());

            Map<String, Class<? extends Handler>> commandMap = ServerData.getInstance().getCommandsMap();
            if(commandMap.containsKey(cmd)) {

                Handler handler = commandMap.get(cmd).newInstance();
                JSONObject jsonSendMsg = handler.handle(id, recipient, msg);
                messageSender.sendMessage(id, jsonSendMsg.get("recipient").toString(), jsonSendMsg, outQueue);

            } else {
                ErrorResponsesHolder errorResponse = new ErrorResponsesHolder();
                errorResponse.sendNoSuchCommand(id, String.valueOf(id), outQueue, cmd);
            }

        } catch (JSONException e) {
            throw new IllegalStateException(e);
        } catch (CharacterCodingException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }

    }


    public ByteBuffer getInByteBuffer() {
        return inByteBuffer;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public Queue<OutboundData> getOutQueue() {
        return outQueue;
    }

}
