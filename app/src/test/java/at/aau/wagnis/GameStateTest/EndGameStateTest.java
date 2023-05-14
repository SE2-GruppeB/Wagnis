package at.aau.wagnis.GameStateTest;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import at.aau.wagnis.gamestate.EndGameState;
import at.aau.wagnis.server.GameServer;

class EndGameStateTest {

    @Mock
    GameServer gameServer;

    EndGameState endGameState;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        endGameState = new EndGameState();
        endGameState.setGameServer(gameServer);
    }
    @Test
    void end() {
        endGameState.end();

        //verify(gameServer).broadcastCommand(any());
        verify(gameServer).close();
    }
}
