package com.calpayne.core.message;

/**
 *
 * @author Cal Payne
 */
public enum MessageType {
    NORMAL("nm"),
    SERVER("sm"),
    WHISPER("wm"),
    ERROR("em");

    private final String typeClass;

    /**
     * @param typeClass the CSS class for the message type
     */
    private MessageType(String typeClass) {
        this.typeClass = typeClass;
    }

    /**
     * @return the type class
     */
    public String getTypeClass() {
        return typeClass;
    }
}
