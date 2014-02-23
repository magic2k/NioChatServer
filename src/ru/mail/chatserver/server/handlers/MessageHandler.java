package ru.mail.chatserver.server.handlers;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: admin
 * Date: 06.10.12
 * Time: 1:14
 */
public class MessageHandler extends AbstractHandler {

    public MessageHandler() {
        super("message", MessageHandler.class);

    }

    @Override
    public JSONObject handle(int sender, String recipient, String msg) throws JSONException {
        JSONObject jsonSendMsg = new JSONObject()
                .put("cmd", "message")
                .put("recipient", recipient)
                .put("msg", msg);

        return jsonSendMsg;
    }

}
