package com.calpayne.core.agent;

import com.calpayne.core.Connection;
import com.calpayne.core.Settings;
import com.calpayne.message.Message;
import com.calpayne.message.Messages;
import com.calpayne.message.handler.MessageHandler;
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
            while (true) {
                try {
                    if (server.hasMessage()) {
                        String message = server.receiveMessage();
                        System.out.println(message);
                        queueMessage(Messages.fromJSON(message));
                    }
                } catch (InterruptedException | IOException | ClassNotFoundException ex) {

                }
            }
        }
    });

    public Client(Settings settings) {
        super(settings);
        super.startUp();

        super.setMessageHandler(new MessageHandler() {
            @Override
            public void handleMessage(Agent agent, Message message) {
                System.out.println(message);
                chatFrame.addMessageToView(message);
            }
        });
    }

    @Override
    protected boolean startUpSteps() {
        return connectTo();
    }

    @Override
    protected void startUpThreads() {
        receiveServerMessages.start();
    }

    private boolean connectTo() {
        boolean success = true;

        try {
            InetAddress bindAddress = InetAddress.getByName(settings.getServerIP());
            server = new Connection(new Socket(bindAddress.getHostName(), settings.getServerPort()));

            // Send handle to new server
            Message message = new Message(settings.getHandle(), "");
            server.sendMessage(message);
        } catch (ConnectException ex) {
            success = false;
            System.err.println("Failed to connect to server.");
        } catch (SocketException ex) {
            success = false;
            System.err.println("Failed to connect to server.");
        } catch (UnknownHostException ex) {
            success = false;
            System.err.println("Failed to connect to server.");
        } catch (IOException ex) {
            success = false;
            System.err.println("Failed to connect to server.");
        }

        return success;
    }

    @Override
    public void sendMessage(Message message) {
        chatFrame.addMessageToView(message);
        try {
            server.sendMessage(message);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
