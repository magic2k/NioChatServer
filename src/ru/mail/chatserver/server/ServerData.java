package ru.mail.chatserver.server;

import org.reflections.Reflections;
import ru.mail.chatserver.server.handlers.AbstractHandler;
import ru.mail.chatserver.server.handlers.Handler;

import java.nio.channels.SelectionKey;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: v.kolosov
 * Date: 11.09.12
 * Time: 13:15
 * Singleton
 */
public class ServerData {
    private static ServerData ourInstance = new ServerData();
    private Map<Integer ,SelectionKey> connectionsMap;
    private Map<SelectionKey ,Integer> changeInterestOpsRequestsMap;
    private Map<String, Class<? extends Handler>> commandsMap;

    private ServerData() {
        connectionsMap = new ConcurrentHashMap<Integer, SelectionKey>();
        changeInterestOpsRequestsMap = new ConcurrentHashMap<SelectionKey, Integer>();
        commandsMap = new ConcurrentHashMap<String, Class<? extends Handler>>();
    }

    /**
     *
     * Get all handlers classes using Reflections lib and register it in commands list
    Must be called before any server activity, because scan for command handlers takes some time
     */
    public void scanCommandHandlers() {
        Reflections reflections = new Reflections("ru.mail.chatserver.server.handlers");
        Set<Class<? extends AbstractHandler>> handlerClasses = reflections.getSubTypesOf(AbstractHandler.class);

        for(Class<? extends Handler> handlerClass : handlerClasses) {
            try {

                handlerClass.newInstance().registerCommand();

            } catch (InstantiationException e) {
                throw new IllegalStateException(e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

    }


    public void requestChangeOps(SelectionKey key, Integer ops) {
        changeInterestOpsRequestsMap.put(key, ops);
    }


    public static ServerData getInstance() {
        return ourInstance;
    }


    public Map<Integer, SelectionKey> getConnectionsMap() {
        return connectionsMap;
    }


    public Map<SelectionKey, Integer> getChangeInterestOpsRequestsMap() {
        return changeInterestOpsRequestsMap;
    }


    public Map<String, Class<? extends Handler>> getCommandsMap() {
        return commandsMap;
    }

}
