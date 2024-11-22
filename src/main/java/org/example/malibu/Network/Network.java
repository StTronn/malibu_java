package org.example.malibu.Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.example.malibu.Peer.Host;
import org.example.malibu.Peer.Peer;
import org.example.malibu.Peer.PeerManager;
import org.example.malibu.protocol.Protocol;

import lombok.SneakyThrows;

public class Network {

  private final ServerSocket socket;
  private final Protocol protocol; 
  private final Map<String, Host> liveHosts = Host.getBootstrapServers().stream().collect(Collectors.toMap(Host::getKeyString, Function.identity()));
  private final int maxRetries = 3;
  private final ExecutorService executorService = Executors.newFixedThreadPool(10); // Adjust pool size as needed
  private final PeerManager peerManager;
  public Network(Integer portNumber) throws IOException {
    this.socket = new ServerSocket(portNumber);
    protocol = new Protocol();
    this.peerManager = new PeerManager(Host.getBootstrapServers());
  }

  Network() throws IOException {
    this.socket = new ServerSocket(18010);
    protocol = new Protocol();
    this.peerManager = new PeerManager(Host.getBootstrapServers());
  }

  //TODO: add handling of defragmentation msgs
  public void listen() throws IOException {
    while (true) {
      Socket clientSocket = socket.accept();
      executorService.execute(() -> handleClient(clientSocket));
    }
  }

  private void handleClient(Socket clientSocket) {
    try {
      Peer peer = new Peer(new Host(clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort()), clientSocket);
      peerManager.addPeer(peer);
      // Add your client handling logic here
      // You can move your protocol communication code here
      
    } catch (Exception e) {
      System.err.println("Error handling client: " + e.getMessage());
    } finally {
      try {
        clientSocket.close();
      } catch (IOException e) {
        System.err.println("Error closing client socket: " + e.getMessage());
      }
    }
  }

  public void send(String message, String ipAddress, Integer portNumber) throws IOException {
    for (Host host : liveHosts.values()) {
      boolean messageSent = false;
      int retryCount = 0;
      int maxRetries = 3;
      
      while (!messageSent && retryCount < maxRetries) {
        try {
          Socket socket = new Socket(host.getIp(), host.getPort());
          PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
          out.println(message);
          socket.close();
          messageSent = true;
          break; // Successfully sent, exit loop
        } catch (IOException e) {
          retryCount++;
          System.err.println("Failed to send message to " + host.getKeyString() + 
              ". Attempt " + retryCount + " of " + maxRetries);
          
          if (retryCount == maxRetries) {
            continue; // Try next host
          }

          try {
            Thread.sleep(1000); // Wait 1 second before retrying
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting to retry", ie);
          }
        }
      }
      
      if (messageSent) {
        break; // Message was sent successfully to this host, exit outer loop
      }
    }
  }

  @SneakyThrows
  public void propogate(String message) {
    for (Host host : liveHosts.values()) {
      send(message, host.getIp(), host.getPort());
    }
  }

  private void updateLiveHosts(Host host) {
    if (!liveHosts.containsKey(host.getKeyString())) {
      liveHosts.put(host.getKeyString(), host);
    }
  }

}
