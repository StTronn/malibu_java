package org.example.malibu.protocol.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type",
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    defaultImpl = ErrorMessage.class
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Hello.class, name = "hello"),
    @JsonSubTypes.Type(value = GetPeers.class, name = "getPeers"),
    @JsonSubTypes.Type(value = ErrorMessage.class, name = "error")
})
public abstract class Message {
    public abstract String getType();
}
