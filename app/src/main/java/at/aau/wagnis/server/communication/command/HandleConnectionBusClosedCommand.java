package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import at.aau.wagnis.gamestate.GameLogicState;

public class HandleConnectionBusClosedCommand implements ServerCommand {

    @Override
    public void execute(@NonNull GameLogicState gameLogicState) {
        gameLogicState.handleConnectionBusClosed();
    }
}
