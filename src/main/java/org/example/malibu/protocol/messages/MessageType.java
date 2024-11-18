package org.example.malibu.protocol.messages;

public enum MessageType {
    HELLO("hello"),
    GET_PEERS("getPeers"),
    ERROR("error");

    private final String type;

    MessageType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    public static MessageType fromString(String text) {
        for (MessageType type : MessageType.values()) {
            if (type.type.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
} 