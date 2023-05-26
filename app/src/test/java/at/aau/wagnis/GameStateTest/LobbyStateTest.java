package at.aau.wagnis.GameStateTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import at.aau.wagnis.Adjacency;
import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;
import at.aau.wagnis.gamestate.LobbyState;
import at.aau.wagnis.server.GameServer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LobbyStateTest {
    LobbyState lobbyState;
    @Mock
    Player player;

    @BeforeEach
    void setUp() {
        lobbyState=new LobbyState();
    }

    @Test
    void testSeedGenerator() {
        assertEquals(84,lobbyState.getSeed().length());
    }

    @Test
    void testHubGeneration() {
        assertEquals(42,lobbyState.getHubs().size());
        assertEquals(1, lobbyState.getHubs().get(1).getId());

        int hubId = 0;
        for(Hub h : lobbyState.getHubs()){
            assertEquals(hubId,h.getId());
            hubId++;
        }

        assertEquals(7,lobbyState.getHubsPerLine());
    }

    @Test
    void testFindHubById() {
        Hub testHub=lobbyState.findHubById(0);
        assertEquals(0,testHub.getId());

        testHub=lobbyState.findHubById(10);
        assertEquals(10,testHub.getId());

        testHub=lobbyState.findHubById(200);
        assertNull(testHub);
    }

    @Test
    void testAdjacencies() {
        lobbyState.getAdjacencies().clear();
        lobbyState.setAdjacencies("123455103456123456123456123456123456123456123456123456123456123456123456123456123456");
        assertNotEquals(null,lobbyState.getAdjacencies());
        for(Adjacency a :lobbyState.getAdjacencies()){
            assertNotEquals(null,a.getHub1());
            assertNotEquals(null,a.getHub2());
            assertNotEquals(a.getHub1(),a.getHub2());
        }
        assertTrue(lobbyState.getAdjacencies().size()>42);

        assertEquals(0,lobbyState.getAdjacencies().get(0).getHub1().getId());
        assertEquals(7,lobbyState.getAdjacencies().get(0).getHub2().getId());

        assertEquals(0,lobbyState.getAdjacencies().get(1).getHub1().getId());
        assertEquals(7,lobbyState.getAdjacencies().get(1).getHub2().getId());

        assertEquals(1,lobbyState.getAdjacencies().get(2).getHub1().getId());
        assertEquals(8,lobbyState.getAdjacencies().get(2).getHub2().getId());

        assertEquals(1,lobbyState.getAdjacencies().get(3).getHub1().getId());
        assertEquals(7,lobbyState.getAdjacencies().get(3).getHub2().getId());

        assertEquals(2,lobbyState.getAdjacencies().get(4).getHub1().getId());
        assertEquals(8,lobbyState.getAdjacencies().get(4).getHub2().getId());

        assertEquals(3,lobbyState.getAdjacencies().get(5).getHub1().getId());
        assertEquals(10,lobbyState.getAdjacencies().get(5).getHub2().getId());

        assertEquals(3,lobbyState.getAdjacencies().get(6).getHub1().getId());
        assertEquals(9,lobbyState.getAdjacencies().get(6).getHub2().getId());

    }
    @Test
    void testPlayer(){
        assertEquals(0,lobbyState.getPlayers().size());
        lobbyState.addPlayer(player);
        assertEquals(1,lobbyState.getPlayers().size());
    }
}