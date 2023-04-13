package at.aau.wagnis.server.communication.connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Function;

@RunWith(MockitoJUnitRunner.class)
public class ClientConnectionListenerTest {

    @Mock private ServerSocket serverSocket;
    @Mock private ClientConnectionBus bus;
    @Mock private ClientConnectionListener.ConnectionFactory connectionFactory;
    @Mock private NetworkClientConnection clientConnection;
    @Mock private Socket socket;
    @Mock private Function<Runnable, Thread> threadFactory;
    @Mock private Thread thread;

    private ClientConnectionListener subject;

    @Before
    public void setup() {
        when(threadFactory.apply(any())).thenReturn(thread);

        subject = new ClientConnectionListener(
                serverSocket,
                bus,
                connectionFactory,
                threadFactory
        );
    }

    @Test
    public void startCreatesThread() {
        // when
        subject.start();

        // then
        verify(threadFactory).apply(any());
        verify(thread).start();
    }

    @Test
    public void callingStartTwiceThrowsIllegalStateException() {
        subject.start();

        // when & then
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                subject::start
        );

        assertEquals("Listener has already been started", ex.getMessage());
    }

    @Test
    public void closeBeforeStartClosesServerSocket() throws IOException {
        // when
        subject.close();

        // then
        verify(serverSocket).close();
    }

    @Test
    public void closeAfterStartInterruptsThread() throws IOException {
        // given
        subject.start();

        // when
        subject.close();

        // then
        verify(serverSocket).close();
        verify(thread).interrupt();
    }

    @Test
    public void callingCloseTwiceDoesNotCloseSocketTwice() throws IOException {
        // when
        subject.close();
        subject.close();

        // then
        verify(serverSocket).close();
    }

    @Test
    public void startAfterCloseThrowsIllegalStateException() {
        // given
        subject.close();

        // when & then
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                subject::start
        );

        assertEquals("Listener is closed", ex.getMessage());
    }

    @Test
    public void ioExceptionOnCloseDoesNotCauseAnotherException() throws IOException {
        // given
        doThrow(new IOException()).when(serverSocket).close();

        // when & then
        try {
            subject.close();
        } catch (Exception ex) {
            Assert.fail();
        }
    }

    @Test
    public void handleNextClientCreatesConnection() throws IOException {
        // given
        when(serverSocket.accept()).thenReturn(socket);
        when(connectionFactory.apply(any(), any())).thenReturn(clientConnection);

        // when
        subject.handleNextClient();

        // then
        verify(connectionFactory).apply(socket, threadFactory);
        verify(bus).registerConnection(clientConnection);
        verify(clientConnection).start();
    }

    @Test
    public void handleNextClientClosesServerSocketOnIOException() throws IOException {
        // given
        when(serverSocket.accept()).thenThrow(new IOException());

        // when
        subject.handleNextClient();

        // then
        verify(serverSocket).close();
    }

    @Test
    public void handleNextClientClosesSocketOnIOExceptionAfterAccept() throws IOException {
        // given
        when(serverSocket.accept()).thenReturn(socket);
        when(connectionFactory.apply(any(), any())).thenThrow(new IOException());

        // when
        subject.handleNextClient();

        // then
        verify(socket).close();
        verify(serverSocket).close();
        verifyNoInteractions(bus);
    }

    @Test
    public void handleNextClientStillClosesServerSocketIfClientSocketFailsToClose() throws IOException {
        // given
        when(serverSocket.accept()).thenReturn(socket);
        when(connectionFactory.apply(any(), any())).thenThrow(new IOException());
        doThrow(new IOException()).when(socket).close();

        // when
        subject.handleNextClient();

        // then
        verify(socket).close();
        verify(serverSocket).close();
    }
}
