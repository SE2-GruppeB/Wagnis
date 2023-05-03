package at.aau.wagnis.gamestate;

import java.util.List;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

public class ReinforceGameState{
    private List<Hub> hubs;
    private List<Integer> troopsToDeploy;

    public ReinforceGameState(List<Hub> hubs, List<Integer> troopsToDeploy) {
        this.hubs = hubs;
        this.troopsToDeploy = troopsToDeploy;
    }

    public List<Hub> getHubs() {
        return hubs;
    }

    public void setHubs(List<Hub> hubs) {
        this.hubs = hubs;
    }

    public List<Integer> getTroopsToDeploy() {
        return troopsToDeploy;
    }

    public void setTroopsToDeploy(List<Integer> troopsToDeploy) {
        this.troopsToDeploy = troopsToDeploy;
    }



    public void reinforce() {
        //TODO implement this


    }
}
