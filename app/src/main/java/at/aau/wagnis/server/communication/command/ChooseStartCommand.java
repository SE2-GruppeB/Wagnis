package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import java.util.List;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;
import at.aau.wagnis.gamestate.GameLogicState;

public class ChooseStartCommand implements ClientOriginatedServerCommand{

    @Override
    public void setClientId(int clientId) {

    }

    @Override
    public int getClientId() {
        return 0;
    }

    @Override
    public void execute(@NonNull GameLogicState gameLogicState) {
        gameLogicState.start();
    }
}

