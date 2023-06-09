package at.aau.wagnis.GameStateTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import at.aau.wagnis.Adjacency;
import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;
import at.aau.wagnis.gamestate.GameData;
import at.aau.wagnis.gamestate.LobbyState;
import at.aau.wagnis.server.GameServer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class LobbyStateTest {
    LobbyState lobbyState;
    @Mock
    Player player;

    GameServer gameServer;

    @BeforeEach
    void setUp() {
        gameServer = mock(GameServer.class);
        lobbyState=new LobbyState();
        lobbyState.setGameServer(gameServer);
    }

    @Test
    void testSeedGenerator() {
        assertEquals(84,lobbyState.getSeed().length());
    }

    @Test
    void testHubGeneration() {
        assertEquals(42,lobbyState.getHubs().size());
        assertEquals(7,lobbyState.getHubsPerLine());

        int hubId = 100;
        List<Hub> hubs = lobbyState.getHubs();
        Collections.sort(hubs);

        for(Hub h : hubs){
            assertEquals(hubId,h.getId());
            hubId++;
        }
    }

    @Test
    void testFindHubById() {
        Hub testHub=lobbyState.findHubById(100);
        assertEquals(100,testHub.getId());

        testHub=lobbyState.findHubById(110);
        assertEquals(110,testHub.getId());

        testHub=lobbyState.findHubById(1000);
        assertNull(testHub);
    }

    @Test
    void testAdjacencies() {
        lobbyState.getAdjacencies().clear();
        lobbyState.setAdjacencies("171015121717101717123456123456123456123456123456123456123456123456123456123456123456");
        assertNotEquals(null,lobbyState.getAdjacencies());
        for(Adjacency a :lobbyState.getAdjacencies()){
            assertNotEquals(null,a.getHub1());
            assertNotEquals(null,a.getHub2());
            assertNotEquals(a.getHub1(),a.getHub2());
        }
        assertTrue(lobbyState.getAdjacencies().size()>42);

        assertEquals(100,lobbyState.getAdjacencies().get(0).getHub1().getId());
        assertEquals(108,lobbyState.getAdjacencies().get(0).getHub2().getId());

        assertEquals(101,lobbyState.getAdjacencies().get(1).getHub1().getId());
        assertEquals(102,lobbyState.getAdjacencies().get(1).getHub2().getId());

        assertEquals(102,lobbyState.getAdjacencies().get(2).getHub1().getId());
        assertEquals(109,lobbyState.getAdjacencies().get(2).getHub2().getId());

        assertEquals(103,lobbyState.getAdjacencies().get(3).getHub1().getId());
        assertEquals(104,lobbyState.getAdjacencies().get(3).getHub2().getId());

        assertEquals(103,lobbyState.getAdjacencies().get(4).getHub1().getId());
        assertEquals(110,lobbyState.getAdjacencies().get(4).getHub2().getId());

        assertEquals(104,lobbyState.getAdjacencies().get(5).getHub1().getId());
        assertEquals(110,lobbyState.getAdjacencies().get(5).getHub2().getId());

        assertEquals(106,lobbyState.getAdjacencies().get(7).getHub1().getId());
        assertEquals(113,lobbyState.getAdjacencies().get(7).getHub2().getId());

        assertEquals(107,lobbyState.getAdjacencies().get(8).getHub1().getId());
        assertEquals(115,lobbyState.getAdjacencies().get(8).getHub2().getId());

        assertEquals(108,lobbyState.getAdjacencies().get(9).getHub1().getId());
        assertEquals(115,lobbyState.getAdjacencies().get(9).getHub2().getId());


    }
    @Test
    void testPlayer(){
        GameData mockGameData = mock(GameData.class);
        when(gameServer.getGameData()).thenReturn(mockGameData);
        assertEquals(0,lobbyState.getPlayers().size());
        lobbyState.addPlayer("10.0.0.1");
        assertEquals(1,lobbyState.getPlayers().size());

        verify(gameServer).getGameData();
    }

    @Test
    void testGetGameData(){
        GameData mockGameData = mock(GameData.class);
        when(gameServer.getGameData()).thenReturn(mockGameData);
        assertNotNull(lobbyState.getGameData());
    }
    @Test
    void testGetSeed(){
        String seed = lobbyState.getSeed();
        assertNotNull(seed);
        assertEquals(84,seed.length());
    }

    @Test
    void testGetHubs(){
        List<Hub> hubs = lobbyState.getHubs();
        assertNotNull(hubs);
        assertEquals(42,hubs.size());
        assertEquals(7,lobbyState.getHubsPerLine());
    }

    @Test
    void testGetPlayers(){
        assertNotNull(lobbyState.getPlayers());
    }
    @Test
    void testGetAdjacencies(){
        assertNotNull(lobbyState.getAdjacencies());
        assertTrue(lobbyState.getAdjacencies().size()>1);
        assertTrue(lobbyState.getAdjacencies().size()<85);
    }
}