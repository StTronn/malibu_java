package org.example.malibu.Peer;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

public class Host {
    @Getter
    private final String ip;
    @Getter 
    private final int port;
    @Getter
    private final String keyString;

    public Host(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.keyString = ip + ":" + port;
    }

    public Host(String keyString) {
        String[] parts = keyString.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid key string format. Expected format: ip:port");
        }
        this.ip = parts[0];
        this.port = Integer.parseInt(parts[1]);
        this.keyString = keyString;
    }

    public boolean isSameHost(Host other) {
        return this.keyString.equals(other.keyString);
    }

    public static class BootstrapServer {
        public static final Host SERVER_1 = new Host("127.0.0.1", 8080);
        public static final Host SERVER_2 = new Host("127.0.0.1", 8081); 
        public static final Host SERVER_3 = new Host("127.0.0.1", 8082);
    }

    public static List<Host> getBootstrapServers() {
        return Arrays.asList(BootstrapServer.SERVER_1, BootstrapServer.SERVER_2, BootstrapServer.SERVER_3);
    }
}
