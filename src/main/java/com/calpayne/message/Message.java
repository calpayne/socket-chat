package com.calpayne.message;

import com.google.gson.Gson;

/**
 *
 * @author Cal Payne
 */
public class Message {

    private MessageType type;
    private final String from;
    private final String message;

    public Message(String from, String message) {
        this(MessageType.NORMAL, from, message);
    }

    public Message(MessageType type, String from, String message) {
        this.type = type;
        this.from = from;
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "<div class=\"" + type.getTypeClass() + "\"><b>" + from + ":</b> " + message + "</div>\n";
    }

    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
