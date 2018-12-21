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

    private final ExecutorService processNewCommand = Executors.newFixedThreadPool(20);

    @Override
    public synchronized void handleMessage(Agent agent, Message message) {
        Server server = (Server) agent;

        if (message.isUserMessage()) {
            server.addMessageToHistory(message);

            if (CommandMessageHandler.messageIsCommand(message)) {
                processNewCommand.execute(new HandleNewCommand(server, message));
            } else {
                server.sendMessage(message);
            }
        }
    }

    private class HandleNewCommand implements Runnable {

        private final Server server;
        private final Message message;

        public HandleNewCommand(Server server, Message message) {
            this.server = server;
            this.message = message;
        }

        @Override
        public void run() {
            CommandMessageHandler cmh = new CommandMessageHandler();
            cmh.handleMessage(server, message);
        }

    }

}
