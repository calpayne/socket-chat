package com.calpayne.core.agent.heartbeat;

import com.calpayne.core.agent.Agent;
import com.calpayne.core.agent.Client;
import com.calpayne.core.agent.Server;
import com.calpayne.core.message.types.OnlineListDataMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cal Payne
 */
public class HeartbeatSender implements Runnable {

    private final Agent agent;

    public HeartbeatSender(Agent agent) {
        this.agent = agent;
    }

    @Override
    public void run() {
        while (true) {
            if (agent instanceof Server) {
                serverTask((Server) agent);
            } else if (agent instanceof Client) {
                clientTask((Client) agent);
            }
        }
    }

    private void clientTask(Client client) {
        // Eventually send a "i am online" message every 5 mins
    }

    private void serverTask(Server server) {
        server.sendMessage(new OnlineListDataMessage(server.getChatFrame().getOnlineList()));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(HeartbeatSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
