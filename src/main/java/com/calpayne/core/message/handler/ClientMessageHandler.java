package com.calpayne.core.message.handler;

import com.calpayne.core.agent.Agent;
import com.calpayne.core.message.Message;

/**
 *
 * @author Cal Payne
 */
public class ClientMessageHandler implements MessageHandler {

    @Override
    public void handleMessage(Agent agent, Message message) {
        if (message.getFrom() == null) {
            agent.getChatFrame().updateOnlineListFromJSON(message.getMessage());
        } else {
            if (!message.getFrom().equalsIgnoreCase(agent.getHandle())) {
                agent.addMessageToView(message);
            }
        }
    }

}
