package com.calpayne.core.message.handler;

import com.calpayne.core.Rank;
import com.calpayne.core.agent.Agent;
import com.calpayne.core.agent.Server;
import com.calpayne.core.message.Message;
import com.calpayne.core.message.MessageType;
import com.calpayne.core.message.types.OnlineListDataMessage;
import java.util.Random;

/**
 *
 * @author Cal Payne
 */
public class CommandMessageHandler implements MessageHandler {

    @Override
    public void handleMessage(Agent agent, Message message) {
        Server server = (Server) agent;
        Random rand = new Random();

        String command = message.getMessage();
        String[] args = message.getMessage().split(" ");
        if (command.contains(" ")) {
            command = message.getMessage().toLowerCase().substring(0, message.getMessage().indexOf(" "));
        }

        switch (command) {
            case "/help":
                server.sendMessage(new Message(MessageType.SERVER, "Server", message.getFrom(), "Commands list:<br />"
                        + "<b>/emoji</b> - show a list of all emojis<br />"
                        + "<b>/message handle message</b> - send a private message<br />"
                        + "<b>/coinflip</b> - flip a coin<br />"
                        + "<b>/roll max</b> - roll for a random number up to the max<br />"
                        + "<b>/makeadmin handle</b> - make someone an admin"));
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
            case "/coinflip":
                String coinFlip = rand.nextBoolean() ? "Heads" : "Tails";
                server.sendMessage(new Message(MessageType.WHISPER, "Server", "<b>" + message.getFrom() + "</b> just flipped a coin and got <b>" + coinFlip + "</b>!"));
                break;
            case "/roll":
                try {
                    int roll = rand.nextInt(Integer.parseInt(args[1]));
                    server.sendMessage(new Message(MessageType.WHISPER, "Server", "<b>" + message.getFrom() + "</b> has just rolled <b>" + roll + "</b> out of <b>" + args[1] + "</b>!"));
                } catch (NumberFormatException e) {
                    server.sendMessage(new Message(MessageType.ERROR, "Server", message.getFrom(), "You need to use a valid integer for max!"));
                }
                break;
            case "/makeadmin":
                if (message.getFrom().equalsIgnoreCase(server.getHandle())) {
                    if (server.hasClient(args[1])) {
                        server.getChatFrame().setRank(args[1], Rank.ADMIN);
                        OnlineListDataMessage oldm = new OnlineListDataMessage(server.getChatFrame().getOnlineList());
                        server.getChatFrame().updateOnlineList(oldm);
                        server.sendMessage(oldm);
                        server.sendMessage(new Message(MessageType.SERVER, "Server", "The user <b>" + args[1] + "</b> is now an admin :trophy:!"));
                    } else {
                        server.sendMessage(new Message(MessageType.ERROR, "Server", message.getFrom(), "The client couldn't be found!"));
                    }
                } else {
                    server.sendMessage(new Message(MessageType.ERROR, "Server", message.getFrom(), "You do not have permission to do that!"));
                }
                break;
            default:
                server.sendMessage(new Message(MessageType.ERROR, "Server", message.getFrom(), "Your command is not recognised! Type <b>/help</b> for a list of commands!"));
        }
    }

    public static boolean messageIsCommand(Message message) {
        return message.getMessage().startsWith("/");
    }

}
