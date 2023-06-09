package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;
import at.aau.wagnis.gamestate.GameLogicState;
import at.aau.wagnis.server.communication.serialization.Serializer;

public class ChooseStartCommand implements ClientOriginatedServerCommand {
    private final List<Hub> hubs;
    private final List<Player> players;

    public ChooseStartCommand(List<Hub> hubs, List<Player> players) {
        this.hubs = hubs;
        this.players = players;
    }

    @Override
    public int getClientId() {
        return 0;
    }

    @Override
    public void setClientId(int clientId) {
        /*Empty because useless, existing because of interface*/
    }

    @Override
    public void execute(@NonNull GameLogicState gameLogicState) {
        gameLogicState.chooseStart(hubs, players);
    }

    // TODO: implement how this command sends/receives

}

