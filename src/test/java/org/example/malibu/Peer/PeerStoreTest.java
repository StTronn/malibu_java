package org.example.malibu.Peer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PeerStoreTest {
    private PeerStore peerStore;
    private static final String HOSTS_FILE = "hosts.txt";
    private List<Host> testHosts;

    @BeforeEach
    void setUp() {
        // Create test hosts
        testHosts = Arrays.asList(
            new Host("localhost:8080"),
            new Host("192.168.1.1:9090"),
            new Host("example.com:7070")
        );
        peerStore = new PeerStore(testHosts);
    }

    @AfterEach
    void tearDown() {
//         Clean up the hosts file after each test
         File file = new File(HOSTS_FILE);
         if (file.exists()) {
             file.delete();
         }
    }

    @Test
    void testStoreAndLoad() {
        // Store the hosts
        peerStore.store();

        // Create a new PeerStore instance to load the data

        // Verify the loaded hosts match the original hosts
        assertEquals(testHosts.size(), peerStore.getLiveHosts().size(), 
            "Number of loaded hosts should match original");
        
        testHosts.forEach(host -> {
            String hostKey = host.getKeyString();
            assertTrue(peerStore.getLiveHosts().containsKey(hostKey), 
                "Loaded PeerStore should contain host " + hostKey);
        });
    }

    @Test
    void testLoadNonExistentFile() {
        // Delete the file if it exists
        File file = new File(HOSTS_FILE);
        if (file.exists()) {
            file.delete();
        }

        // Create a new PeerStore and load from non-existent file
        PeerStore newPeerStore = new PeerStore();
        newPeerStore.load();

        // Verify the file was created
        assertTrue(file.exists(), "hosts.txt should be created if it doesn't exist");
    }

    @Test
    void testEmptyFile() {
        // Create an empty file
        try {
            Files.writeString(new File(HOSTS_FILE).toPath(), "");
        } catch (IOException e) {
            fail("Failed to create empty hosts file: " + e.getMessage());
        }

        // Load from empty file
        PeerStore newPeerStore = new PeerStore();
        newPeerStore.load();

        // No exception should be thrown
        assertTrue(true, "Should handle empty file without errors");
    }

    @Test
    void testConstructorWithHosts() {
        // Test the constructor that takes a List<Host>
        PeerStore store = new PeerStore(testHosts);
        
        // Store the hosts to verify the internal state
        store.store();
        
        // Verify all test hosts were properly stored
        testHosts.forEach(host -> {
            String hostKey = host.getKeyString();
            try {
                String fileContent = Files.readString(new File(HOSTS_FILE).toPath());
                assertTrue(fileContent.contains(hostKey), 
                    "Constructor should properly store host " + hostKey);
            } catch (IOException e) {
                fail("Failed to read hosts file: " + e.getMessage());
            }
        });
    }
    @Test
    void testReadFromFile() {
        // Write test data to file
        List<String> hostStrings = testHosts.stream()
            .map(Host::getKeyString)
            .collect(Collectors.toList());
        
        try {
            Files.writeString(new File(HOSTS_FILE).toPath(), String.join(",", hostStrings));
        } catch (IOException e) {
            fail("Failed to write test data to hosts file: " + e.getMessage());
        }

        // Create new PeerStore and load from file
        PeerStore peerStore = new PeerStore();
        peerStore.load();

        // Verify each test host was loaded correctly
        testHosts.forEach(host -> {
            String hostKey = host.getKeyString();
            try {
                String fileContent = Files.readString(new File(HOSTS_FILE).toPath());
                assertTrue(fileContent.contains(hostKey),
                    "File should contain host " + hostKey + " after loading");
            } catch (IOException e) {
                fail("Failed to read hosts file: " + e.getMessage());
            }
        });
    }

} 