package com.calpayne.message;

/**
 *
 * @author Cal Payne
 */
public enum MessageType {
    NORMAL("nm"),
    SERVER("sm");

    private final String typeClass;

    private MessageType(String typeClass) {
        this.typeClass = typeClass;
    }

    public String getTypeClass() {
        return typeClass;
    }
}
