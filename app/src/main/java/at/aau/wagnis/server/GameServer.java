package at.aau.wagnis.server;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.Objects;

import at.aau.wagnis.gamestate.GameLogicState;
import at.aau.wagnis.gamestate.GameState;
import at.aau.wagnis.server.communication.command.ClientCommand;
import at.aau.wagnis.server.communication.command.SendGameStateCommand;
import at.aau.wagnis.server.communication.command.ServerCommand;
import at.aau.wagnis.server.communication.connection.ClientConnectionBus;
import at.aau.wagnis.server.communication.connection.ClientConnectionListener;

public class GameServer implements Runnable {

    private final ClientConnectionBus connectionBus;
    private final ClientConnectionListener clientConnectionListener;
    private GameState gameState = null;
    public GameState getGameState() {
        return gameState;
    }
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
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

                GameState demoState = new GameState();
                demoState.setSeed("123455123455123456123455123456123456123456123456123456123456123456123456123456123456");
                demoState.setPlayers(Collections.emptyList());
                demoState.setHubs(Collections.emptyList());
                broadcastCommand(new SendGameStateCommand(demoState));
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
