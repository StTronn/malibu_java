package org.example.malibu;

import java.io.IOException;

import org.example.malibu.Network.Network;

public class Server {

  private final Network network;

  public Server(Integer portNumber) throws IOException {
    this.network = new Network(portNumber);
  }

  public static void main(String[] args) {
    try {
      Server server = new Server(18010);
      System.out.println("Server started on port 18010");

      try {
        server.network.listen();
      } catch (IOException e) {
        System.err.println("Error in server: " + e.getMessage());
      }

    } catch (IOException e) {
      System.err.println("Could not start server: " + e.getMessage());
    }
  }
}
