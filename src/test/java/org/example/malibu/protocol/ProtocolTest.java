package org.example.malibu.protocol;

import org.example.malibu.protocol.messages.ErrorMessage;
import org.example.malibu.protocol.messages.GetPeers;
import org.example.malibu.protocol.messages.Hello;
import org.example.malibu.protocol.messages.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProtocolTest {

    private Protocol protocol;

    @BeforeEach
    void setUp() {
        protocol = new Protocol();
    }

    @Test
    void processMessage_ValidHelloMessage() {
        // Given
        String helloJson = "{\"type\":\"hello\",\"version\":\"0.9.0\",\"agent\":\"Malibu\"}";

        // When
        Message result = protocol.processMessage(helloJson);

        // Then
        assertTrue(result instanceof Hello);
        assertEquals("hello", result.getType());
        assertEquals("0.9.0", ((Hello) result).getVersion());
        assertEquals("Malibu", ((Hello) result).getAgent());
				assertFalse(((Hello) result).isValid());
    }

    @Test
    void processMessage_ValidGetPeersMessage() {
        // Given
        String getPeersJson = "{\"type\":\"getPeers\"}";

        // When
        Message result = protocol.processMessage(getPeersJson);

        // Then
        assertTrue(result instanceof GetPeers);
        assertEquals("getPeers", result.getType());
    }

    @Test
    void processMessage_InvalidJsonFormat() {
        // Given
        String invalidJson = "invalid json format";

        // When
        Message result = protocol.processMessage(invalidJson);

        // Then
        assertTrue(result instanceof ErrorMessage);
        // assertEquals("Invalid json mapping", ((ErrorMessage) result).getMessage());
    }

    @Test
    void processMessage_UnknownMessageType() {
        // Given
        String unknownTypeJson = "{\"type\":\"unknownType\"}";

        // When
        Message result = protocol.processMessage(unknownTypeJson);

        // Then
        assertTrue(result instanceof ErrorMessage);
        // assertEquals("Invalid type", ((ErrorMessage) result).getMessage());
    }

    @Test
    void processMessage_NullMessage() {
        // Given
        String nullJson = null;

        // When
        Message result = protocol.processMessage(nullJson);

        // Then
        assertTrue(result instanceof ErrorMessage);
        assertEquals("Invalid json mapping", ((ErrorMessage) result).getMessage());
    }

    @Test
    void processMessage_EmptyMessage() {
        // Given
        String emptyJson = "";

        // When
        Message result = protocol.processMessage(emptyJson);

        // Then
        assertTrue(result instanceof ErrorMessage);
        assertEquals("Invalid json mapping", ((ErrorMessage) result).getMessage());
    }

    @Test
    void processMessage_MissingTypeField() {
        // Given
        String jsonWithoutType = "{\"someField\":\"someValue\"}";

        // When
        Message result = protocol.processMessage(jsonWithoutType);

        // Then
        assertTrue(result instanceof ErrorMessage);
        assertEquals("Invalid json mapping", ((ErrorMessage) result).getMessage());
    }
} 