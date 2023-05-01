package at.aau.wagnis.application;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.function.Function;
import java.util.function.Supplier;

import at.aau.wagnis.gamestate.GameLogicState;
import at.aau.wagnis.server.GameServer;

class GameServerFactoryTest {

    @Mock private Function<Runnable, Thread> threadFactory;
    @Mock private Thread thread1;
    @Mock private Thread thread2;
    @Mock private GameServerFactory.ServerSocketFactory serverSocketFactory;
    @Mock private Supplier<GameLogicState> initialGameLogicStateSupplier;
    @Mock private GameLogicState gameLogicState;
    @Mock private ServerSocket serverSocket;

    private GameServerFactory subject;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        subject = new GameServerFactory(threadFactory, serverSocketFactory, initialGameLogicStateSupplier);
    }

    @Test
    void sanityCheck() throws IOException {
        // given
        int port = 54321;
        when(serverSocketFactory.create(anyInt())).thenReturn(serverSocket);
        when(threadFactory.apply(any())).thenReturn(thread1, thread2);
        when(initialGameLogicStateSupplier.get()).thenReturn(gameLogicState);

        // when
        GameServer result = subject.createGameServer(port);

        // then
        assertNotNull(result);

        verify(serverSocketFactory).create(port);

        verify(threadFactory).apply(result);
        verify(thread1).start();
        verify(thread2).start();

        verify(initialGameLogicStateSupplier).get();
        verifyNoMoreInteractions(serverSocketFactory);
    }

    @Test
    void rethrowsIOException() throws IOException {
        // given
        IOException serverSocketFactoryException = new IOException();
        when(serverSocketFactory.create(anyInt())).thenThrow(serverSocketFactoryException);

        // when & then
        IOException ex = assertThrows(
                IOException.class,
                () -> subject.createGameServer(54321)
        );

        assertSame(serverSocketFactoryException, ex);
    }
}
