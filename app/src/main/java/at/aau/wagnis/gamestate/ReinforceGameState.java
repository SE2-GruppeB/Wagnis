package at.aau.wagnis.gamestate;

import android.util.Log;

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
        if(hubs.size() != troopsToDeploy.size()) {throw new IllegalArgumentException("Hubs and troopsToDeploy must have the same size");}
        if (hubs.isEmpty()) {throw new IllegalArgumentException("There must be at least one");}
        for (int i = 0; i < hubs.size(); i++) {
            Log.d("Info", "Added " + troopsToDeploy.get(i) + " troops to hub " + hubs.get(i).getId());
            hubs.get(i).addTroops(troopsToDeploy.get(i));
        }
    }
}
