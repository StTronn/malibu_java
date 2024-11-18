package org.example.malibu.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.malibu.protocol.messages.ErrorMessage;
import org.example.malibu.protocol.messages.Message;

import java.io.IOException;

public class Mapper {
    private final ObjectMapper objectMapper;

    public Mapper() {
        this.objectMapper = new ObjectMapper();
    }

    public Message unmarshall(String message) {
        try {
            if (message == null || message.isEmpty()) {
                return ErrorMessage.builder()
                        .message("Invalid json mapping")
                        .build();
            }
            return objectMapper.readValue(message, Message.class);
        } catch (JsonMappingException e) {
            System.err.println("Invalid json mapping: " + e.getMessage());
            return ErrorMessage.builder()
                    .message("Invalid json mapping")
                    .build();
        } catch (IOException e) {
            System.err.println("IO error unmarshalling: " + e.getMessage());
            return ErrorMessage.builder()
                    .message("IO Error")
                    .build();
        }
    }

    public String marshall(Message message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            System.err.println("Invalid json marshalling: " + e.getMessage());
            return ErrorMessage.builder()
                    .message("Invalid json marshalling")
                    .build().toString();
        }
    }
}
