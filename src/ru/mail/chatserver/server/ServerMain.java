package ru.mail.chatserver.server;

import ru.mail.chatserver.server.ChatServer;
import ru.mail.chatserver.shared.BufferDecoder;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * User: magic2k
 * Date: 02.09.12
 * Time: 4:03
 */
public class ServerMain {

    public static void main(String[] args) {
        ServerData.getInstance().scanCommandHandlers();
        new ChatServer("", 9090).run();

    }

}
