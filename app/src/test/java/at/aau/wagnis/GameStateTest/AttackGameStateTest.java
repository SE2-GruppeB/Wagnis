package at.aau.wagnis.GameStateTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import android.widget.Button;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;
import at.aau.wagnis.gamestate.AttackGameState;

class AttackGameStateTest {

    @Mock
    private Hub sourceHub;
    @Mock
    private Hub targetHub;
    @Mock
    private Hub sourceHub2;
    @Mock
    private Hub targetHub2;
    @Mock
    private AttackGameState gameState;
    @Mock
    private AttackGameState gameState2;

    private Player attacker;
    private Player defender;
    private Player attacker2;
    private Player defender2;

    @BeforeEach
    void setup() {
        Button sourceButtonMock = Mockito.mock(Button.class);
        Button targetButtonMock = Mockito.mock(Button.class);

        Button sourceButtonMock2 = Mockito.mock(Button.class);
        Button targetButtonMock2 = Mockito.mock(Button.class);

        sourceHub = new Hub(sourceButtonMock);
        targetHub = new Hub(targetButtonMock);

        attacker = new Player(1);
        defender = new Player(2);

        sourceHub.setOwner(attacker);
        targetHub.setOwner(defender);

        attacker.addHub(sourceHub);
        defender.addHub(targetHub);

        sourceHub.setAmountTroops(3);
        targetHub.setAmountTroops(2);

        gameState = new AttackGameState(sourceHub, targetHub);

        sourceHub2 = new Hub(sourceButtonMock2);
        targetHub2 = new Hub(targetButtonMock2);

        attacker2 = new Player(3);
        defender2 = new Player(4);

        sourceHub2.setOwner(attacker2);
        targetHub2.setOwner(defender2);

        attacker2.addHub(sourceHub2);
        defender2.addHub(targetHub2);

        sourceHub2.setAmountTroops(4);
        targetHub2.setAmountTroops(4);

        gameState2 = new AttackGameState(sourceHub2, targetHub2);
    }

    @Test
    void testAttack() {
        gameState.attack();
        assertTrue(sourceHub.getAmountTroops() < 3 || targetHub.getAmountTroops() < 2);

        //assertTrue(attacker.getOwnedHubs().contains(targetHub) || defender.getOwnedHubs().contains(sourceHub));
    }

    @Test
    void testIllegalAttack() {
        sourceHub.setAmountTroops(1);

        assertThrows(IllegalArgumentException.class, () -> gameState.attack());
    }

    @Test
    void testAttackFailed() {
        targetHub.setAmountTroops(0);

        assertThrows(IllegalArgumentException.class, () -> gameState.attack());
    }

    @Test
    void testGameWon() {
        Player player = new Player(1);
        for (int i = 0; i < 42; i++) {
            player.addHub(new Hub(Mockito.mock(Button.class)));
        }
        assertTrue(gameState.gameWon(player));
    }

    @Test
    void testGameWonFalse() {
        Player player = new Player(1);
        for (int i = 0; i < 20; i++) {
            player.addHub(new Hub(Mockito.mock(Button.class)));
        }
        assertFalse(gameState.gameWon(player));
    }

    @Test
    void testAttackerDiceRoll(){
        gameState.attack();
        assertEquals(2,gameState.testAttackerDiceRoll);
    }

    @Test
    void testAttackerDiceRoll2(){
        gameState2.attack();
        assertEquals(3,gameState2.testAttackerDiceRoll);
    }


    @Test
    void testDefenderDiceRoll(){
        gameState.attack();
        assertEquals(2,gameState.testDefenderDiceRoll);
    }

    @Test
    void testDefenderDiceRoll2(){
        gameState2.attack();
        assertEquals(2,gameState2.testDefenderDiceRoll);
    }

    @Test
    void testIsAttacker(){
        gameState.setAttacker(true);
        assertTrue(gameState.isAttacker());
    }

    @Test
    void testIsDefender(){
        gameState.setDefender(true);
        assertTrue(gameState.isDefender());
    }

}