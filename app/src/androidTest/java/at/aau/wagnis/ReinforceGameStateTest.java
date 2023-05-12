package at.aau.wagnis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import at.aau.wagnis.gamestate.ReinforceGameState;

public class ReinforceGameStateTest {

    List<Hub> hubsNull;
    List<Integer> TroopsNull;
    List<Hub> hubs;
    List<Integer> Troops;

    @Before
    public void setUp() {

        hubsNull = null;
        TroopsNull = null;

        Troops.add(1);
        Troops.add(2);
    }

    @Test
    public void constructorHubsTest(){
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {new ReinforceGameState(hubsNull,Troops);});
        String expectedMessage = "Hubs cannot be null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage,actualMessage);

    }

    @Test
    public void constructorTroopsTest(){
        // TODO i hab grad ka Ahnung mehr wie i gscheid mocken tue
        /*
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {new ReinforceGameState(hubs,Troopsnull);});
        String expectedMessage = "TroopstoDeploy cannot be null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage,actualMessage);
         */
    }

    //es werden noch weitere tests benötigt
}
