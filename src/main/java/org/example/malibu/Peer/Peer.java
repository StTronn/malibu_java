package org.example.malibu.Peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.example.malibu.protocol.Protocol;
import org.example.malibu.protocol.messages.Message;

import lombok.Data;

@Data
public class Peer {

	private final Host host;
	private final Socket clientSocket;
	private final Protocol protocol;

	private boolean isLive = false;
	private boolean handshakeComplete = false;
	private BufferedReader in;
	private PrintWriter out;

	public Peer(Host host) {
		this.host = host;
		this.clientSocket = null;
		this.protocol = new Protocol();
	}
	
	public Peer(Host host, Socket clientSocket) throws IOException {
		this.host = host;
		this.clientSocket = clientSocket;
		this.protocol = new Protocol();
		this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		this.out = new PrintWriter(clientSocket.getOutputStream(), true);
		listen();
	}

	public void listen() throws IOException {
		String message;
		System.out.println("Listening for messages from " + host.getKeyString());
		while ((message = in.readLine()) != null) {
			System.out.println("Received message from " + host.getKeyString() + ": " + message);
			Message msg = protocol.processMessage(message);
			System.out.println("Sending message to " + host.getKeyString() + ": " + msg.toString());
			out.println(protocol.sendMessage(msg));
		}
	}


}
