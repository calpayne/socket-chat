package com.calpayne.core.agent;

import com.calpayne.core.Connection;
import com.calpayne.core.Settings;
import com.calpayne.core.gui.OnlineList;
import com.calpayne.core.message.Message;
import com.calpayne.core.message.MessageType;
import com.calpayne.core.message.Messages;
import com.calpayne.core.message.types.OnlineListDataMessage;
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
                                queueMessage(Messages.fromJSON(message));
                            }
                        } catch (InterruptedException | IOException | ClassNotFoundException ex) {

                        }
                    });
                }
            }
        }
    });

    /**
     * @param settings the settings to use
     */
    public Server(Settings settings) {
        super(settings, (Agent agent, Message message) -> {
            agent.sendMessage(message);
        });
        super.startup();
    }

    /**
     * Additional startup steps
     */
    @Override
    protected void startupSteps() {
        try {
            InetAddress bindAddress = InetAddress.getByName(settings.getServerIP());
            serverSocket = new ServerSocket(settings.getServerPort(), 0, bindAddress);
        } catch (IOException ex) {

        }
    }

    /**
     * Startup threads
     */
    @Override
    protected void startupThreads() {
        acceptConnections.start();
        receiveClientMessages.start();
    }

    /**
     * @param message the message to send
     */
    @Override
    public void sendMessage(Message message) {
        chatFrame.addMessageToView(message);
        connections.entrySet().forEach((entry) -> {
            String key = entry.getKey();
            Connection value = entry.getValue();
            try {
                value.sendMessage(message);
                value.sendMessage(new OnlineListDataMessage(chatFrame.getOnlineList()));
            } catch (IOException ex) {
                chatFrame.removeClient(key);
            }
        });
    }

    public void sendMessage(OnlineList message) {
        connections.entrySet().forEach((entry) -> {
            String key = entry.getKey();
            Connection value = entry.getValue();
            try {
                value.sendMessage(message);
            } catch (IOException ex) {
                chatFrame.removeClient(key);
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

                            chatFrame.addClient(theirHandle);
                        }
                    } else {
                        Message reply = new Message(MessageType.ERROR, "Server", "Could not connect because the handle '" + theirHandle + "' is already in use!");
                        newConnection.sendMessage(reply);
                    }

                } else {
                    System.err.println("Connection with agent timed out.");
                }
            } catch (IOException | ClassNotFoundException ex) {

            }
        }

    }
}
