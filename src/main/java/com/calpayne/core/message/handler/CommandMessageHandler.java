package com.calpayne.core.message.handler;

import com.calpayne.core.agent.Agent;
import com.calpayne.core.agent.Server;
import com.calpayne.core.message.Message;
import com.calpayne.core.message.MessageType;

/**
 *
 * @author Cal Payne
 */
public class CommandMessageHandler implements MessageHandler {

    @Override
    public void handleMessage(Agent agent, Message message) {
        Server server = (Server) agent;

        switch (message.getMessage().toLowerCase()) {
            case "/help":
                server.sendMessage(new Message(MessageType.SERVER, "Server", message.getFrom(), "Commands list:<br />"
                        + "<b>/emoji</b> - show a list of all emojis<br />"
                        + "<b>/message <handle> <message></b> - send a private message"));
                break;
            case "/emoji":
                server.sendMessage(new Message(MessageType.SERVER, "Server", message.getFrom(), "Emoji list (remove spaces to use):<br />"
                        + "<b>: alien :</b> - :alien:<br />"
                        + "<b>: banana :</b> - :banana:<br />"
                        + "<b>: cheers :</b> - :cheers:<br />"
                        + "<b>: disco :</b> - :disco:<br />"
                        + "<b>: fire :</b> - :fire:<br />"
                        + "<b>: ghost :</b> - :ghost:<br />"
                        + "<b>: heart :</b> - :heart:<br />"
                        + "<b>: star :</b> - :star:<br />"
                        + "<b>: trophy :</b> - :trophy:"));
                break;
            default:
                server.sendMessage(new Message(MessageType.ERROR, "Server", message.getFrom(), "Your command is not recognised! Type <b>/help</b> for a list of commands!"));
        }
    }

    public static boolean messageIsCommand(Message message) {
        return message.getMessage().startsWith("/");
    }

}
