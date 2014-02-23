package ru.mail.chatserver.client;

import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * User: magic2k
 * Date: 18.09.12
 */
public class ChatClientData {
    private static ChatClientData ourInstance = new ChatClientData();
    private Queue<ByteBuffer> outQueue;

    public static ChatClientData getInstance() {
        return ourInstance;
    }

    private ChatClientData() {
        outQueue = new ConcurrentLinkedQueue<ByteBuffer>();
    }

    public Queue<ByteBuffer> getOutQueue() {
        return outQueue;
    }
}
