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
                Message emojis = new Message(MessageType.SERVER, "Server", message.getFrom(), "Emoji list:<br />"
                        + ":alien: is <b>:alien:</b><br />"
                        + ":banana: is <b>:banana:</b><br />"
                        + ":cheers: is <b>:cheers:</b><br />"
                        + ":disco: is <b>:disco:</b><br />"
                        + ":fire: is <b>:fire:</b><br />"
                        + ":ghost: is <b>:ghost:</b><br />"
                        + ":heart: is <b>:heart:</b><br />"
                        + ":star: is <b>:star:</b><br />"
                        + ":trophy: is <b>:trophy:</b>");
                emojis.setOnlyFirstEmoji(true);
                server.sendMessage(emojis);
                break;
            case "/message":
                String whipser = "";
                for (int i = 2; i < args.length; i++) {
                    whipser += args[i] + " ";
                }
                server.sendMessage(new Message(MessageType.WHISPER, message.getFrom(), args[1], whipser));
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
