package at.aau.wagnis.application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

import at.aau.wagnis.client.GameClient;
import at.aau.wagnis.gamestate.GameState;
import at.aau.wagnis.server.GameServer;
import at.aau.wagnis.server.communication.command.ClientOriginatedServerCommand;

public class GameManager {

    protected static final int GAME_PORT = 54321;
    protected static final String LOCAL_SERVER_ADDRESS = "localhost";

    private final NetworkGameClientFactory gameClientFactory;
    private final GameServerFactory gameServerFactory;

    private final Object connectionLock = new Object();
    private final Object connectionStateLock = new Object();
    private GameServer localGameServer = null;
    private GameClient gameClient = null;
    private Consumer<ConnectionState> connectionStateListener = null;
    private ConnectionState connectionState = ConnectionState.NO_CONNECTION;

    public GameManager(@NonNull NetworkGameClientFactory gameClientFactory, @NonNull GameServerFactory gameServerFactory) {
        this.gameClientFactory = Objects.requireNonNull(gameClientFactory);
        this.gameServerFactory = Objects.requireNonNull(gameServerFactory);
    }

    /**
     * Start a new locally hosted game; any existing game connection will be closed.
     */
    public void startNewGame() {
        createGame(null);
    }

    /**
     * Connect to a game server at the provided address; any existing game will be closed.
     *
     * @param serverAddress The address of the server to connect to.
     */
    public void joinGameByServerAddress(@NonNull String serverAddress) {
        createGame(Objects.requireNonNull(serverAddress));
    }

    private void createGame(@Nullable String existingServerAddress) {
        synchronized (connectionLock) {
            this.reset();

            try {
                setConnectionState(ConnectionState.CONNECTING);

                String serverAddress;
                if (existingServerAddress == null) {
                    localGameServer = gameServerFactory.createGameServer(GAME_PORT);
                    serverAddress = LOCAL_SERVER_ADDRESS;
                } else {
                    serverAddress = existingServerAddress;
                }

                gameClient = gameClientFactory.createGameClient(serverAddress, GAME_PORT);

                setConnectionState(ConnectionState.CONNECTED);
            } catch (IOException e) {
                this.reset();
                setConnectionState(ConnectionState.ERROR);
            }
        }
    }

    /**
     * Disconnect from the current game, also stopping the local game server if any.
     */
    public void disconnect() {
        synchronized (connectionLock) {
            setConnectionState(ConnectionState.NO_CONNECTION);
            this.reset();
        }
    }

    private void reset() {
        synchronized (connectionLock) {
            if (gameClient != null) {
                gameClient.close();
                gameClient = null;
            }

            if (localGameServer != null) {
                localGameServer.close();
                localGameServer = null;
            }
        }
    }

    /**
     * @param command Command to send to the connected server
     * @throws IllegalStateException If the connection is not ready to send commands.
     */
    public void postCommand(@NonNull ClientOriginatedServerCommand command) {
        synchronized (connectionLock) {
            if (gameClient == null) {
                throw new IllegalStateException("Game client is not ready");
            } else {
                gameClient.sendCommand(command);
            }
        }
    }

    /**
     * Set a listener that should be notified about changes of the currently running game's state.
     * The listener will immediately receive an initial update with the current state.
     * <p>
     * If no state has been received yet, the value of the notification will be null.
     */
    public void setGameStateListener(@Nullable Consumer<GameState> listener) {
        synchronized (connectionLock) {
            if (gameClient == null) {
                throw new IllegalStateException("Game client is not ready");
            } else {
                gameClient.setGameStateListener(listener);
            }
        }
    }

    /**
     * Set a consumer that should be notified whenever the connection state changes.
     * The listener will immediately receive an initial update with the current state.
     */
    public void setConnectionStateListener(@Nullable Consumer<ConnectionState> listener) {
        synchronized (connectionStateLock) {
            this.connectionStateListener = listener;

            if (this.connectionStateListener != null) {
                this.connectionStateListener.accept(connectionState);
            }
        }
    }

    private void setConnectionState(@NonNull ConnectionState newState) {
        synchronized (connectionStateLock) {
            this.connectionState = Objects.requireNonNull(newState);

            if (this.connectionStateListener != null) {
                this.connectionStateListener.accept(newState);
            }
        }
    }

    public enum ConnectionState {
        NO_CONNECTION,
        CONNECTING,
        CONNECTED,
        ERROR
    }
}
