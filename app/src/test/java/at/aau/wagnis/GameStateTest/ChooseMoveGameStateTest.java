package at.aau.wagnis.GameStateTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import at.aau.wagnis.gamestate.ChooseMoveState;
import at.aau.wagnis.gamestate.GameData;
import at.aau.wagnis.gamestate.MoveTroopsState;
import at.aau.wagnis.server.GameServer;
 class ChooseMoveGameStateTest {

    @Mock
    private GameServer gameServer;
    @Mock
    private GameData gameState;
    private ChooseMoveState chooseMoveGameState;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        chooseMoveGameState = new ChooseMoveState();
        chooseMoveGameState.setGameServer(gameServer);
    }

    /**
     * Testet, ob `chooseMove()` einen neuen `MoveTroopsState` festlegt, wenn `gameServer.setGameLogicState()` aufgerufen wird.
     */
    @Test
    void chooseMove_shouldSetNewMoveGameState() {
        int playerId = 1;
        int sourceHubId = 2;
        int targetHubId = 3;
        int numTroops = 5;

        chooseMoveGameState.chooseMove(playerId, sourceHubId, targetHubId, numTroops);

        verify(gameServer).getGameData();
        verify(gameServer).setGameLogicState(any(MoveTroopsState.class));
    }

    /**
     * Testet, ob `chooseMove()` den richtigen `MoveTroopsState` festlegt, wenn `gameServer.setGameLogicState()` aufgerufen wird.
     */
    @Test
    void chooseMove_shouldSetCorrectMoveGameState() {
        int playerId = 1;
        int sourceHubId = 2;
        int targetHubId = 3;
        int numTroops = 5;

        chooseMoveGameState.chooseMove(playerId, sourceHubId, targetHubId, numTroops);

        verify(gameServer).setGameLogicState(argThat(argument ->
                argument instanceof MoveTroopsState &&
                        ((MoveTroopsState) argument).getSourceHubId() == sourceHubId &&
                        ((MoveTroopsState) argument).getTargetHubId() == targetHubId
        ));
    }

}
