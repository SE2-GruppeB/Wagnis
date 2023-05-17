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
        assertEquals(lobbyState.getSeed().length(),84);
    }

    @Test
    void testHubGeneration() {
        assertEquals(lobbyState.getHubs().size(),42);

        int hubId = 100;
        for(Hub h : lobbyState.getHubs()){
            assertEquals(h.getId(),hubId);
            hubId++;
        }

        assertEquals(lobbyState.getHubsPerLine(),7);
    }

    @Test
    void testFindHubById() {
        Hub testHub=lobbyState.findHubById(100);
        assertEquals(testHub.getId(),100);

        testHub=lobbyState.findHubById(10);
        assertEquals(testHub,null);

        testHub=lobbyState.findHubById(200);
        assertEquals(testHub,null);
    }

    @Test
    void testAdjacencies() {
        lobbyState.setAdjacencies("123455103456123456123456123456123456123456123456123456123456123456123456123456123456");

        assertNotEquals(lobbyState.getAdjacencies(),null);
        for(Adjacency a :lobbyState.getAdjacencies()){
            assertNotEquals(a.getHub1(),null);
            assertNotEquals(a.getHub2(),null);
            assertNotEquals(a.getHub1(),a.getHub2());
        }
        assertTrue(lobbyState.getAdjacencies().size()>42);

        assertEquals(lobbyState.getAdjacencies().get(0).getHub1().getId(),100);
        assertEquals(lobbyState.getAdjacencies().get(0).getHub2().getId(),101);

        assertEquals(lobbyState.getAdjacencies().get(1).getHub1().getId(),100);
        assertEquals(lobbyState.getAdjacencies().get(1).getHub2().getId(),107);

        assertEquals(lobbyState.getAdjacencies().get(2).getHub1().getId(),101);
        assertEquals(lobbyState.getAdjacencies().get(2).getHub2().getId(),102);

        assertEquals(lobbyState.getAdjacencies().get(2).getHub1().getId(),101);
        assertEquals(lobbyState.getAdjacencies().get(2).getHub2().getId(),107);

        assertEquals(lobbyState.getAdjacencies().get(3).getHub1().getId(),102);
        assertEquals(lobbyState.getAdjacencies().get(3).getHub2().getId(),108);

        assertEquals(lobbyState.getAdjacencies().get(4).getHub1().getId(),103);
        assertEquals(lobbyState.getAdjacencies().get(4).getHub2().getId(),104);

        assertEquals(lobbyState.getAdjacencies().get(4).getHub1().getId(),103);
        assertEquals(lobbyState.getAdjacencies().get(4).getHub2().getId(),109);

    }
    @Test
    void testPlayer(){
        assertEquals(lobbyState.getPlayers().size(),0);
        lobbyState.addPlayer(player);
        assertEquals(lobbyState.getPlayers().size(),1);
    }
}