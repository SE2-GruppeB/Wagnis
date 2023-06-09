package at.aau.wagnis.server;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Objects;

import at.aau.wagnis.gamestate.GameLogicState;
import at.aau.wagnis.gamestate.GameData;
import at.aau.wagnis.gamestate.LobbyState;
import at.aau.wagnis.server.communication.command.ClientCommand;
import at.aau.wagnis.server.communication.command.SendGameDataCommand;
import at.aau.wagnis.server.communication.command.ServerCommand;
import at.aau.wagnis.server.communication.connection.ClientConnectionBus;
import at.aau.wagnis.server.communication.connection.ClientConnectionListener;

public class GameServer implements Runnable {

    private final ClientConnectionBus connectionBus;
    private final ClientConnectionListener clientConnectionListener;
    private GameData gameData = null;
    public GameData getGameData() {
        return gameData;
    }
    public void setGameState(GameData gameData) {
        this.gameData = gameData;
    }

    private GameLogicState gameLogicState;

    public GameServer(
            @NonNull ClientConnectionBus connectionBus,
            @NonNull ClientConnectionListener clientConnectionListener,
            @NonNull GameLogicState initialState) {
        this.connectionBus = Objects.requireNonNull(connectionBus);
        this.clientConnectionListener = Objects.requireNonNull(clientConnectionListener);
        this.gameLogicState = Objects.requireNonNull(initialState);
        this.gameLogicState.setGameServer(this);
    }

    /**
     * Process commands from the ClientConnectionBus until the thread is interrupted.
     */
    public void run() {
        try {
            while(!Thread.currentThread().isInterrupted()) {
                ServerCommand command = connectionBus.getNextCommand();
                callCommand(command);

                if(gameData == null && gameLogicState instanceof LobbyState) {
                    gameData = ((LobbyState) gameLogicState).getGameData();
                }

                if(gameData != null) {  // without this condition some tests would fail
                    gameData.setCurrentGameLogicState(gameLogicState.getClass().getSimpleName());
                }

                broadcastCommand(new SendGameDataCommand(gameData));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Extrected method to avoid SonarCloud code smell
     * @param command Command to be executed
     */
    private void callCommand(ServerCommand command) {
        try {
            command.execute(gameLogicState);
        } catch (IllegalArgumentException e){
            Log.e("Server","Ignored Illegal Command"+e.getMessage());
        }
    }

    public void close() {
        clientConnectionListener.close();
        connectionBus.close();
    }

    public void setGameLogicState(@NonNull GameLogicState gameLogicState) {
        this.gameLogicState = Objects.requireNonNull(gameLogicState);
        gameLogicState.setGameServer(this);
        gameLogicState.onEntry();
    }

    public void broadcastCommand(@NonNull ClientCommand command) {

        this.connectionBus.broadcastCommand(command);
    }
}
