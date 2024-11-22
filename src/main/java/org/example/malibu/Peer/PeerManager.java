package org.example.malibu.Peer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class PeerManager {

	private final Map<String, Peer> peers;
	private final PeerStore peerStore;

	public PeerManager(List<Host> hosts) {
		this.peers = new HashMap<>();
		this.peerStore = new PeerStore(hosts);
	}

	public void addPeer(Peer peer) {
		peers.put(peer.getHost().getKeyString(), peer);
		peerStore.store();
	}

	public void removePeer(Host host){
		peers.remove(host.getKeyString());
		peerStore.store();
	}
}
