package org.example.malibu.Peer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class PeerStore {
	private Map<String, Peer> liveHosts;

	public PeerStore(List<Host> hosts){
		this.liveHosts = hosts.stream().collect(Collectors.toMap(Host::getKeyString, host -> new Peer(host)));
		load();
	}

	public PeerStore(){
		liveHosts = new HashMap<>();
		load();
	}

	private static final String HOSTS_FILE = "hosts.txt";

	public void store() {
		Map<String, Host> hosts = readFromFile().stream().collect(Collectors.toMap(Host::getKeyString, Function.identity()));

		try {
			// Open writer in overwrite mode (false = don't append)
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(HOSTS_FILE, true))) {
				String hostsData ="," + liveHosts.values().stream()
					.filter(peer -> !hosts.containsKey(peer.getHost().getKeyString()))
					.map(peer -> peer.getHost().getKeyString())
					.collect(Collectors.joining(","));
				writer.write(hostsData);
			}
		} catch (IOException e) {
			System.err.println("Error writing hosts to file: " + e.getMessage());
		}
	}

	public void load() {
		List<Host> hosts = readFromFile();
		if(hosts.isEmpty()){
			return; 
		}
		this.liveHosts = hosts.stream()
			.collect(Collectors.toMap(Host::getKeyString, host -> new Peer(host)));
	}

	private List<Host> readFromFile() {
		File file = new File(HOSTS_FILE);
		if (!file.exists()) {
			try {
				file.createNewFile();
				return new ArrayList<>(); 
			} catch (IOException e) {
				System.err.println("Error creating hosts file: " + e.getMessage());
				return new ArrayList<>(); 
			}
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(HOSTS_FILE))) {
			String line = reader.readLine();
			if (line != null && !line.trim().isEmpty()) {
				List<Host> hosts = Arrays.stream(line.split(","))
					.filter(host -> host != null && !host.trim().isEmpty())
					.map(Host::new)
					.collect(Collectors.toList());
				return hosts;
			}
		} catch (IOException e) {
			System.err.println("Error reading hosts from file: " + e.getMessage());
		}
		return new ArrayList<>(); 

	}

	public void addHost(Host host) {
		if (host != null) {
			this.liveHosts.put(host.getKeyString(), new Peer(host));
			store();
		}
	}

	///TODO: this will not remove the host from the file
	public void removeHost(Host host) {
		if (host != null) {
			this.liveHosts.remove(host.getKeyString());
			store(); 
		}
	}



}
