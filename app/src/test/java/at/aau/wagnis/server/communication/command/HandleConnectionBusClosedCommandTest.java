package at.aau.wagnis.server.communication.command;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import at.aau.wagnis.gamestate.GameLogicState;

class HandleConnectionBusClosedCommandTest {

    @Mock private GameLogicState gameLogicState;

    private HandleConnectionBusClosedCommand subject;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        subject = new HandleConnectionBusClosedCommand();
    }

    @Test
    void callsGameLogicState() {
        // when
        subject.execute(gameLogicState);

        // then
        verify(gameLogicState, only()).handleConnectionBusClosed();
    }
}
