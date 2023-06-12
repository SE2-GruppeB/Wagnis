package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import at.aau.wagnis.gamestate.GameLogicState;
//TODo implement
public class UseCardsCommand implements ClientOriginatedServerCommand{

    private Integer clientId;

    @Override
    public int getClientId() {
        if (this.clientId != null) {
            return clientId;
        } else {
            throw new IllegalStateException("ClientId has not been set");
        }
    }

    @Override
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    @Override
    public void execute(@NonNull GameLogicState gameLogicState) {
        // TODO
    }
}
