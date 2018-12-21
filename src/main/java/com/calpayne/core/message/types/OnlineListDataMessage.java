package com.calpayne.core.message.types;

import com.calpayne.core.Nametag;
import com.calpayne.core.message.Message;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Cal Payne
 */
public class OnlineListDataMessage extends Message {

    private final HashMap<String, Nametag> online;

    public OnlineListDataMessage(HashMap<String, Nametag> online) {
        this.online = online;
    }
    
    public HashMap<String, Nametag> getOnline() {
        return online;
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
