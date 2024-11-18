package org.example.malibu.protocol;

import lombok.AllArgsConstructor;
import org.example.malibu.protocol.messages.ErrorMessage;
import org.example.malibu.protocol.messages.Hello;
import org.example.malibu.protocol.messages.Message;
import org.example.malibu.protocol.messages.GetPeers;
import org.example.malibu.protocol.messages.MessageType;

@AllArgsConstructor
public class Protocol {

  private final Mapper mapper;

  public Protocol(){
    mapper = new Mapper();
  }

  public Message processMessage(String message){
    Message messageObj = mapper.unmarshall(message);
    if (messageObj == null){
      return ErrorMessage.builder()
              .message("Invalid json mapping")
              .build();
    }
    return processMessage(messageObj);
  }


  public Message processMessage(Message message){
    try {
      MessageType messageType = MessageType.fromString(message.getType());
      switch (messageType) {
        case HELLO:
          return processHello(message);
        case GET_PEERS:
          return processGetPeers(message);
        case ERROR:
          return message;
        default:
          return defaultMessage(message);
      }
    } catch (IllegalArgumentException e) {
      return defaultMessage(message);
    }
  }

  public String sendMessage(Message message){
    return mapper.marshall(message);
  }

  public Message sendHello(){
    return Hello.builder()
            .build();
  }

  private Message processHello(Message message){
    Hello hello = (Hello) message;
    hello.isValid();
    return hello;
  }

  private Message processGetPeers(Message message) {
    GetPeers getPeers = (GetPeers) message;
    // TODO: Implement logic to get peers
    return getPeers;
  }

  private Message defaultMessage(Message message){
    return ErrorMessage.builder()
            .message("Invalid type")
            .build();

  }
}
