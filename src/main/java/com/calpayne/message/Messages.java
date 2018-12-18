package com.calpayne.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author Cal Payne
 */
public abstract class Messages {

    public static Message fromJSON(String json) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        return gson.fromJson(json, Message.class);
    }
}
