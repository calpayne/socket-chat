package com.calpayne.core.agent;

import com.calpayne.core.Connection;
import com.calpayne.core.Settings;
import com.calpayne.core.message.Message;
import com.calpayne.core.message.MessageType;
import com.calpayne.core.message.Messages;
import com.calpayne.core.message.handler.ServerMessageHandler;
import com.calpayne.core.message.types.AreYouAliveMessage;
import com.calpayne.core.message.types.OnlineListDataMessage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
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
    private final HashMap<String, ArrayList<Message>> messageHistory = new HashMap<>();

    private final Thread acceptConnections = new Thread(() -> {
        while (true) {
            try {
                // Send new connection to pool to be processed
                processNewClient.execute(new ConnectionHandler(new Connection(serverSocket.accept())));
            } catch (IOException ex) {

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

    private final Thread sendAndWaitForAliveMessages = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                synchronized (lock) {
                    connections.entrySet().forEach((entry) -> {
                        String key = entry.getKey();
                        Connection value = entry.getValue();

                        if (!value.isClosed()) {
                            try {
                                value.sendMessage(new AreYouAliveMessage(true));
                            } catch (IOException ex) {
                                setUserOffline(key);
                            }
                            int timeout = 0;
                            boolean noMessage = false;
                            try {
                                while (!value.hasMessage()) {
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
                            } catch (IOException ex) {
                                setUserOffline(key);
                            }

                            if (noMessage) {
                                setUserOffline(key);
                            }
                        }
                    });
                }

                try {
                    Thread.sleep(Settings.GLOBAL_TIMEOUT_TIME * 1000);
                } catch (InterruptedException ex) {

                }
            }
        }
    });

    /**
     * @param settings the settings to use
     */
    public Server(Settings settings) {
        super(settings, new ServerMessageHandler());
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

            chatFrame.addClient(settings.getHandle());
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
        sendAndWaitForAliveMessages.start();
    }

    public void updateOnlineList(OnlineListDataMessage oldm) {
        chatFrame.updateOnlineList(oldm);
    }

    private void setUserOffline(String handle) {
        try {
            chatFrame.removeClient(handle);
            connections.get(handle).close();
            // causes a java.util.ConcurrentModificationException
            //connections.remove(handle);
            sendMessage(new OnlineListDataMessage(getChatFrame().getOnlineList()));
            sendMessage(new Message(MessageType.SERVER, "Server", "The user <b>" + handle + "</b> is now offline."));
        } catch (IOException ex) {

        }
    }

    /**
     * @param message the message to send
     */
    @Override
    public synchronized void sendMessage(Message message) {
        if (message.isUserMessage()) {
            chatFrame.addMessageToView(message);
        }

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

    public void addMessageToHistory(Message message) {
        synchronized (lock) {
            if (messageHistory.containsKey(message.getFrom())) {
                messageHistory.get(message.getFrom()).add(message);
            } else {
                ArrayList<Message> temp = new ArrayList();
                temp.add(message);

                messageHistory.put(message.getFrom(), temp);
            }
        }
    }

    public ArrayList<String> getOnlineList() {
        return chatFrame.getOnlineList();
    }

    public void addClientToOnlineList(String handle) {
        chatFrame.addClient(handle);

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
                    boolean doAdd = false;

                    // assuming first message is the handle it wants
                    String theirHandle = message.getFrom();
                    if (connections.containsKey(theirHandle) || theirHandle.equalsIgnoreCase(settings.getHandle())) {
                        if (connections.get(theirHandle).isClosed()) {
                            doAdd = true;
                        } else {
                            Message reply = new Message(MessageType.ERROR, "Server", "Could not connect because the handle '" + theirHandle + "' is already in use!");
                            newConnection.sendMessage(reply);
                        }
                    } else {
                        doAdd = true;
                    }

                    if (doAdd) {
                        synchronized (lock) {
                            // add to connections
                            connections.put(theirHandle, newConnection);
                            sendMessage(new Message(MessageType.SERVER, "Server", theirHandle + " has joined the chat room!"));

                            chatFrame.addClient(theirHandle);
                            sendMessage(new OnlineListDataMessage(getChatFrame().getOnlineList()));
                        }
                    }

                } else {
                    System.err.println("Connection with agent timed out.");
                }
            } catch (IOException | ClassNotFoundException ex) {

            }
        }

    }
}
