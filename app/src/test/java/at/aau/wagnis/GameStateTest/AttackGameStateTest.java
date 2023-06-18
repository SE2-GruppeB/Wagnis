package at.aau.wagnis.GameStateTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import android.widget.Button;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    @Mock
    private AttackGameState gameState3;
    @Mock
    private Hub sourceHub3;
    @Mock
    private Hub targetHub3;

    private Player attacker;
    private Player defender;
    private Player attacker2;
    private Player defender2;
    private Player attacker3;
    private Player defender3;

    @BeforeEach
    void setup() {
        Button sourceButtonMock = Mockito.mock(Button.class);
        Button targetButtonMock = Mockito.mock(Button.class);

        Button sourceButtonMock2 = Mockito.mock(Button.class);
        Button targetButtonMock2 = Mockito.mock(Button.class);

        Button sourceButtonMock3 = Mockito.mock(Button.class);
        Button targetButtonMock3 = Mockito.mock(Button.class);

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

        sourceHub3 = new Hub(sourceButtonMock3);
        targetHub3 = new Hub(targetButtonMock3);

        attacker3 = new Player(5);
        defender3 = new Player(6);

        sourceHub3.setOwner(attacker3);
        targetHub3.setOwner(defender3);

        attacker3.addHub(sourceHub3);
        defender3.addHub(targetHub3);

        sourceHub3.setAmountTroops(1);
        targetHub3.setAmountTroops(1);

        gameState3 = new AttackGameState(sourceHub3, targetHub3);
    }

    /**
     * Testet den Angriff, wo die `attack()`-Methode aufgerufen wird.
     */
    @Test
    void testAttack() {
        gameState.attack();
        assertTrue(sourceHub.getAmountTroops() < 3 || targetHub.getAmountTroops() < 2);

        //assertTrue(attacker.getOwnedHubs().contains(targetHub) || defender.getOwnedHubs().contains(sourceHub));
    }

    /**
     * Testet einen ungültigen Angriff, wo die Quell-Hub-Truppenanzahl 1 beträgt.
     */
    @Test
    void testIllegalAttack() {
        sourceHub.setAmountTroops(1);

        assertThrows(IllegalArgumentException.class, () -> gameState.attack());
    }

    /**
     * Testet einen fehlgeschlagenen Angriff, wobei die Ziel-Hub-Truppenanzahl 0 beträgt.
     */
    @Test
    void testAttackFailed() {
        targetHub.setAmountTroops(0);

        assertThrows(IllegalArgumentException.class, () -> gameState.attack());
    }

    /**
     * Testet, ob das Spiel gewonnen wurde, wen ein Spieler alle 42 Hubs besitzt.
     */
    @Test
    void testGameWon() {
        Player player = new Player(1);
        for (int i = 0; i < 42; i++) {
            player.addHub(new Hub(Mockito.mock(Button.class)));
        }
        assertTrue(gameState.gameWon(player));
    }

    /**
     * Testet, ob das Spiel nicht gewonnen wurde, wenn ein Spieler weniger als 42 Hubs besitzt.
     */
    @Test
    void testGameWonFalse() {
        Player player = new Player(1);
        for (int i = 0; i < 20; i++) {
            player.addHub(new Hub(Mockito.mock(Button.class)));
        }
        assertFalse(gameState.gameWon(player));
    }

    /**
     * Testet die Anzahl der  Würfe des Angreifers bei einem Angriff mit 2 Truppen.
     */
    @Test
    void testAttackerDiceRoll(){
        gameState.attack();
        assertEquals(2,gameState.getTestAttackerDiceRoll());
    }

    /**
     * Testet den Würfelwert des Angreifers bei einem Angriff mit 3 Truppen.
     */
    @Test
    void testAttackerDiceRoll2(){
        gameState2.attack();
        assertEquals(3,gameState2.getTestAttackerDiceRoll());
    }

    /**
     * Testet den Würfelwert des Verteidigers bei einem Angriff wenn der Verteidiger 2 Truppen am Hub hat.
     */
    @Test
    void testDefenderDiceRoll(){
        gameState.attack();
        assertEquals(2,gameState.getTestDefenderDiceRoll());
    }

    /**
     * Testet den Würfelwert des Verteidigers bei einem Angriff wenn der Verteidiger 2 Truppe hat .
     */
    @Test
    void testDefenderDiceRoll2(){
        gameState2.attack();
        assertEquals(2,gameState2.getTestDefenderDiceRoll());
    }

    /**
     * Testet, ob der Spieler ein Angreifer ist.
     */
    @Test
    void testIsAttacker(){
        gameState.setAttacker(true);
        assertTrue(gameState.isAttacker());
    }

    /**
     * Testet, ob der Spieler ein Verteidiger ist.
     */
    @Test
    void testIsDefender(){
        gameState.setDefender(true);
        assertTrue(gameState.isDefender());
    }

    /**
     * Testet, erstellen eines sourceHubs.
     */
    @Test
    void testGetSourceHub(){
        gameState.setSourceHub(sourceHub);
        assertEquals(sourceHub, gameState.getSourceHub());
    }
    /**
     * Testet, erstellen eines targetHubs.
     */
    @Test
    void testGetTargetHub(){
        gameState.setTargetHub(targetHub);
        assertEquals(targetHub, gameState.getTargetHub());
    }
    /**
     * Testet, abrufen der SourceHubId.
     */
    @Test
    void testGetSourceHubId(){
        gameState.setSourceHubId(5);
        assertEquals(5, gameState.getSourceHubId());
    }
    /**
     * Testet, abrufen der TargetHubId.
     */
    @Test
    void testGetTargetHubId(){
        gameState.setTargetHubId(5);
        assertEquals(5, gameState.getTargetHubId());
    }
    /**
     * Testet, ob eine IllegalArgumentException ausgelöst wird wenn Source- und TargetHub = 1.
     */
    @Test
    void testAttackFailedIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> {
            gameState3.attack();
        });
    }
}
