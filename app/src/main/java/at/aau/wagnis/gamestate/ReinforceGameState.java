package at.aau.wagnis.gamestate;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

public class ReinforceGameState{
    private Player player;


    public ReinforceGameState(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Hub playerChosenHub() {
        //TODO implement this
        return null;
    }

    public void reinforce() {
        //TODO implement this

        
    }
}
