package at.aau.wagnis.server.communication.command;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import at.aau.wagnis.gamestate.GameLogicState;

class HandleConnectionClosedCommandTest {

    @Mock
    private GameLogicState gameLogicState;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void executeCallsGameLogicStateWithId() {
        // given
        int id = 1;
        HandleConnectionClosedCommand subject = new HandleConnectionClosedCommand(id);

        // when
        subject.execute(gameLogicState);

        // then
        verify(gameLogicState, only()).handleClosedConnection(id);
    }
}
