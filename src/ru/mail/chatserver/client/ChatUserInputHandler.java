package ru.mail.chatserver.client;

import org.json.JSONException;
import org.json.JSONObject;
import ru.mail.chatserver.shared.BufferDecoder;
import ru.mail.chatserver.shared.util.ByteBufferSizeHeaderMaker;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.charset.CharacterCodingException;
import java.util.Queue;
import java.util.Scanner;

/**
 * User: magic2k
 * Date: 18.09.12
 */
public class ChatUserInputHandler implements Runnable {

    private static String cmd;
    private static String recipient;
    private static String msg;
    private volatile boolean isRunning;
    private BufferDecoder bufferDecoder;
    private ChatClientData clientData;

    public void run() {

        bufferDecoder = new BufferDecoder();
        clientData = ChatClientData.getInstance();
        Queue<ByteBuffer> outQueue = clientData.getOutQueue();
        isRunning = true;
        System.out.println("Input format: <command name> <recipient> <message>");
        System.out.println("List of commands: time, message");
        System.out.println("Example: message all Yo-ho-ho");
        System.out.println("All fields must be filled...");

        while(isRunning) {

            Scanner sc = new Scanner(System.in);
            cmd = sc.next();
            recipient = sc.next();
            msg = sc.nextLine();

            try {
                JSONObject jsonMsg = new JSONObject()
                        .put("cmd", cmd)
                        .put("recipient", recipient)
                        .put("msg", msg);

                ByteBuffer outMessage = bufferDecoder.jsonToByteBuffer(jsonMsg);

                ByteBuffer outMessageWithSize = ByteBufferSizeHeaderMaker.addMessageSize(outMessage);
                outQueue.add(outMessageWithSize);


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (CharacterCodingException e) {
                e.printStackTrace();
            }

        }

    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
