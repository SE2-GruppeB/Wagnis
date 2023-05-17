package at.aau.wagnis.GameStateTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import at.aau.wagnis.Adjacency;
import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;
import at.aau.wagnis.gamestate.LobbyState;

import static org.junit.jupiter.api.Assertions.*;

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

        int hubId = 100;
        for(Hub h : lobbyState.getHubs()){
            assertEquals(hubId,h.getId());
            hubId++;
        }

        assertEquals(7,lobbyState.getHubsPerLine());
    }

    @Test
    void testFindHubById() {
        Hub testHub=lobbyState.findHubById(100);
        assertEquals(100,testHub.getId());

        testHub=lobbyState.findHubById(10);
        assertEquals(null,testHub);

        testHub=lobbyState.findHubById(200);
        assertEquals(null,testHub);
    }

    @Test
    void testAdjacencies() {
        lobbyState.setAdjacencies("123455103456123456123456123456123456123456123456123456123456123456123456123456123456");

        assertNotEquals(null,lobbyState.getAdjacencies());
        for(Adjacency a :lobbyState.getAdjacencies()){
            assertNotEquals(null,a.getHub1());
            assertNotEquals(null,a.getHub2());
            assertNotEquals(a.getHub1(),a.getHub2());
        }
        assertTrue(lobbyState.getAdjacencies().size()>42);

        assertEquals(100,lobbyState.getAdjacencies().get(0).getHub1().getId());
        assertEquals(101,lobbyState.getAdjacencies().get(0).getHub2().getId());

        assertEquals(100,lobbyState.getAdjacencies().get(1).getHub1().getId());
        assertEquals(107,lobbyState.getAdjacencies().get(1).getHub2().getId());

        assertEquals(101,lobbyState.getAdjacencies().get(2).getHub1().getId());
        assertEquals(102,lobbyState.getAdjacencies().get(2).getHub2().getId());

        assertEquals(101,lobbyState.getAdjacencies().get(2).getHub1().getId());
        assertEquals(107,lobbyState.getAdjacencies().get(2).getHub2().getId());

        assertEquals(102,lobbyState.getAdjacencies().get(3).getHub1().getId());
        assertEquals(108,lobbyState.getAdjacencies().get(3).getHub2().getId());

        assertEquals(103,lobbyState.getAdjacencies().get(4).getHub1().getId());
        assertEquals(104,lobbyState.getAdjacencies().get(4).getHub2().getId());

        assertEquals(103,lobbyState.getAdjacencies().get(4).getHub1().getId());
        assertEquals(109,lobbyState.getAdjacencies().get(4).getHub2().getId());

    }
    @Test
    void testPlayer(){
        assertEquals(0,lobbyState.getPlayers().size());
        lobbyState.addPlayer(player);
        assertEquals(1,lobbyState.getPlayers().size());
    }
}