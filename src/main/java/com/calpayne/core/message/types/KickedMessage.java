package com.calpayne.core.message.types;

import com.calpayne.core.message.Message;
import com.google.gson.Gson;

/**
 *
 * @author Cal Payne
 */
public class KickedMessage extends Message {
    
    private final boolean kicked;
    
    public KickedMessage() {
        kicked = true;
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
