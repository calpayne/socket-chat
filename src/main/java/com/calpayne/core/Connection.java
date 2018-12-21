package com.calpayne.core;

import com.calpayne.core.message.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Cal Payne
 */
public class Connection {

    private final Socket socket;
    private final InputStream clientSocketInputStream;
    private final InputStreamReader clientSocketInputStreamReader;
    private final BufferedReader clientSocketBufferedReader;
    private final PrintWriter clientPrintWriter;
    private Nametag nametag;
    private boolean isClosed;

    /**
     * @param socket the socket to store and use
     * @throws java.io.IOException
     */
    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        clientSocketInputStream = this.socket.getInputStream();
        clientSocketInputStreamReader = new InputStreamReader(clientSocketInputStream);
        clientSocketBufferedReader = new BufferedReader(clientSocketInputStreamReader);
        clientPrintWriter = new PrintWriter(this.socket.getOutputStream(), true);
        isClosed = false;
    }

    /**
     * @param message the message to send
     * @throws java.io.IOException
     */
    public void sendMessage(Message message) throws IOException {
        clientPrintWriter.println(message.toJSON());
    }

    /**
     * @return the message
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public String receiveMessage() throws IOException, ClassNotFoundException {
        return clientSocketBufferedReader.readLine();
    }

    /**
     * @return if the connection has a message
     * @throws java.io.IOException
     */
    public boolean hasMessage() throws IOException {
        return clientSocketInputStream.available() > 0;
    }

    /**
     * @param ipAddress the ip address to check
     * @return if the connection has an ip address
     */
    public boolean hasIpAddress(final String ipAddress) {
        return socket.getInetAddress().getHostAddress().compareTo(ipAddress) == 0;
    }

    public void setNametag(Nametag nametag) {
        this.nametag = nametag;
    }

    public String getNametag() {
        return nametag.toString();
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void close() throws IOException {
        socket.close();
        isClosed = true;
    }
}
