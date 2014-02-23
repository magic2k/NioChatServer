package ru.mail.chatserver.server;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: magic2k
 * Date: 02.09.12
 * Time: 4:10
 */
public class ChatServer extends Thread {

    private Selector selector;
    private AtomicInteger id;
    private volatile boolean isRunning;
    private ServerSocketChannel serverSocketChannel;
    private ExecutorService threadPool = Executors.newFixedThreadPool(20);

    public ChatServer(String host, int port) {

        try {

            id = new AtomicInteger();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().setReuseAddress(true);
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(host, port));
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            System.out.println("Can't create selector or ServerSocketChannel");
            throw new IllegalStateException(e);
        }

    }


    public void run() {

        isRunning = true;
        while (isRunning) {
            try {


                processInterestOpsChangeRequests();
                checkForConnections();

            } catch (Exception e) {
                throw new IllegalStateException(e);
            }

            yield();
        }

        stopServer();
    }

    private void checkForConnections() {

        try {
            // need selector timeout there because otherwise select call will block thread until any event occured
            // and changeRequests will not be processed
            if (selector.select(10) == 0) {
                return;
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator it = selectedKeys.iterator();

        while(it.hasNext()) {

            SelectionKey key = (SelectionKey) it.next();
            it.remove();

            try {

                if (key.isValid() && key.isAcceptable()) {
                    acceptConnection(key);
                }

                if (key.isValid() && key.isReadable()) {
                    readMessage(key);
                }

                if (key.isValid() && key.isWritable()) {
                    writeMessage(key);
                }
            } catch (IOException e) {
                key.cancel();
                UserRequestsHandler u = (UserRequestsHandler)key.attachment();
                ServerData.getInstance().getConnectionsMap().remove(u.getId());
            } catch (IllegalStateException e) {
                key.cancel();
                UserRequestsHandler u = (UserRequestsHandler)key.attachment();
                ServerData.getInstance().getConnectionsMap().remove(u.getId());
            }
        }

    }

    private void acceptConnection(SelectionKey key) throws IOException {
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        UserRequestsHandler userRequestsHandler = new UserRequestsHandler();
        SelectionKey clientKey = socketChannel.register(selector, SelectionKey.OP_READ, userRequestsHandler);

        int userId = id.incrementAndGet();
        ServerData.getInstance().getConnectionsMap().put(userId, clientKey);
        userRequestsHandler.setId(userId);
        System.out.println(userRequestsHandler.getId() + " connected");
    }

    private void readMessage(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        if (socketChannel == null || !socketChannel.isConnected()) {
            throw new IllegalStateException("SocketChannel not connected or null");
        }

        UserRequestsHandler userRequestsHandler = (UserRequestsHandler) key.attachment();

        int bytesRead = socketChannel.read(userRequestsHandler.getInByteBuffer());
        // remote peer disconnected
        if (bytesRead == -1) {
            socketChannel.close();
            key.cancel();
            return;
        }

        if (bytesRead > 0) {
            threadPool.execute(userRequestsHandler);
        }
    }


    private void writeMessage(SelectionKey key) throws IOException {

        UserRequestsHandler userRequestsHandler = (UserRequestsHandler) key.attachment();
        OutboundData outMessageData = userRequestsHandler.getOutQueue().poll();

        if(outMessageData == null) {
            return;
        }

        SelectionKey recipientKey = outMessageData.getRecipientSelectionKey();

        if(!recipientKey.isValid()) {
            return;
        }

        SocketChannel recipientSocketChannel = (SocketChannel) outMessageData.getRecipientSelectionKey().channel();
        if (recipientSocketChannel == null || !recipientSocketChannel.isConnected()) {
            throw new IllegalStateException("recipientSocketChannel not connected or null");
        }


        ByteBuffer outMessage = outMessageData.getOutMessageByteBuffer();

        if (outMessage != null && outMessage.hasRemaining()) {
            int bytesWritten = recipientSocketChannel.write(outMessage);

            if (bytesWritten == -1) throw new EOFException();
                System.out.println("Bytes written " + bytesWritten);
        }

        if (userRequestsHandler.getOutQueue().isEmpty()) {
            key.interestOps(SelectionKey.OP_READ);
        }
    }


    private void processInterestOpsChangeRequests() {

        Map<SelectionKey, Integer> interestOpsRequestsMap = ServerData.getInstance().getChangeInterestOpsRequestsMap();

        if( !interestOpsRequestsMap.isEmpty() ) {

            // I hope lack of local vars there is okay
            for(Entry<SelectionKey, Integer> entry : interestOpsRequestsMap.entrySet()) {
                entry.getKey().interestOps(entry.getValue());
                interestOpsRequestsMap.remove(entry.getKey());
                selector.wakeup();
            }
        }
    }


    private void stopServer() {
        try {

            threadPool.shutdown();
            serverSocketChannel.close();

        } catch (IOException e) {
            System.out.println("Can't close server socket");
            throw new IllegalStateException(e);
        }
    }


}
