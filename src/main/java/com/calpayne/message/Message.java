package com.calpayne.message;

/**
 *
 * @author Cal Payne
 */
public class Message {

    private final String from;
    private final String message;

    public Message(String from, String message) {
        this.from = from;
        this.message = message;
    }

    @Override
    public String toString() {
        return "<div class=\"nm\"><b>" + from + ":</b> " + message + "</div>\n";
    }

}
