package ru.mail.chatserver.server.handlers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * User: magic2k
 * Date: 07.10.12
 * Time: 15:42
 */
public class TimeHandler extends AbstractHandler {

    public TimeHandler() {
        super("time", TimeHandler.class);
    }


    public JSONObject handle(int sender, String recipient, String msg) throws JSONException {

        Date date = new Date();
        JSONObject jsonSendMsg = new JSONObject()
                .put("cmd", "time")
                .put("recipient", sender)
                .put("msg", date.toString());

        return  jsonSendMsg;
    }

}
