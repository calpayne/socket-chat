package com.calpayne.core.message;

import com.google.gson.Gson;
import java.util.Date;

/**
 *
 * @author Cal Payne
 */
public class Message implements Comparable<Message> {

    private MessageType type;
    private final Date date = new Date();
    private final String from;
    private String message;

    public Message() {
        this(null, null, null);
    }

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

    public Date getDate() {
        return date;
    }

    /**
     * @return the sender
     */
    public String getFrom() {
        return from;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public boolean isUserMessage() {
        return true;
    }

    @Override
    public int compareTo(Message m) {
        return this.date.compareTo(m.date);
    }

}
