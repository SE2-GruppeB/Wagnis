package at.aau.wagnis.server;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import at.aau.wagnis.gamestate.GameLogicState;
import at.aau.wagnis.server.communication.command.ClientCommand;
import at.aau.wagnis.server.communication.command.ServerCommand;
import at.aau.wagnis.server.communication.connection.ClientConnectionBus;
import at.aau.wagnis.server.communication.connection.ClientConnectionListener;

@Timeout(5)
class GameServerTest {

    @Mock
    private ClientConnectionBus bus;
    @Mock
    private ClientConnectionListener listener;
    @Mock
    private ServerCommand serverCommand;
    @Mock
    private ClientCommand clientCommand;
    @Mock
    private GameLogicState initialGameLogicState;
    @Mock
    private GameLogicState gameLogicState;

    private GameServer subject;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        subject = new GameServer(bus, listener, initialGameLogicState);
    }

    @Test
    void runProcessesCommandsUntilInterrupted() throws InterruptedException {
        // given
        when(bus.getNextCommand())
                .thenReturn(serverCommand)
                .thenThrow(new InterruptedException());

        // when
        subject.run();

        // then
        assertTrue(Thread.interrupted());
        verify(bus, times(2)).getNextCommand();
        verify(serverCommand).execute(initialGameLogicState);
        verifyNoMoreInteractions(serverCommand);
    }

    @Test
    void closeReleasesConnectionResources() {
        // when
        subject.close();

        // then
        verify(listener).close();
        verify(bus).close();
    }

    @Test
    void setGameLogicStateIsAppliedToCommands() throws InterruptedException {
        // given
        when(bus.getNextCommand())
                .thenReturn(serverCommand)
                .thenThrow(new InterruptedException());

        // when
        subject.setGameLogicState(gameLogicState);
        subject.run();

        // then
        assertTrue(Thread.interrupted());
        verify(bus, times(2)).getNextCommand();
        verify(serverCommand).execute(gameLogicState);
        verifyNoMoreInteractions(serverCommand);
    }

    @Test
    void broadcastCommandDelegatesToBus() {
        // when
        subject.broadcastCommand(clientCommand);

        // then
        verify(bus).broadcastCommand(clientCommand);
    }

    @Test
    void runExitsWhenCommandInterruptsThread() throws InterruptedException {
        // given
        when(bus.getNextCommand()).thenReturn(serverCommand);

        doAnswer(invocation -> {
            Thread.currentThread().interrupt();
            return null;
        }).when(serverCommand).execute(any());

        // when
        subject.run();

        // then
        assertTrue(Thread.interrupted());
        verify(bus).getNextCommand();
        verify(serverCommand).execute(initialGameLogicState);
    }
}