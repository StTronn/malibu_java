package org.example.malibu.protocol.messages;

import java.util.Objects;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Hello extends Message {
    private final String type = MessageType.HELLO.toString();
    private String version = "0.8.0";
    private String agent = "Malibu";

    @Builder
    public Hello(String version, String agent) {
      this.version = version != null ? version : "0.8.0";
      this.agent = agent != null ? agent : "Malibu"; 
    }

    public boolean isValid() {
        return Objects.nonNull(version) && version.equals("0.8.0") && Objects.nonNull(agent) && agent.equals("Malibu");
    }
}
