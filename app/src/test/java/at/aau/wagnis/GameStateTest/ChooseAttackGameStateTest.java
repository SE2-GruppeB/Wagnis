package at.aau.wagnis.GameStateTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import at.aau.wagnis.gamestate.AttackGameState;
import at.aau.wagnis.gamestate.ChooseAttackGameState;
import at.aau.wagnis.gamestate.GameData;
import at.aau.wagnis.server.GameServer;

class ChooseAttackGameStateTest {
    @Mock
    private GameServer gameServer;
    @Mock
    private GameData gameState;

    private ChooseAttackGameState chooseAttackGameState;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        chooseAttackGameState = new ChooseAttackGameState();
        chooseAttackGameState.setGameServer(gameServer);
    }

    @Test
    void chooseAttack_shouldSetNewAttackGameState() {
        int playerId = 1;
        int sourceHubId = 2;
        int targetHubId = 3;

        chooseAttackGameState.chooseAttack(playerId, sourceHubId, targetHubId);

        verify(gameServer).getGameData();
        verify(gameServer).setGameLogicState(any(AttackGameState.class));
    }

    @Test
    void chooseAttack_shouldSetCorrectAttackGameState() {
        int playerId = 1;
        int sourceHubId = 2;
        int targetHubId = 3;

        chooseAttackGameState.chooseAttack(playerId, sourceHubId, targetHubId);

        verify(gameServer).setGameLogicState(argThat(argument ->
                argument instanceof AttackGameState &&
                        ((AttackGameState) argument).getSourceHubId() == sourceHubId &&
                        ((AttackGameState) argument).getTargetHubId() == targetHubId
        ));
    }
}
