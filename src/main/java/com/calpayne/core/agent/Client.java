package com.calpayne.core.agent;

import com.calpayne.core.Connection;
import com.calpayne.core.Nametag;
import com.calpayne.core.Rank;
import com.calpayne.core.Settings;
import com.calpayne.core.gui.ChatFrame;
import com.calpayne.core.message.Message;
import com.calpayne.core.message.MessageType;
import com.calpayne.core.message.Messages;
import com.calpayne.core.message.handler.ClientMessageHandler;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cal Payne
 */
public class Client extends Agent {

    private Connection server;

    /**
     * Thread to check if servers have sent a new message (adds them to the blocking queue)
     */
    private final Thread receiveServerMessages = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!isOffline) {
                try {
                    if (server != null && server.hasMessage()) {
                        String message = server.receiveMessage();
                        queueMessage(Messages.fromJSON(message));
                    }
                } catch (InterruptedException | IOException | ClassNotFoundException ex) {
                    addMessageToView(new Message(MessageType.ERROR, "Server", "Lost connection to server."));
                }
            }
        }
    });

    /**
     * @param settings the settings to use
     * @param chatFrame the chat frame to use
     */
    public Client(Settings settings, ChatFrame chatFrame) {
        super(settings, chatFrame, new ClientMessageHandler());
        super.startup();
    }

    /**
     * Additional startup steps
     */
    @Override
    protected void startupSteps() {
        connectTo();
    }

    /**
     * Startup threads
     */
    @Override
    protected void startupThreads() {
        receiveServerMessages.start();
    }

    /**
     * Connect to the server in settings
     */
    private void connectTo() {
        try {
            InetAddress bindAddress = InetAddress.getByName(settings.getServerIP());
            server = new Connection(new Socket(bindAddress.getHostName(), settings.getServerPort()));

            // Send handle to new server
            Message message = new Message(settings.getHandle(), "");
            server.sendMessage(message);
        } catch (ConnectException ex) {
            addMessageToView(new Message(MessageType.ERROR, "Server", "Failed to connect to server."));
        } catch (SocketException ex) {
            addMessageToView(new Message(MessageType.ERROR, "Server", "Failed to connect to server."));
        } catch (UnknownHostException ex) {
            addMessageToView(new Message(MessageType.ERROR, "Server", "Failed to connect to server."));
        } catch (IOException ex) {
            addMessageToView(new Message(MessageType.ERROR, "Server", "Failed to connect to server."));
        }
    }

    public void closeConnection() {
        try {
            server.close();
            isOffline = true;
            chatFrame.resetList();
            chatFrame.addClient(new Nametag(settings.getHandle(), Rank.NORMAL));
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param message the message to send
     */
    @Override
    public synchronized void sendMessage(Message message) {
        try {
            if (message.isUserMessage()) {
                chatFrame.addMessageToView(message);
            }
            server.sendMessage(message);
        } catch (IOException ex) {
            addMessageToView(new Message(MessageType.ERROR, "Server", "Failed to send message to server (recconect to the server)."));
        }
    }

}
