package com.calpayne.core.message.types;

import com.calpayne.core.message.Message;
import com.google.gson.Gson;

/**
 *
 * @author Cal Payne
 */
public class AreYouAliveMessage extends Message {

    private final boolean alive;

    public AreYouAliveMessage(boolean alive) {
        this.alive = alive;
    }

    public boolean isAlive() {
        return alive;
    }

    @Override
    public boolean isUserMessage() {
        return false;
    }

    @Override
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
