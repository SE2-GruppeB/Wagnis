package at.aau.wagnis.GameStateTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import at.aau.wagnis.Hub;
import at.aau.wagnis.gamestate.ReinforceGameState;

class ReinforceGameStateTest {

    List<Integer> hubsNull;
    List<Integer> troopsNull;
    List<Hub> hubs;
    List<Integer> troops;

    @BeforeEach
    void setUp() {

        hubsNull = null;
        troopsNull = null;

        troops = new ArrayList<>();
        troops.add(1);
        troops.add(2);
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

    //es werden noch weitere tests benötigt
}
