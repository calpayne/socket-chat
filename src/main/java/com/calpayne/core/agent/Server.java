package com.calpayne.core.agent;

import com.calpayne.core.Connection;
import com.calpayne.core.Nametag;
import com.calpayne.core.Rank;
import com.calpayne.core.Settings;
import com.calpayne.core.gui.ChatFrame;
import com.calpayne.core.message.Message;
import com.calpayne.core.message.MessageType;
import com.calpayne.core.message.Messages;
import com.calpayne.core.message.handler.CommandMessageHandler;
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
                            if ((!connection.isClosed()) && connection.hasMessage()) {
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
     * @param chatFrame the chat frame to use
     */
    public Server(Settings settings, ChatFrame chatFrame) {
        super(settings, chatFrame, new ServerMessageHandler());
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

            chatFrame.addClient(new Nametag(settings.getHandle(), Rank.SERVER));
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

    public void setUserOffline(String handle) {
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
        boolean send = true;

        if (message.isUserMessage()) {
            if (message.getTo() == null || message.getTo().isEmpty()) {
                chatFrame.addMessageToView(message);
            }

            if (message.getTo() != null && !message.getTo().isEmpty()) {
                try {
                    if (connections.containsKey(message.getTo())) {
                        connections.get(message.getTo()).sendMessage(message);
                    } else if (message.getTo().equalsIgnoreCase(settings.getHandle())) {
                        chatFrame.addMessageToView(message);
                    } else {
                        if (message.getFrom().equalsIgnoreCase(settings.getHandle())) {
                            chatFrame.addMessageToView(new Message(MessageType.ERROR, "Server", "The user you're trying to message could not be found."));
                        } else {
                            connections.get(message.getFrom()).sendMessage(new Message(MessageType.ERROR, "Server", "The user you're trying to message could not be found."));
                        }
                    }
                } catch (IOException ex) {

                }
                send = false;
            } else if (CommandMessageHandler.messageIsCommand(message)) {
                CommandMessageHandler cmh = new CommandMessageHandler();
                cmh.handleMessage(this, message);
                send = false;
            }
        }

        if (send) {
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
    }

    public boolean hasClient(String handle) {
        return connections.containsKey(handle);
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

    public ArrayList<Nametag> getOnlineList() {
        return chatFrame.getOnlineList();
    }

    public void addClientToOnlineList(Nametag nametag) {
        chatFrame.addClient(nametag);

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
                    if (theirHandle.isEmpty() || theirHandle.trim().isEmpty() || !theirHandle.matches("^[a-zA-Z0-9 ]*$")) {
                        newConnection.sendMessage(new Message(MessageType.ERROR, "Server", "Please only use letters and numbers in your name!"));
                    } else if (theirHandle.length() > 15) {
                        newConnection.sendMessage(new Message(MessageType.ERROR, "Server", "Please use a handle that has less than 15 characters!"));
                    } else if (connections.containsKey(theirHandle) || theirHandle.equalsIgnoreCase(settings.getHandle())) {
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
                            sendMessage(new Message(MessageType.SERVER, "Server", "<b>" + theirHandle + "</b> has joined the chat room!"));

                            // this is where their rank would be set etc
                            chatFrame.addClient(new Nametag(theirHandle));
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
