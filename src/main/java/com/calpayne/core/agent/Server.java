package com.calpayne.core.agent;

import com.calpayne.core.Connection;
import com.calpayne.core.Settings;
import com.calpayne.message.Message;
import com.calpayne.message.MessageType;
import com.calpayne.message.Messages;
import com.calpayne.message.handler.MessageHandler;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Cal Payne
 */
public class Server extends Agent {

    private final Object lock = new Object();
    private final ExecutorService processNewClient = Executors.newFixedThreadPool(20);
    private ServerSocket serverSocket;
    private final HashMap<String, Connection> connections = new HashMap<>();

    private final Thread acceptConnections = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    // Send new connection to pool to be processed
                    processNewClient.execute(new ConnectionHandler(new Connection(serverSocket.accept())));
                } catch (IOException ex) {

                }
            }
        }
    });

    private final Thread receiveClientMessages = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                synchronized (lock) {
                    connections.values().forEach((connection) -> {
                        try {
                            if (connection.hasMessage()) {
                                String message = connection.receiveMessage();
                                System.out.println(message);
                                queueMessage(Messages.fromJSON(message));
                            }
                        } catch (InterruptedException | IOException | ClassNotFoundException ex) {

                        }
                    });
                }
            }
        }
    });

    public Server(Settings settings) {
        super(settings, new MessageHandler() {
            @Override
            public void handleMessage(Agent agent, Message message) {
                System.out.println(message);
                agent.addMessageToView(message);
            }
        });
        super.startUp();
    }

    @Override
    protected boolean startUpSteps() {
        try {
            InetAddress bindAddress = InetAddress.getByName(settings.getServerIP());
            serverSocket = new ServerSocket(settings.getServerPort(), 0, bindAddress);
        } catch (IOException ex) {
            return false;
        }

        return true;
    }

    @Override
    protected void startUpThreads() {
        acceptConnections.start();
        receiveClientMessages.start();
    }

    @Override
    public void sendMessage(Message message) {
        chatFrame.addMessageToView(message);
        connections.values().forEach((connection) -> {
            try {
                connection.sendMessage(message);
            } catch (IOException ex) {

            }
        });
    }

    private class ConnectionHandler implements Runnable {

        private final Connection newConnection;

        /**
         * @param newConnection the connection to handle
         */
        public ConnectionHandler(Connection newConnection) {
            this.newConnection = newConnection;
        }

        /**
         * Process a new connection
         */
        @Override
        public void run() {
            try {
                int timeout = 0;
                boolean noMessage = false;
                while (!newConnection.hasMessage()) {
                    if (timeout == Settings.GLOBAL_TIMEOUT_TIME) {
                        noMessage = true;
                        break;
                    }

                    try {
                        Thread.sleep(1000);
                        timeout++;
                    } catch (InterruptedException ex) {

                    }
                }

                if (!noMessage) {
                    String receivedMessage = newConnection.receiveMessage();
                    Message message = Messages.fromJSON(receivedMessage);

                    // assuming first message is the handle it wants
                    String theirHandle = message.getFrom();
                    if (!connections.containsKey(theirHandle)) {
                        synchronized (lock) {
                            // add to connections
                            connections.put(theirHandle, newConnection);
                            Message announce = new Message(MessageType.SERVER, "Server", theirHandle + " has joined the chat room!");
                            sendMessage(announce);
                        }
                    } else {
                        System.err.println("Already connected to a peer with name: '" + theirHandle + "'");
                    }

                } else {
                    System.err.println("Connection with agent timed out.");
                }
            } catch (IOException | ClassNotFoundException ex) {

            }
        }

    }
}
