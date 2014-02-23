package ru.mail.chatserver.client;

import java.io.IOException;

/**
 * User: admin
 * Date: 15.09.12
 * Time: 15:22
 */
public class ChatClientMain {

    public static void main(String[] args) {

        try {

            ChatClient client = new ChatClient("", 9090);
            ChatUserInputHandler userInputHandler = new ChatUserInputHandler();
            Thread t1 = new Thread(client, "ClientThread");
            Thread t2 = new Thread(userInputHandler, "UserInputThread");

            t1.start();
            t2.start();

        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }


}
