package at.aau.wagnis.server;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.Objects;

import at.aau.wagnis.GlobalVariables;
import at.aau.wagnis.gamestate.GameLogicState;
import at.aau.wagnis.gamestate.GameData;
import at.aau.wagnis.server.communication.command.ClientCommand;
import at.aau.wagnis.server.communication.command.SendGameStateCommand;
import at.aau.wagnis.server.communication.command.ServerCommand;
import at.aau.wagnis.server.communication.connection.ClientConnectionBus;
import at.aau.wagnis.server.communication.connection.ClientConnectionListener;

public class GameServer implements Runnable {

    private final ClientConnectionBus connectionBus;
    private final ClientConnectionListener clientConnectionListener;
    private GameData gameData = null;
    public GameData getGameState() {
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
    }

    /**
     * Process commands from the ClientConnectionBus until the thread is interrupted.
     */
    public void run() {
        try {
            while(!Thread.currentThread().isInterrupted()) {
                ServerCommand command = connectionBus.getNextCommand();
                command.execute(gameLogicState);
                if(gameData == null) {
                    gameData = new GameData();
                    GlobalVariables.seedGenerator();
                    gameData.setSeed(GlobalVariables.getSeed());
                    gameData.setPlayers(Collections.emptyList());
                    gameData.setHubs(Collections.emptyList());
                }
                broadcastCommand(new SendGameStateCommand(gameData));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void close() {
        clientConnectionListener.close();
        connectionBus.close();
    }

    public void setGameLogicState(@NonNull GameLogicState gameLogicState) {
        this.gameLogicState = Objects.requireNonNull(gameLogicState);
    }

    public void broadcastCommand(@NonNull ClientCommand command) {

        this.connectionBus.broadcastCommand(command);
    }
}
