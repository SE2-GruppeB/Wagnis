package at.aau.wagnis.GameStateTest;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;
import at.aau.wagnis.gamestate.AttackGameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

import android.widget.Button;

public class AttackGameStateTest {

    @Mock private Hub sourceHub;
    @Mock private Hub targetHub;
    @Mock private AttackGameState gameState;

    private Player attacker;
    private Player defender;

    @BeforeEach
    public void setup() {
        Button sourceButtonMock = Mockito.mock(Button.class);
        Button targetButtonMock = Mockito.mock(Button.class);

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

        int sourceHubId = sourceHub.getId();
        int targetHubId = targetHub.getId();
        gameState = new AttackGameState(sourceHubId, targetHubId);
    }

    @Test
    public void testAttack() {
        gameState.attack();

        assertTrue(sourceHub.getAmountTroops() < 3 || targetHub.getAmountTroops() < 2);
        //assertTrue(attacker.getOwnedHubs().contains(targetHub) || defender.getOwnedHubs().contains(sourceHub));
    }

    @Test
    public void testIllegalAttack() {
        sourceHub.setAmountTroops(1);

        assertThrows(IllegalArgumentException.class, () -> gameState.attack());
    }

    @Test
    public void testAttackFailed() {
        targetHub.setAmountTroops(0);

        assertThrows(IllegalArgumentException.class, () -> gameState.attack());
    }

    @Test
    public void testGameWon() {
        // add test case for game won scenario
    }
}
