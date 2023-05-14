package at.aau.wagnis.client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

import at.aau.wagnis.gamestate.GameData;
import at.aau.wagnis.server.communication.command.ClientCommand;
import at.aau.wagnis.server.communication.command.ClientOriginatedServerCommand;
import at.aau.wagnis.server.communication.connection.ServerConnection;

public class GameClient implements Consumer<ClientCommand>, ClientLogic {

    private ServerConnection serverConnection = null;

    private GameData currentGameData = null;
    private Consumer<GameData> gameStateListener = null;

    public synchronized void setServerConnection(@NonNull ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
        this.serverConnection.setCommandConsumer(this);
    }

    @Override
    public void accept(@NonNull ClientCommand clientCommand) {
        clientCommand.execute(this);
    }

    @Override
    public synchronized void updateGameState(@NonNull GameData gameData) {
        this.currentGameData = Objects.requireNonNull(gameData);

        if (this.gameStateListener != null) {
            this.gameStateListener.accept(currentGameData);
        }
    }

    /**
     * Set a listener that should be notified when the game state changes.
     * The listener will immediately receive an initial update with the current state.
     *
     * If no state has been received yet, the value of the notification will be null.
     */
    public synchronized void setGameStateListener(@Nullable Consumer<GameData> listener) {
        this.gameStateListener = listener;

        if (this.gameStateListener != null) {
            this.gameStateListener.accept(currentGameData);
        }
    }

    /**
     * Send a command to the connected server
     */
    public synchronized void sendCommand(@NonNull ClientOriginatedServerCommand command) {
        if (this.serverConnection == null) {
            throw new IllegalStateException("Server connection has not been set");
        }

        this.serverConnection.send(command);
    }

    public synchronized void close() {
        if (this.serverConnection != null) {
            this.serverConnection.close();
        }
    }
}
