package com.calpayne.core.agent;

import com.calpayne.core.Connection;
import com.calpayne.core.Settings;
import com.calpayne.message.Message;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author Cal Payne
 */
public class Client extends Agent {

    private Connection server;

    public Client(Settings settings) {
        super(settings);

        if (connectTo()) {
            super.startUp();
        }
    }

    @Override
    protected void startUpSteps() {

    }

    private boolean connectTo() {
        boolean success = true;

        try {
            InetAddress bindAddress = InetAddress.getByName(settings.getServerIP());
            server = new Connection(new Socket(bindAddress.getHostName(), settings.getServerPort()));

            // Send handle to new server
            Message message = new Message(settings.getHandle(), "");
            server.sendMessage(message);
            System.out.println("\033[1mConnected to:\033[0m " + settings.getServerIP());
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

}
