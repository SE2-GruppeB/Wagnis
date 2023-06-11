package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import at.aau.wagnis.gamestate.GameLogicState;
//TODo implement
public class UseCardsCommand implements ClientOriginatedServerCommand{
    @Override
    public int getClientId() {
        return 0;
    }

    @Override
    public void setClientId(int clientId) {

    }

    @Override
    public void execute(@NonNull GameLogicState gameLogicState) {

    }
}
