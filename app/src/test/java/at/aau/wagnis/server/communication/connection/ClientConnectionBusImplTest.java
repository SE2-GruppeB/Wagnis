package at.aau.wagnis.server.communication.connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import at.aau.wagnis.server.communication.command.ClientCommand;
import at.aau.wagnis.server.communication.command.ClientOriginatedServerCommand;
import at.aau.wagnis.server.communication.command.HandleConnectionClosedCommand;
import at.aau.wagnis.server.communication.command.ServerCommand;

@RunWith(MockitoJUnitRunner.class)
public class ClientConnectionBusImplTest {

    @Mock private ClientConnection conn1;
    @Mock private ClientConnection conn2;
    @Mock private ClientConnection conn3;
    @Mock private ClientCommand clientCommand1;
    @Mock private ClientOriginatedServerCommand serverCommand1;
    @Mock private ClientOriginatedServerCommand serverCommand2;

    private ClientConnectionBusImpl subject;

    @Rule
    public Timeout timeout = Timeout.millis(5000);

    @Before
    public void setup() {
        subject = new ClientConnectionBusImpl();
    }

    @Test
    public void registerConnectionSetsIdsInOrder() {
        // when
        subject.registerConnection(conn1);
        subject.registerConnection(conn2);
        subject.registerConnection(conn3);

        // then
        verify(conn1).setClientConnectionBus(subject, 0);
        verify(conn2).setClientConnectionBus(subject, 1);
        verify(conn3).setClientConnectionBus(subject, 2);
        verifyNoMoreInteractions(conn1, conn2, conn3);
    }

    @Test
    public void broadcastCommandNotifiesAllConnections() {
        // given
        subject.registerConnection(conn1);
        subject.registerConnection(conn2);
        subject.registerConnection(conn3);

        // when
        subject.broadcastCommand(clientCommand1);

        // then
        verify(conn1).send(clientCommand1);
        verify(conn2).send(clientCommand1);
        verify(conn3).send(clientCommand1);
    }

    @Test
    public void receivedCommandsAreReturnedInGetNextCommand() throws InterruptedException {
        // given
        subject.reportReceivedCommand(serverCommand1);
        subject.reportReceivedCommand(serverCommand2);

        // when
        ServerCommand result1 = subject.getNextCommand();
        ServerCommand result2 = subject.getNextCommand();

        // then
        assertSame(serverCommand1, result1);
        assertSame(serverCommand2, result2);
    }

    @Test
    public void handleClosedConnectionCreatesNotification() throws InterruptedException {
        // given
        subject.registerConnection(conn1);

        // when
        subject.handleClosedConnection(0);
        ServerCommand resultCommand = subject.getNextCommand();

        // then
        assertTrue(resultCommand instanceof HandleConnectionClosedCommand);
        assertEquals(0, ((HandleConnectionClosedCommand) resultCommand).getClientId());
        assertFalse(subject.hasNextCommand());
    }

    @Test
    public void handleClosedConnectionForUnknownIdCausesIllegalStateException() {
        // given
        subject.registerConnection(conn1);

        // when & then
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> subject.handleClosedConnection(5)
        );

        assertEquals("No connection with id 5 found", ex.getMessage());
    }

    @Test
    public void closeClosesAllKnownConnections() {
        // given
        subject.registerConnection(conn1);
        subject.registerConnection(conn2);
        subject.registerConnection(conn3);

        // when
        subject.close();

        // then
        verify(conn1).close();
        verify(conn2).close();
        verify(conn3).close();
    }

    @Test
    public void callingCloseTwiceDoesNotCloseConnectionsTwice() {
        // given
        subject.registerConnection(conn1);

        // when
        subject.close();
        subject.close();

        // then
        verify(conn1).close();
    }

    @Test
    public void registerConnectionFailsAfterClose() {
        // given
        subject.close();

        // when & then
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> subject.registerConnection(conn1)
        );

        assertEquals("Bus is closed", ex.getMessage());
    }

    @Test
    public void handleClosedConnectionDoesNotCreateNotificationAfterClose() {
        // given
        subject.close();

        // when
        subject.handleClosedConnection(0);

        // then
        assertFalse(subject.hasNextCommand());
    }
}
