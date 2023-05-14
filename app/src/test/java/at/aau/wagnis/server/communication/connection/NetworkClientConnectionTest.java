package at.aau.wagnis.server.communication.connection;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.function.Function;

import at.aau.wagnis.server.communication.command.ClientCommand;
import at.aau.wagnis.server.communication.command.ClientOriginatedServerCommand;
import at.aau.wagnis.server.communication.serialization.ActiveDeserializingReader;
import at.aau.wagnis.server.communication.serialization.ActiveSerializingWriter;


public class NetworkClientConnectionTest {

    @Mock private ActiveDeserializingReader<ClientOriginatedServerCommand> input;
    @Mock private ActiveSerializingWriter<ClientCommand> output;
    @Mock private ClientConnectionBus bus;
    @Mock private ClientCommand clientCommand;
    @Mock private ClientOriginatedServerCommand serverCommand;

    private NetworkClientConnection subject;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        subject = new NetworkClientConnection(input, output);
    }

    @Test
    public void startThrowsIllegalStateExceptionIfBusHasNotBeenSet() {
        // when & then
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                subject::start
        );

        assertEquals("Connection bus has not been set", ex.getMessage());
    }

    @Test
    public void startStartsReaderAndWriter() {
        // when
        subject.init(bus, 0);
        subject.start();

        // then
        verify(input).start(any(), any());
        verify(output).start(any());
    }

    @Test
    public void startPassesOnIllegalStateExceptionFromReader() {
        // given
        IllegalStateException thrownFromReader = new IllegalStateException();
        doThrow(thrownFromReader).when(input).start(any(), any());
        subject.init(bus, 0);

        // when & then
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                subject::start
        );

        assertSame(thrownFromReader, ex);
    }

    @Test
    public void startPassesOnIllegalStateExceptionFromWriter() {
        // given
        IllegalStateException thrownFromWriter = new IllegalStateException();
        doThrow(thrownFromWriter).when(output).start(any());
        subject.init(bus, 0);

        // when & then
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                subject::start
        );

        assertSame(thrownFromWriter, ex);
    }

    @Test
    public void sendObjectCallsWriter() {
        // given
        subject.init(bus, 0);
        subject.start();

        // when
        subject.send(clientCommand);

        // then
        verify(output).write(clientCommand);
    }

    @Test
    public void closeNotifiesBusIfSet() {
        // given
        int clientId = 1;
        subject.init(bus, clientId);

        // when
        subject.close();

        // then
        verify(input).close();
        verify(output).close();
        verify(bus).handleClosedConnection(clientId);
    }

    @Test
    public void closeClosesReaderAndWriterIfBusIsNotSet() {
        // when
        subject.close();

        // then
        verify(input).close();
        verify(output).close();
    }

    @Test
    public void callingCloseTwiceDoesNotNotifyBusTwice() {
        // given
        int clientId = 1;
        subject.init(bus, clientId);

        // when
        subject.close();
        subject.close();

        // then
        verify(bus).handleClosedConnection(clientId);
        verifyNoMoreInteractions(bus);
    }

    @Test
    public void onErrorClosesConnection() {
        // given
        int clientId = 1;
        subject.init(bus, clientId);

        // when
        subject.onError();

        // then
        verify(input).close();
        verify(output).close();
        verify(bus).handleClosedConnection(clientId);
    }

    @Test
    public void onReceiveNotifiesBusIfSet() {
        // given
        int clientId = 1;
        subject.init(bus, clientId);

        // when
        subject.onReceive(serverCommand);

        // then
        verify(serverCommand).setClientId(clientId);
        verify(bus).reportReceivedCommand(serverCommand);
    }

    @Test
    public void onReceiveThrowsIllegalStateExceptionIfBusHasNotBeenSet() {
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> subject.onReceive(serverCommand)
        );

        assertEquals("Received an object before bus configuration was set", ex.getMessage());
    }

    @Test
    public void fromSocketSanityCheck() throws IOException {
        // given
        InputStream inputStream = mock(InputStream.class);
        OutputStream outputStream = mock(OutputStream.class);

        Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(inputStream);
        when(socket.getOutputStream()).thenReturn(outputStream);

        Function<Runnable, Thread> threadFactory = mockFunction();

        // when
        NetworkClientConnection result = NetworkClientConnection.fromSocket(socket, threadFactory);

        // then
        assertNotNull(result);

        verify(socket).getInputStream();
        verify(socket).getOutputStream();
    }

    @SuppressWarnings("unchecked")
    private <T, U> Function<T, U> mockFunction() {
        return mock(Function.class);
    }
}