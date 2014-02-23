package ru.mail.chatserver.server.handlers;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: v.kolosov
 * Date: 05.10.12
 * Time: 17:58
 *
 *   Auto-add to commands list can be done with declaring handlers as inner classes and class.getClasses()
 *  or Reflections library.
 *  I prefer second, because lots of inner classes looks some isoteric.
 */
public interface Handler {

    public JSONObject handle(int sender, String recipient, String msg) throws JSONException;

    /**
     *  Add command to commands list */
    public void registerCommand();
}
