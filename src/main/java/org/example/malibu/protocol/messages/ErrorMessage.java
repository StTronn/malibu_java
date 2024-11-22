package org.example.malibu.protocol.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorMessage extends Message {
  private String message;

  private String type = "error";


  @Builder
  public ErrorMessage(String message) {
    this.message = message;
  }

  public static final ErrorMessage NOT_DEFINED_TYPE = ErrorMessage.builder()
          .message("Not a defined type")
          .build();


  public static final ErrorMessage HANDSHAKE_ALREADY_COMPLETED = ErrorMessage.builder()
          .message("Handshake already completed")
          .build();

  public static final ErrorMessage INVALID_HELLO_MESSAGE = ErrorMessage.builder()
          .message("Invalid hello message")
          .build();

}
