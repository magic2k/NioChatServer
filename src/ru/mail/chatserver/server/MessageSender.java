package ru.mail.chatserver.server;

import org.json.JSONException;
import org.json.JSONObject;
import ru.mail.chatserver.server.errors.ErrorResponsesHolder;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Map;
import java.util.Queue;

/**
 * User: magic2k
 * Date: 07.10.12
 * Time: 16:05
 */


public class MessageSender {

    public void sendMessage(int sender, String recipient, JSONObject jsonMessage, Queue<OutboundData> senderOutQueue) throws IOException, JSONException {

        if (recipient.equals("all")) {
            for (Map.Entry<Integer, SelectionKey> k : ServerData.getInstance().getConnectionsMap().entrySet()) {
                Integer recipientId = k.getKey();
                SelectionKey mapKey = k.getValue();
                jsonMessage.put("recipient", recipientId);

                UserRequestsHandler recipientHandler = (UserRequestsHandler) mapKey.attachment();
                Queue<OutboundData> recipientOutQueue = recipientHandler.getOutQueue();
                recipientOutQueue.add(new OutboundData(mapKey, jsonMessage));
                ServerData.getInstance().requestChangeOps(mapKey, SelectionKey.OP_WRITE);

            }
            return;

        } else {
            try {

                Integer recipientId = Integer.parseInt(recipient);

                if (ServerData.getInstance().getConnectionsMap().containsKey(recipientId)) {
                    SelectionKey mapKey = ServerData.getInstance().getConnectionsMap().get(recipientId);
                    if (mapKey.isValid()) {
                        senderOutQueue.add(new OutboundData(mapKey, jsonMessage));
                    } else {
                        ErrorResponsesHolder errorResponse = new ErrorResponsesHolder();
                        errorResponse.sendUserDisconnectedError(sender, recipient, senderOutQueue);
                    }
                }

                if (!ServerData.getInstance().getConnectionsMap().containsKey(recipientId)) {
                    ErrorResponsesHolder errorResponse = new ErrorResponsesHolder();
                    errorResponse.sendNoSuchUserError(sender, recipient, senderOutQueue);
                }

            } catch (NumberFormatException e) {
                ErrorResponsesHolder errorResponse = new ErrorResponsesHolder();
                errorResponse.sendIncorrectUserId(sender, recipient, senderOutQueue);
            }
        }

        SelectionKey key = ServerData.getInstance().getConnectionsMap().get(sender);
        ServerData.getInstance().requestChangeOps(key, SelectionKey.OP_WRITE);
    }

}
