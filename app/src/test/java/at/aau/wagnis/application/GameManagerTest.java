package at.aau.wagnis.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static at.aau.wagnis.application.GameManager.ConnectionState.CONNECTED;
import static at.aau.wagnis.application.GameManager.ConnectionState.CONNECTING;
import static at.aau.wagnis.application.GameManager.ConnectionState.ERROR;
import static at.aau.wagnis.application.GameManager.ConnectionState.NO_CONNECTION;
import static at.aau.wagnis.application.GameManager.GAME_PORT;
import static at.aau.wagnis.application.GameManager.LOCAL_SERVER_ADDRESS;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.function.Consumer;

import at.aau.wagnis.client.GameClient;
import at.aau.wagnis.gamestate.GameData;
import at.aau.wagnis.server.GameServer;
import at.aau.wagnis.server.communication.command.ClientOriginatedServerCommand;

class GameManagerTest {

    @Mock private GameServerFactory gameServerFactory;
    @Mock private GameServer gameServer1;
    @Mock private GameServer gameServer2;
    @Mock private NetworkGameClientFactory gameClientFactory;
    @Mock private GameClient gameClient1;
    @Mock private GameClient gameClient2;
    @Mock private Consumer<GameManager.ConnectionState> connectionStateConsumer;
    @Mock private Consumer<GameData> gameDataConsumer;
    @Mock private ClientOriginatedServerCommand serverCommand;

    private GameManager subject;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        subject = new GameManager(gameClientFactory, gameServerFactory);
    }

    @Test
    void startNewGameCreatesServerAndClient() throws IOException {
        // when
        subject.setConnectionStateListener(connectionStateConsumer);
        subject.startNewGame();

        // then
        InOrder inOrder = Mockito.inOrder(gameServerFactory, gameClientFactory, connectionStateConsumer);
        inOrder.verify(connectionStateConsumer).accept(NO_CONNECTION);
        inOrder.verify(connectionStateConsumer).accept(CONNECTING);
        inOrder.verify(gameServerFactory).createGameServer(GAME_PORT);
        inOrder.verify(gameClientFactory).createGameClient(LOCAL_SERVER_ADDRESS, GAME_PORT);
        inOrder.verify(connectionStateConsumer).accept(CONNECTED);
        verifyNoMoreInteractions(connectionStateConsumer, gameServerFactory, gameClientFactory);
    }

    @Test
    void joinGameByServerAddressDoesNotCreateLocalServer() throws IOException {
        // given
        String serverAddress = "someAddress";

        // when
        subject.setConnectionStateListener(connectionStateConsumer);
        subject.joinGameByServerAddress(serverAddress);

        // then
        InOrder inOrder = Mockito.inOrder(gameClientFactory, connectionStateConsumer);
        inOrder.verify(connectionStateConsumer).accept(NO_CONNECTION);
        inOrder.verify(connectionStateConsumer).accept(CONNECTING);
        inOrder.verify(gameClientFactory).createGameClient(serverAddress, GAME_PORT);
        inOrder.verify(connectionStateConsumer).accept(CONNECTED);
        verifyNoMoreInteractions(connectionStateConsumer, gameServerFactory, gameClientFactory);
    }

    @Test
    void connectingTwiceClosesFirstConnection() throws IOException {
        // given
        when(gameServerFactory.createGameServer(anyInt())).thenReturn(gameServer1, gameServer2);
        when(gameClientFactory.createGameClient(any(), anyInt())).thenReturn(gameClient1, gameClient2);

        // when
        subject.startNewGame();
        subject.startNewGame();

        // then
        verify(gameServer1).close();
        verify(gameClient1).close();
        verify(gameServer2, never()).close();
        verify(gameClient2, never()).close();
    }

    @Test
    void disconnectClosesConnection() throws IOException {
        // given
        when(gameServerFactory.createGameServer(anyInt())).thenReturn(gameServer1);
        when(gameClientFactory.createGameClient(any(), anyInt())).thenReturn(gameClient1);

        // when
        subject.startNewGame();
        subject.disconnect();

        // then
        verify(gameServer1).close();
        verify(gameClient1).close();
    }

    @Test
    void createGameSetsErrorStateWhenServerFailsToStart() throws IOException {
        // given
        when(gameServerFactory.createGameServer(anyInt())).thenThrow(new IOException());

        // when
        subject.setConnectionStateListener(connectionStateConsumer);
        subject.startNewGame();

        // then
        InOrder inOrder = Mockito.inOrder(gameServerFactory, connectionStateConsumer);
        inOrder.verify(connectionStateConsumer).accept(NO_CONNECTION);
        inOrder.verify(connectionStateConsumer).accept(CONNECTING);
        inOrder.verify(gameServerFactory).createGameServer(anyInt());
        inOrder.verify(connectionStateConsumer).accept(ERROR);
        verifyNoMoreInteractions(connectionStateConsumer, gameServerFactory, gameClientFactory);
    }

    @Test
    void createGameClosesLocalServerIfClientFailsToConnect() throws IOException {
        // given
        when(gameServerFactory.createGameServer(anyInt())).thenReturn(gameServer1);
        when(gameClientFactory.createGameClient(any(), anyInt())).thenThrow(new IOException());

        // when
        subject.setConnectionStateListener(connectionStateConsumer);
        subject.startNewGame();

        // then
        verify(gameServer1, only()).close();

        InOrder inOrder = Mockito.inOrder(connectionStateConsumer);
        inOrder.verify(connectionStateConsumer).accept(NO_CONNECTION);
        inOrder.verify(connectionStateConsumer).accept(CONNECTING);
        inOrder.verify(connectionStateConsumer).accept(ERROR);
        verifyNoMoreInteractions(connectionStateConsumer);
    }

    @Test
    void postCommandFailsWhenNotConnected() {
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> subject.postCommand(serverCommand)
        );

        assertEquals("Game client is not ready", ex.getMessage());
    }

    @Test
    void postCommandNotifiesGameClient() throws IOException {
        // given
        when(gameServerFactory.createGameServer(anyInt())).thenReturn(gameServer1);
        when(gameClientFactory.createGameClient(any(), anyInt())).thenReturn(gameClient1);

        // when
        subject.startNewGame();
        subject.postCommand(serverCommand);

        // then
        verify(gameClient1).sendCommand(serverCommand);
    }

    @Test
    void setGameStateListenerFailsWhenNotConnected() {
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> subject.setGameStateListener(gameDataConsumer)
        );

        assertEquals("Game client is not ready", ex.getMessage());
    }

    @Test
    void setGameStateListenerNotifiesClient() throws IOException {
        // given
        when(gameServerFactory.createGameServer(anyInt())).thenReturn(gameServer1);
        when(gameClientFactory.createGameClient(any(), anyInt())).thenReturn(gameClient1);

        // when
        subject.startNewGame();
        subject.setGameStateListener(gameDataConsumer);

        // then
        verify(gameClient1).setGameDataListener(gameDataConsumer);
    }

    @Test
    void connectionStateListenerCanBeRemoved() throws IOException {
        // given
        when(gameServerFactory.createGameServer(anyInt())).thenReturn(gameServer1);
        when(gameClientFactory.createGameClient(any(), anyInt())).thenReturn(gameClient1);

        // when
        subject.setConnectionStateListener(connectionStateConsumer);
        subject.setConnectionStateListener(null);
        subject.startNewGame();

        // then
        verify(connectionStateConsumer, only()).accept(NO_CONNECTION);
    }
}
