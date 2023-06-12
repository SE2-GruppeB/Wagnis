package at.aau.wagnis.GameStateTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import at.aau.wagnis.Hub;
import at.aau.wagnis.gamestate.GameData;
import at.aau.wagnis.gamestate.ReinforceGameState;
import at.aau.wagnis.server.GameServer;

class ReinforceGameStateTest {


    private List<Integer> hubsNull;

    private List<Integer> troopsNull;

    private List<Integer> hubs= new ArrayList<Integer>();

    private List<Integer> troops= new ArrayList<Integer>();

    private  ReinforceGameState rgstate;
    @Mock
    private GameServer gameServer;
    @Mock
    private GameData gameData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        hubs.add(1);
        hubs.add(2);
        troops.add(3);
        troops.add(4);
    }

    @Test
    void constructorHubsTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ReinforceGameState(hubsNull, troops);
        });
        String expectedMessage = "Hubs cannot be null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void constructorTroopsTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ReinforceGameState(hubs, troopsNull);
        });
        String expectedMessage = "TroopstoDeploy cannot be null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void hubGetterTest() {
        rgstate = new ReinforceGameState(hubs, troops);
        assertEquals(rgstate.getHubs(),hubs);
    }

    @Test
    void troopGetterTest() {
        rgstate = new ReinforceGameState(hubs, troops);
        assertEquals(rgstate.getTroopsToDeploy(),troops);
    }

    @Test
    void setHubsTest() {
        rgstate = new ReinforceGameState(hubs,troops);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            rgstate.setHubs(hubsNull);
        });
        String expectedMessage = "Hubs cannot be null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void setTroopsTest() {
        rgstate = new ReinforceGameState(hubs,troops);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            rgstate.setTroopsToDeploy(troopsNull);
        });
        String expectedMessage = "TroopstoDeploy cannot be null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void reinforceWithNoParaTest(){
        rgstate= new ReinforceGameState(hubs,troops);
        troops.add(3);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            rgstate.reinforce();
        });
        String expectedMessage = "Hubs and troopsToDeploy must have the same size";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void reinforceWithNoParaTest2(){
        hubs.clear();
        troops.clear();
        rgstate= new ReinforceGameState(hubs,troops);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            rgstate.reinforce();
        });
        String expectedMessage = "There must be at least one";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void reinforceWithParaTest(){
        troops.add(3);
        rgstate= new ReinforceGameState(hubs,troops);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            rgstate.reinforce(hubs,troops);
        });
        String expectedMessage = "Hubs and troopsToDeploy must have the same size";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void reinforceWithParaTest2(){
        hubs.clear();
        troops.clear();
        rgstate = new ReinforceGameState(hubs,troops);


        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            rgstate.reinforce(hubs,troops);
        });
        String expectedMessage = "There must be at least one";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }


    @Test
    void reinforceSingleTest(){
        rgstate= new ReinforceGameState(hubs,troops);
        gameServer = mock(GameServer.class);
        gameData = mock(GameData.class);
        rgstate.setGameServer(gameServer);
        ArrayList<Hub> hubList = new ArrayList<Hub>();
        Hub hub, hub1;
        hub=new Hub(hubs.get(0));
        hub1=new Hub(hubs.get(1));
        hub.setAmountTroops(0);
        hub1.setAmountTroops(0);
        hubList.add(hub);
        hubList.add(hub1);

        when(gameServer.getGameData()).thenReturn(gameData);
        when(gameData.getHubs()).thenReturn(hubList);

        rgstate.reinforceSingle(hubs.get(0),troops.get(0));
        assertEquals(hub.getAmountTroops(),troops.get(0));
        assertEquals(0,hub1.getAmountTroops());
        rgstate.reinforceSingle(hubs.get(1),troops.get(1));
        assertEquals(hub1.getAmountTroops(),troops.get(1));

    }

}
