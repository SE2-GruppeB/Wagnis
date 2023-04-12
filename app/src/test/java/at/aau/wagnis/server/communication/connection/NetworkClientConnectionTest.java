package at.aau.wagnis.server.communication.connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import at.aau.wagnis.server.communication.command.ClientCommand;
import at.aau.wagnis.server.communication.command.ClientOriginatedServerCommand;
import at.aau.wagnis.server.communication.serialization.ActiveDeserializingReader;
import at.aau.wagnis.server.communication.serialization.ActiveSerializingWriter;

@RunWith(MockitoJUnitRunner.class)
public class NetworkClientConnectionTest {

    @Mock private ActiveDeserializingReader<ClientOriginatedServerCommand> input;
    @Mock private ActiveSerializingWriter<ClientCommand> output;
    @Mock private ClientConnectionBus bus;
    @Mock private ClientCommand clientCommand;
    @Mock private ClientOriginatedServerCommand serverCommand;

    private NetworkClientConnection subject;

    @Before
    public void setup() {
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
        subject.setClientConnectionBus(bus, 0);
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
        subject.setClientConnectionBus(bus, 0);

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
        subject.setClientConnectionBus(bus, 0);

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
        subject.setClientConnectionBus(bus, 0);
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
        subject.setClientConnectionBus(bus, clientId);

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
        subject.setClientConnectionBus(bus, clientId);

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
        subject.setClientConnectionBus(bus, clientId);

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
        subject.setClientConnectionBus(bus, clientId);

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
}