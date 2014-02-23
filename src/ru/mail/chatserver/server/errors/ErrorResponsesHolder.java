package ru.mail.chatserver.server.errors;

import org.json.JSONException;
import org.json.JSONObject;
import ru.mail.chatserver.server.MessageSender;
import ru.mail.chatserver.server.OutboundData;
import ru.mail.chatserver.server.ServerData;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.charset.CharacterCodingException;
import java.util.Queue;

/**
 * User: magic2k
 * Date: 07.10.12
 * Time: 16:47
 */
public class ErrorResponsesHolder {

    MessageSender messageSender = new MessageSender();

    public void sendUserDisconnectedError(int sender, String recipient, Queue<OutboundData> senderOutQueue) throws JSONException, IOException {
        ServerData.getInstance().getConnectionsMap().remove(recipient);
        System.out.println("User was disconnected: " + recipient);
        JSONObject jsonMessage = new JSONObject()
                .put("cmd", "message")
                .put("recipient", sender)
                .put("msg", "User was disconnected: " + recipient);

        messageSender.sendMessage(sender, String.valueOf(sender), jsonMessage, senderOutQueue);
    }


    public void sendNoSuchUserError(int sender, String recipient, Queue<OutboundData> senderOutQueue) throws JSONException, IOException {
        System.out.println("No such user: " + recipient);
        JSONObject jsonMessage = new JSONObject()
                .put("cmd", "message")
                .put("recipient", sender)
                .put("msg", "No such user id in chat: " + recipient);

        messageSender.sendMessage(sender, String.valueOf(sender), jsonMessage, senderOutQueue);
    }


    public void sendIncorrectUserId(int sender, String recipient, Queue<OutboundData> senderOutQueue) throws JSONException, IOException {
        System.out.println("Incorrect user id: " + recipient);
        JSONObject jsonMessage = new JSONObject()
                .put("cmd", "message")
                .put("recipient", sender)
                .put("msg", "Incorrect user id: " + recipient);

        messageSender.sendMessage(sender, String.valueOf(sender), jsonMessage, senderOutQueue);
    }


    public void sendNoSuchCommand(int sender, String recipient, Queue<OutboundData> senderOutQueue, String cmd) throws JSONException, IOException {
        System.out.println("No such command on server: " + cmd);
        JSONObject jsonSendMessage = new JSONObject()
                .put("cmd", "message")
                .put("recipient", sender)
                .put("msg", "No such command on server: " + cmd);
        messageSender.sendMessage(sender, String.valueOf(sender), jsonSendMessage, senderOutQueue);
    }

}
