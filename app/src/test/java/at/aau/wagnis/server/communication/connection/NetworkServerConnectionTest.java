package at.aau.wagnis.server.communication.connection;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.function.Function;

import at.aau.wagnis.server.communication.command.ClientCommand;
import at.aau.wagnis.server.communication.command.ClientOriginatedServerCommand;
import at.aau.wagnis.server.communication.serialization.ActiveDeserializingReader;
import at.aau.wagnis.server.communication.serialization.ActiveSerializingWriter;

class NetworkServerConnectionTest {

    @Mock private ActiveDeserializingReader<ClientCommand> input;
    @Mock private ActiveSerializingWriter<ClientOriginatedServerCommand> output;
    @Mock private Consumer<ClientCommand> commandConsumer;

    private NetworkServerConnection subject;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        subject = new NetworkServerConnection(input, output);
    }

    @Test
    void startFailsWhenNoCommandConsumerHasBeenSet() {
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                subject::start
        );

        assertEquals("Command consumer has not been set", ex.getMessage());
    }

    @Test
    void startStartsReaderAndWriter() {
        // given
        subject.setCommandConsumer(commandConsumer);

        // when
        subject.start();

        // then
        verify(input).start(any(), any());
        verify(output).start(any());
    }

    @Test
    void startPassesOnIllegalStateExceptionFromReader() {
        // given
        subject.setCommandConsumer(commandConsumer);
        IllegalStateException thrownFromReader = new IllegalStateException();
        doThrow(thrownFromReader).when(input).start(any(), any());

        // when & then
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                subject::start
        );

        assertSame(thrownFromReader, ex);
    }

    @Test
    void startPassesOnIllegalStateExceptionFromWriter() {
        // given
        subject.setCommandConsumer(commandConsumer);
        IllegalStateException thrownFromWriter = new IllegalStateException();
        doThrow(thrownFromWriter).when(output).start(any());

        // when & then
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                subject::start
        );

        assertSame(thrownFromWriter, ex);
    }

    @Test
    void closeClosesReaderAndWriter() {
        // when
        subject.close();

        // then
        verify(input).close();
        verify(output).close();
    }

    @Test
    void onErrorClosesReaderAndWriter() {
        // when
        subject.onError();

        // then
        verify(input).close();
        verify(output).close();
    }

    @Test
    void sendCallsWriter() {
        // given
        ClientOriginatedServerCommand input = mock(ClientOriginatedServerCommand.class);

        // when
        subject.send(input);

        // then
        verify(output).write(input);
    }

    @Test
    void onReceiveNotifiesConsumer() {
        // given
        subject.setCommandConsumer(commandConsumer);
        ClientCommand clientCommand = mock(ClientCommand.class);

        // when
        subject.onReceive(clientCommand);

        // then
        verify(commandConsumer).accept(clientCommand);
    }

    @Test
    void fromSocketSanityCheck() throws IOException {
        // given
        InputStream inputStream = mock(InputStream.class);
        OutputStream outputStream = mock(OutputStream.class);

        Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(inputStream);
        when(socket.getOutputStream()).thenReturn(outputStream);

        Function<Runnable, Thread> threadFactory = mockFunction();

        // when
        NetworkServerConnection result = NetworkServerConnection.fromSocket(
                socket,
                threadFactory
        );

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
