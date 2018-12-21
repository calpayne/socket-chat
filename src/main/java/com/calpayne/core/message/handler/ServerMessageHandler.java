package com.calpayne.core.message.handler;

import com.calpayne.core.agent.Agent;
import com.calpayne.core.agent.Server;
import com.calpayne.core.message.Message;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Cal Payne
 */
public class ServerMessageHandler implements MessageHandler {
    
    @Override
    public synchronized void handleMessage(Agent agent, Message message) {
        Server server = (Server) agent;

        if (message.isUserMessage()) {
            server.addMessageToHistory(message);

            if (CommandMessageHandler.messageIsCommand(message)) {
                CommandMessageHandler cmh = new CommandMessageHandler();
                cmh.handleMessage(agent, message);
            } else {
                server.sendMessage(message);
            }
        }
    }

}
