package at.aau.wagnis.GameStateTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import android.content.Context;
import android.widget.Button;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import at.aau.wagnis.GlobalVariables;
import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;
import at.aau.wagnis.gamestate.MoveTroopsState;

public class MoveTroopsStateTest {
    private Hub sourceHub;
    private Hub targetHub;
    private MoveTroopsState moveTroopsState;


    @BeforeEach
    public void setUp() {
        sourceHub = new Hub(Mockito.mock(Button.class));
        targetHub = new Hub(Mockito.mock(Button.class));
        moveTroopsState = new MoveTroopsState(sourceHub, targetHub);
        Player player = new Player(1);
        sourceHub.setOwner(player);
        targetHub.setOwner(player);
    }

    @Test
    public void testValidMove() {

        sourceHub.setAmountTroops(5);
        targetHub.setAmountTroops(0);

        moveTroopsState.move(3);

        assertEquals(2, sourceHub.getAmountTroops());
        assertEquals(3, targetHub.getAmountTroops());
    }


    @Test
    public void testInvalidMove() {

        sourceHub.setAmountTroops(0);
        targetHub.setAmountTroops(1);

        assertThrows(IllegalArgumentException.class, () -> {
            moveTroopsState.move(1);
        });
    }


    @Test
    public void testDifferentPlayersMove() {

        sourceHub.setOwner(new Player(1));
        targetHub.setOwner(new Player(2));

        sourceHub.setAmountTroops(0);
        targetHub.setAmountTroops(2);

        assertThrows(IllegalArgumentException.class, () -> {
            moveTroopsState.move(1);
        });

    }

}
