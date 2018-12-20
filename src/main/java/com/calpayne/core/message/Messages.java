package com.calpayne.core.message;

import com.calpayne.core.message.types.AreYouAliveMessage;
import com.calpayne.core.message.types.OnlineListDataMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author Cal Payne
 */
public abstract class Messages {

    /**
     * @param json the JSON to get a message from
     * @return the message from the JSON
     */
    public static Message fromJSON(String json) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        
        Message message = gson.fromJson(json, Message.class);
        
        if (json.startsWith("{\"online\":") && message.getFrom() == null) {
            message = gson.fromJson(json, OnlineListDataMessage.class);
        } else if (json.startsWith("{\"alive\":") && message.getFrom() == null) {
            message = gson.fromJson(json, AreYouAliveMessage.class);
        }

        return message;
    }
    
    public static String addEmojis(String message) {
        return message.replaceAll(":star:", "<img src=\"file:emoji/star.png\">")
                .replaceAll(":alien:", "<img src=\"file:emoji/alien.png\">")
                .replaceAll(":banana:", "<img src=\"file:emoji/banana.png\">")
                .replaceAll(":cheers:", "<img src=\"file:emoji/cheers.png\">")
                .replaceAll(":disco:", "<img src=\"file:emoji/disco.png\">")
                .replaceAll(":fire:", "<img src=\"file:emoji/fire.png\">")
                .replaceAll(":ghost:", "<img src=\"file:emoji/ghost.png\">")
                .replaceAll(":heart:", "<img src=\"file:emoji/heart.png\">")
                .replaceAll(":trophy:", "<img src=\"file:emoji/trophy.png\">");
    }
}
