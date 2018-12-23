package com.calpayne.core.message.handler;

import com.calpayne.core.agent.Agent;
import com.calpayne.core.agent.Client;
import com.calpayne.core.message.Message;
import com.calpayne.core.message.types.AreYouAliveMessage;
import com.calpayne.core.message.types.KickedMessage;
import com.calpayne.core.message.types.OnlineListDataMessage;

/**
 *
 * @author Cal Payne
 */
public class ClientMessageHandler implements MessageHandler {

    @Override
    public synchronized void handleMessage(Agent agent, Message message) {
        Client client = (Client) agent;

        if (!message.isUserMessage()) {
            if (message instanceof OnlineListDataMessage) {
                client.getChatFrame().updateOnlineList((OnlineListDataMessage) message);
            } else if (message instanceof AreYouAliveMessage) {
                AreYouAliveMessage ayam = new AreYouAliveMessage(true);
                client.sendMessage(ayam);
            } else if (message instanceof KickedMessage) {
                client.closeConnection();
            }
        } else if (!message.getFrom().equalsIgnoreCase(client.getHandle())) {
            client.addMessageToView(message);
        }
    }

}
