package org.example.malibu.Peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

import org.example.malibu.protocol.Protocol;
import org.example.malibu.protocol.messages.ErrorMessage;
import org.example.malibu.protocol.messages.Hello;
import org.example.malibu.protocol.messages.Message;
import org.example.malibu.protocol.messages.MessageType;


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
		initialize();
	}

	public void initialize() throws IOException {
		this.sendHello();
	}

	public void sendMessage(Message message) throws IOException {
		System.out.println("Sending message to " + host.getKeyString() + ": " + message.toString());
		out.println(protocol.sendMessage(message));
	}

	public void sendHello() throws IOException {
		sendMessage(protocol.sendHello());
	}

	public void listen() throws IOException {
		String message;
		System.out.println("Listening for messages from " + host.getKeyString());
		while ((message = in.readLine()) != null) {
			System.out.println("Received message from " + host.getKeyString() + ": " + message);
			Message incomingMessage = protocol.processMessage(message);
			Message outgoingMessage = messageHandlerMessage(incomingMessage);

			sendMessage(outgoingMessage);
		}
	}

	public Message messageHandlerMessage(Message message){
		if(Objects.isNull(message) || Objects.isNull(message.getType())){
			return ErrorMessage.NOT_DEFINED_TYPE;
		}

		switch (message.getType()) {
			case "HELLO":
				return handleHello((Hello) message);
			default:
				return ErrorMessage.NOT_DEFINED_TYPE;
		}

	}

	private Message handleHello(Hello hello){
		if(isHandshakeComplete()){
			System.out.println("Handshake already completed");
			return ErrorMessage.HANDSHAKE_ALREADY_COMPLETED;
		}
		if(!hello.isValid()){
			System.out.println("Invalid hello message");
			return ErrorMessage.INVALID_HELLO_MESSAGE;
		}
		setHandshakeComplete(true);
		return null;
	}


}
