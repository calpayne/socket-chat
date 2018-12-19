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
        return message.replaceAll(":star:", "<img src=\"file:star.png\">");
    }
}
