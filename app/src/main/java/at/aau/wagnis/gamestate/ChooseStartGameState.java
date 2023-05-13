package at.aau.wagnis.gamestate;

import static at.aau.wagnis.GlobalVariables.hubs;
import static at.aau.wagnis.GlobalVariables.players;

import java.util.List;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

public class ChooseStartGameState extends GameLogicState {

    @Override
    public void chooseStart(List<Hub> hubs,List<Player> players) {
        this.gameServer.getGameState();
        this.gameServer.setGameLogicState(new StartGameState(hubs,players));
    }
}
