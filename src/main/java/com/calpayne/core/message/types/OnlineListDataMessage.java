package com.calpayne.core.message.types;

import com.calpayne.core.message.Message;
import com.google.gson.Gson;
import java.util.ArrayList;

/**
 *
 * @author Cal Payne
 */
public class OnlineListDataMessage extends Message {

    private final ArrayList<String> online;

    public OnlineListDataMessage(ArrayList<String> online) {
        this.online = online;
    }
    
    public ArrayList<String> getOnline() {
        return online;
    }

    @Override
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
