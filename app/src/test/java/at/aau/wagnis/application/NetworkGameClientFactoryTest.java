package at.aau.wagnis.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.Socket;
import java.util.function.Function;

import at.aau.wagnis.client.GameClient;
import at.aau.wagnis.server.communication.connection.NetworkServerConnection;

class NetworkGameClientFactoryTest {

    @Mock
    private Function<Runnable, Thread> threadFactory;
    @Mock
    private NetworkGameClientFactory.SocketFactory socketFactory;
    @Mock
    private Socket socket;
    @Mock
    private NetworkGameClientFactory.NetworkServerConnectionFactory serverConnectionFactory;
    @Mock
    private NetworkServerConnection serverConnection;

    private NetworkGameClientFactory subject;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        subject = new NetworkGameClientFactory(threadFactory, socketFactory, serverConnectionFactory);
    }

    @Test
    void sanityCheck() throws IOException {
        // given
        String address = "testAddress";
        int port = 54321;

        when(socketFactory.create(any(), anyInt())).thenReturn(socket);
        when(serverConnectionFactory.fromSocket(any(), any())).thenReturn(serverConnection);

        // when
        GameClient result = subject.createGameClient(address, port);

        // then
        assertNotNull(result);
        verify(socketFactory).create(address, port);
        verify(serverConnectionFactory).fromSocket(socket, threadFactory);
        verify(serverConnection).start();
    }

    @Test
    void rethrowsIOException() throws IOException {
        // given
        Exception socketFactoryException = new IOException();
        when(socketFactory.create(any(), anyInt())).thenThrow(socketFactoryException);

        // when & then
        IOException ex = assertThrows(
                IOException.class,
                () -> subject.createGameClient("someAddress", 54321)
        );

        assertSame(socketFactoryException, ex);
    }

    @Test
    void closesSocketIfAlreadyOpenOnIOException() throws IOException {
        // given
        Exception connectionFactoryException = new IOException();
        when(socketFactory.create(any(), anyInt())).thenReturn(socket);
        when(serverConnectionFactory.fromSocket(any(), any())).thenThrow(connectionFactoryException);

        // when & then
        IOException ex = assertThrows(
                IOException.class,
                () -> subject.createGameClient("someAddress", 54321)
        );

        assertSame(connectionFactoryException, ex);
        verify(socket).close();
    }

    @Test
    void suppressesSecondIOExceptionOnClose() throws IOException {
        // given
        Exception connectionFactoryException = new IOException();
        Exception closeException = new IOException();

        when(socketFactory.create(any(), anyInt())).thenReturn(socket);
        when(serverConnectionFactory.fromSocket(any(), any())).thenThrow(connectionFactoryException);
        doThrow(closeException).when(socket).close();

        // when & then
        IOException ex = assertThrows(
                IOException.class,
                () -> subject.createGameClient("someAddress", 54321)
        );

        assertSame(connectionFactoryException, ex);
        verify(socket).close();
        assertEquals(1, connectionFactoryException.getSuppressed().length);
        assertEquals(closeException, connectionFactoryException.getSuppressed()[0]);
    }
}
