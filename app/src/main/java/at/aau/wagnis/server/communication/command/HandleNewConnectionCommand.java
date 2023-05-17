package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import at.aau.wagnis.gamestate.GameLogicState;

public class HandleNewConnectionCommand implements ServerCommand {

    private final int clientId;

    public HandleNewConnectionCommand(int clientId) {
        this.clientId = clientId;
    }

    @Override
    public void execute(@NonNull GameLogicState gameLogicState) {
        gameLogicState.handleNewConnection(this.clientId);
    }

    public int getClientId() {
        return this.clientId;
    }
}
