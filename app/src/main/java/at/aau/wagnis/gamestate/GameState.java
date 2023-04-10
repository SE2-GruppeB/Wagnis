package at.aau.wagnis.gamestate;

import java.util.List;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;


public class GameState {
    private final List<Hub> hubs;
    private final List<Player> players;

    public GameState(List<Hub> hubs, List<Player> players) {
        this.hubs = hubs;
        this.players = players;
    }
}
