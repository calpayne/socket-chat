package com.calpayne.message.handler;

import com.calpayne.core.agent.Agent;
import com.calpayne.message.Message;

/**
 *
 * @author Cal Payne
 */
public interface MessageHandler {

    public void handleMessage(Agent agent, Message message);

}
