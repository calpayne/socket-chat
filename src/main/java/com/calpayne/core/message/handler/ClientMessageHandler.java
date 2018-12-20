package com.calpayne.core.message.handler;

import com.calpayne.core.agent.Agent;
import com.calpayne.core.message.Message;
import com.calpayne.core.message.types.AreYouAliveMessage;
import com.calpayne.core.message.types.OnlineListDataMessage;

/**
 *
 * @author Cal Payne
 */
public class ClientMessageHandler implements MessageHandler {
    
    @Override
    public void handleMessage(Agent agent, Message message) {
        if (!message.isUserMessage()) {
            if (message instanceof OnlineListDataMessage) {
                agent.getChatFrame().updateOnlineList((OnlineListDataMessage) message);
            } else if (message instanceof AreYouAliveMessage) {
                agent.sendMessage(message);
            }
        } else if (!message.getFrom().equalsIgnoreCase(agent.getHandle())) {
            agent.addMessageToView(message);
        }
    }
    
}
