package com.calpayne.core.message;

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

        return gson.fromJson(json, Message.class);
    }
}
