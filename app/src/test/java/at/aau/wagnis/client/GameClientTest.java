package at.aau.wagnis.client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.function.Consumer;

import at.aau.wagnis.gamestate.GameData;
import at.aau.wagnis.server.communication.command.ClientCommand;
import at.aau.wagnis.server.communication.command.ClientOriginatedServerCommand;
import at.aau.wagnis.server.communication.connection.ServerConnection;

class GameClientTest {

    @Mock private ServerConnection serverConnection;
    @Mock private GameData gameData;
    @Mock private Consumer<GameData> gameStateConsumer;
    @Mock private ClientCommand clientCommand;
    @Mock private ClientOriginatedServerCommand serverCommand;

    private GameClient subject;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        subject = new GameClient();
    }

    @Test
    void setServerConnectionSetsCommandConsumer() {
        // when
        subject.setServerConnection(serverConnection);

        // then
        verify(serverConnection).setCommandConsumer(subject);
    }

    @Test
    void acceptExecutesCommand() {
        // when
        subject.accept(clientCommand);

        // then
        verify(clientCommand).execute(subject);
    }

    @Test
    void updateGameStateNotifiesListener() {
        // given
        subject.setGameDataListener(gameStateConsumer);

        // when
        subject.updateGameData(gameData);

        // then
        InOrder inOrder = Mockito.inOrder(gameStateConsumer);
        inOrder.verify(gameStateConsumer).accept(null);
        inOrder.verify(gameStateConsumer).accept(gameData);
        verifyNoMoreInteractions(gameStateConsumer);
    }

    @Test
    void updateGameStateStoresState() {
        // given
        subject.updateGameData(gameData);

        // when
        subject.setGameDataListener(gameStateConsumer);

        // then
        verify(gameStateConsumer).accept(gameData);
        verifyNoMoreInteractions(gameStateConsumer);
    }

    @Test
    void setGameStateListenerRemovesListener() {
        // given
        subject.setGameDataListener(gameStateConsumer);

        // when
        subject.setGameDataListener(null);
        subject.updateGameData(gameData);

        // then
        verify(gameStateConsumer, never()).accept(gameData);
    }

    @Test
    void sendCommandThrowsIfConnectionHasNotBeenSet() {
        // when & then
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> subject.sendCommand(serverCommand)
        );

        assertEquals("Server connection has not been set", ex.getMessage());
    }

    @Test
    void sendCommandCallsServerConnection() {
        // given
        subject.setServerConnection(serverConnection);

        // when
        subject.sendCommand(serverCommand);

        // then
        verify(serverConnection).send(serverCommand);
    }

    @Test
    void closeClosesConnection() {
        // given
        subject.setServerConnection(serverConnection);

        // when
        subject.close();

        // then
        verify(serverConnection).close();
    }

    @Test
    void closeWithoutConnectionDoesNotThrow() {
        assertDoesNotThrow(subject::close);
    }
}
