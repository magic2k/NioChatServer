package ru.mail.chatserver.client;

import org.json.JSONException;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Queue;

/**
 * User: magic2k
 * Date: 09.09.12
 */
public class ChatClient implements Runnable {

    private SocketChannel socketChannel;
    private Selector selector;
    private boolean isRunning = false;
    private RequestsHandler handler;
    private ChatClientData clientData;
    private Queue<ByteBuffer> outQueue;

    public ChatClient(String host, int port) throws IOException, InterruptedException {
        handler = new RequestsHandler();
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        selector = Selector.open();
        socketChannel.connect(new InetSocketAddress(host, port));
        while (!socketChannel.finishConnect()) {
            Thread.sleep(100);
        }
        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        clientData = ChatClientData.getInstance();
        outQueue = clientData.getOutQueue();
    }


    public void run() {

        isRunning = true;
        while(isRunning) {

            try {
                Thread.sleep(100);
                checkForConnections();

            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            } catch (JSONException e) {
                throw new IllegalStateException(e);
            }

            Thread.yield();
        }
        //shutdownClient
    }


    private void checkForConnections() throws IOException, JSONException {

        if(selector.select() == 0) {
            return;
        }

        for(SelectionKey k : selector.selectedKeys()) {
            SelectionKey key = k;
            selector.selectedKeys().remove(k);

            if (key.isValid() && key.isReadable()) {
                readMessage(key);
            }

            if (key.isValid() && key.isWritable()) {
                writeMessage(key);
            }
        }

    }


    private void readMessage(SelectionKey key) throws IOException, JSONException {
        SocketChannel socketChannel = (SocketChannel)key.channel();

        int readBytes = socketChannel.read(handler.getByteBuffer());
//        System.out.println("Bytes read: " + readBytes);
        if(readBytes == -1) {
            return;
        }

        if(readBytes > 0) {
            handler.handle();
        }

    }


    private void writeMessage(SelectionKey key) throws IOException {

        if (socketChannel == null || !socketChannel.isConnected()) {
            throw new IllegalStateException("SocketChannel not connected or null");
        }

        ByteBuffer outMessage = outQueue.poll();
        if( outMessage != null && outMessage.hasRemaining() ) {

            int bytesWritten = socketChannel.write(outMessage);
//            System.out.println("Bytes written " + bytesWritten);

            if (bytesWritten == -1 ) throw new EOFException();
        }

    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
