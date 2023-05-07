package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import at.aau.wagnis.gamestate.GameLogicState;

public class HandleConnectionClosedCommand implements ServerCommand {

    private final int clientId;

    public HandleConnectionClosedCommand(int clientId) {
        this.clientId = clientId;
    }

    public int getClientId() {
        return clientId;
    }

    @NonNull
    @Override
    public String toString() {
        return "HandleConnectionClosedCommand{" +
                "clientId=" + clientId +
                '}';
    }

    @Override
    public void execute(@NonNull GameLogicState gameLogicState) {
        gameLogicState.handleClosedConnection(clientId);
    }
}
