package at.aau.wagnis.server.communication.connection;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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

@RunWith(MockitoJUnitRunner.class)
public class NetworkServerConnectionTest {

    @Mock private ActiveDeserializingReader<ClientCommand> input;
    @Mock private ActiveSerializingWriter<ClientOriginatedServerCommand> output;
    @Mock private Consumer<ClientCommand> commandConsumer;

    private NetworkServerConnection subject;

    @Before
    public void setup() {
        subject = new NetworkServerConnection(input, output, commandConsumer);
    }

    @Test
    public void startStartsReaderAndWriter() {
        // when
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

        // when & then
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                subject::start
        );

        assertSame(thrownFromWriter, ex);
    }

    @Test
    public void closeClosesReaderAndWriter() {
        // when
        subject.close();

        // then
        verify(input).close();
        verify(output).close();
    }

    @Test
    public void onErrorClosesReaderAndWriter() {
        // when
        subject.onError();

        // then
        verify(input).close();
        verify(output).close();
    }

    @Test
    public void sendCallsWriter() {
        // given
        ClientOriginatedServerCommand input = mock(ClientOriginatedServerCommand.class);

        // when
        subject.send(input);

        // then
        verify(output).write(input);
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
        NetworkServerConnection result = NetworkServerConnection.fromSocket(
                socket,
                threadFactory,
                commandConsumer
        );

        // then
        assertNotNull(result);

        verify(socket).getInputStream();
        verify(socket).getOutputStream();
        verifyNoInteractions(commandConsumer);
    }

    @SuppressWarnings("unchecked")
    private <T, U> Function<T, U> mockFunction() {
        return mock(Function.class);
    }
}
