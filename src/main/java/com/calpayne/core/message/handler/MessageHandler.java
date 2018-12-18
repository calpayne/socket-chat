package com.calpayne.core.message.handler;

import com.calpayne.core.agent.Agent;
import com.calpayne.core.message.Message;

/**
 *
 * @author Cal Payne
 */
public interface MessageHandler {

    /**
     * @param agent the agent handling the message
     * @param message the message to handle
     */
    public void handleMessage(Agent agent, Message message);

}
