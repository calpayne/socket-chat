package com.calpayne.core.message.handler;

import com.calpayne.core.agent.Agent;
import com.calpayne.core.agent.Server;
import com.calpayne.core.message.Message;

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
        }
        
        server.addMessageToHistory(message);
        server.sendMessage(message);
    }
    
}
