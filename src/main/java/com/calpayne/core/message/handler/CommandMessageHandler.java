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
        
        String command = message.getMessage();
        String[] args = message.getMessage().split(" ");
        if (command.contains(" ")) {
            command = message.getMessage().toLowerCase().substring(0, message.getMessage().indexOf(" "));
        }
        
        
        switch (command) {
            case "/help":
                server.sendMessage(new Message(MessageType.SERVER, "Server", message.getFrom(), "Commands list:<br />"
                        + "<b>/emoji</b> - show a list of all emojis<br />"
                        + "<b>/message handle message</b> - send a private message"));
                break;
            case "/emoji":
                server.sendMessage(new Message(MessageType.SERVER, "Server", message.getFrom(), "Emoji list:<br />"
                        + ":alien: - <b>:alien:</b><br />"
                        + ":banana: - <b>:banana:</b><br />"
                        + ":cheers: - <b>:cheers:</b><br />"
                        + ":disco: - <b>:disco:</b><br />"
                        + ":fire: - <b>:fire:</b><br />"
                        + ":ghost: - <b>:ghost:</b><br />"
                        + ":heart: - <b>:heart:</b><br />"
                        + ":star: - <b>:star:</b><br />"
                        + ":trophy: - <b>:trophy:</b>"));
                break;
            case "/message":
                server.sendMessage(new Message(MessageType.WHISPER, message.getFrom(), args[1], args[2]));
                break;
            default:
                System.out.println(command);
                server.sendMessage(new Message(MessageType.ERROR, "Server", message.getFrom(), "Your command is not recognised! Type <b>/help</b> for a list of commands!"));
        }
    }

    public static boolean messageIsCommand(Message message) {
        return message.getMessage().startsWith("/");
    }

}
