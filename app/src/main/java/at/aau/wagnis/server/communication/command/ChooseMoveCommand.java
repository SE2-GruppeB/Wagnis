package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import at.aau.wagnis.gamestate.GameLogicState;

public class ChooseMoveCommand implements ClientOriginatedServerCommand {
    @Override
    public void setClientId(int clientId) {

    }

    @Override
    public int getClientId() {
        return 0;
    }

    @Override
    public void execute(@NonNull GameLogicState gameLogicState) {

    }
}