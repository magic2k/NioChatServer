package ru.mail.chatserver.server.handlers;

import org.json.JSONException;
import org.json.JSONObject;
import ru.mail.chatserver.server.ServerData;

/**
 * User: v.kolosov
 * Date: 05.10.12
 * Time: 19:00
 */
public abstract class AbstractHandler implements Handler {

    String command;
    Class<? extends Handler> thisHandler;

    protected AbstractHandler(String command, Class<? extends Handler> thisHandler) {
        this.thisHandler = thisHandler;
        this.command = command;
        registerCommand();
    }


    public  void registerCommand() {
        ServerData.getInstance().getCommandsMap().put(command, thisHandler);
    }

    public abstract JSONObject handle(int sender, String recipient, String msg) throws JSONException;

}
