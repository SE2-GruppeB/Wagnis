package at.aau.wagnis.server.communication.command;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import at.aau.wagnis.gamestate.GameLogicState;

class HandleNewConnectionCommandTest {

    @Mock private GameLogicState gameLogicState;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void executeCallsGameLogicState() {
        // given
        int id = 1;
        HandleNewConnectionCommand subject = new HandleNewConnectionCommand(id);

        // when
        subject.execute(gameLogicState);

        // then
        verify(gameLogicState).handleNewConnection(id);
    }
}
