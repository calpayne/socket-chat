package com.calpayne.core.message.handler;

import com.calpayne.core.agent.Agent;
import com.calpayne.core.agent.Server;
import com.calpayne.core.message.Message;
import com.calpayne.core.message.MessageType;
import com.calpayne.core.message.types.OnlineListDataMessage;

/**
 *
 * @author Cal Payne
 */
public class ServerMessageHandler implements MessageHandler {

    @Override
    public void handleMessage(Agent agent, Message message) {
        Server server = (Server) agent;

        if (!server.getOnlineList().contains(message.getFrom())) {
            server.addClientToOnlineList(message.getFrom());
            server.sendMessage(new Message(MessageType.SERVER, "Server", "The user <b>" + message.getFrom() + "</b> is no longer AFK."));
            server.sendMessage(new OnlineListDataMessage(server.getChatFrame().getOnlineList()));
        }

        server.addMessageToHistory(message);
        server.sendMessage(message);
    }

}
