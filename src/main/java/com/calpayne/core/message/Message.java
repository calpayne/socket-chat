package com.calpayne.core.message;

import com.google.gson.Gson;

/**
 *
 * @author Cal Payne
 */
public class Message {

    private MessageType type;
    private final String from;
    private final String message;

    /**
     * @param from the sender
     * @param message the message
     */
    public Message(String from, String message) {
        this(MessageType.NORMAL, from, message);
    }

    /**
     * @param type the type
     * @param from the sender
     * @param message the message
     */
    public Message(MessageType type, String from, String message) {
        this.type = type;
        this.from = from;
        this.message = message;
    }

    /**
     * @return the sender
     */
    public String getFrom() {
        return from;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return string of the message
     */
    @Override
    public String toString() {
        return "<div class=\"" + type.getTypeClass() + "\"><b>" + from + ":</b> " + Messages.addEmojis(message) + "</div>\n";
    }

    /**
     * @return JSON string of the message
     */
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
