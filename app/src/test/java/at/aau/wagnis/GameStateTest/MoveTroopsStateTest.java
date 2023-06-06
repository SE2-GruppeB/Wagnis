package at.aau.wagnis.GameStateTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import android.content.Context;
import android.widget.Button;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import at.aau.wagnis.Adjacency;
import at.aau.wagnis.GlobalVariables;
import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;
import at.aau.wagnis.gamestate.MoveTroopsState;

public class MoveTroopsStateTest {
    private Hub sourceHub;
    private Hub targetHub;

    @BeforeEach
    public void setUp() {
        sourceHub = new Hub(Mockito.mock(Button.class));
        targetHub = new Hub(Mockito.mock(Button.class));
        Adjacency adjacency = new Adjacency(sourceHub, targetHub);
        moveTroopsState = new MoveTroopsState(sourceHub, targetHub);
        Player player = new Player(1);
        sourceHub.setOwner(player);
        targetHub.setOwner(player);
        GlobalVariables.adjacencies.add(adjacency);
    }

    /**
     * Testet eine gültige Truppenbewegung, indem  `move()` aufgerufen wird und überprüft ob die Anzahl der Truppen in den Hubs korrekt aktualisiert wurde.
     */
    @Test
    public void testValidMove() {
        sourceHub.setAmountTroops(5);
        targetHub.setAmountTroops(0);

        moveTroopsState.move(3);

        assertEquals(2, sourceHub.getAmountTroops());
        assertEquals(3, targetHub.getAmountTroops());
    }

    /**
     * Testet eine ungültige Truppenbewegung, indem die Methode `move()` mit einer ungültigen Anzahl von Truppen aufgerufen wird .
     */
    @Test
    public void testInvalidMove() {
        sourceHub.setAmountTroops(0);
        targetHub.setAmountTroops(1);

        assertFalse(moveTroopsState.move(1));
    }


    /**
     * Testet eine Truppenbewegung zwischen Hubs.
     */
    @Test
    public void testDifferentPlayersMove() {
        sourceHub.setOwner(new Player(1));
        targetHub.setOwner(new Player(2));

        sourceHub.setAmountTroops(0);
        targetHub.setAmountTroops(2);

        assertFalse(moveTroopsState.move(1));
    }
}
