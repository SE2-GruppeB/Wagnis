package at.aau.wagnis.gamestate;

import java.util.List;

import at.aau.wagnis.Hub;

public class ReinforceGameState extends GameLogicState{
    private List<Integer> hubs;
    private List<Integer> troopsToDeploy;

    public ReinforceGameState(List<Integer> hubs, List<Integer> troopsToDeploy) {
        if (hubs == null) {
            throw new IllegalArgumentException("Hubs cannot be null");
        }
        if (troopsToDeploy == null) {
            throw new IllegalArgumentException("TroopstoDeploy cannot be null");
        }
        this.hubs = hubs;
        this.troopsToDeploy = troopsToDeploy;
    }

    public List<Integer> getHubs() {
        return hubs;
    }

    public void setHubs(List<Integer> hubs) {
        if (hubs == null) {
            throw new IllegalArgumentException("Hubs cannot be null");
        }
        this.hubs = hubs;
    }

    public List<Integer> getTroopsToDeploy() {
        return troopsToDeploy;
    }

    public void setTroopsToDeploy(List<Integer> troopsToDeploy) {
        if (troopsToDeploy == null) {
            throw new IllegalArgumentException("TroopstoDeploy cannot be null");
        }
        this.troopsToDeploy = troopsToDeploy;
    }

    @Override
    public void reinforce() {
        if (hubs.size() != troopsToDeploy.size()) {
            throw new IllegalArgumentException("Hubs and troopsToDeploy must have the same size");
        }
        if (hubs.isEmpty()) {
            throw new IllegalArgumentException("There must be at least one");
        }
        for (int i = 0; i < hubs.size(); i++) {
            reinforceSingle(hubs.get(i),troopsToDeploy.get(i));
        }
    }

    public void reinforceSingle (int hubId, int troops){
        List<Hub> hubsFromServer = this.gameServer.getGameData().getHubs();
        for (Hub hub : hubsFromServer) {
            if (hub.getId() == hubId){
                hub.addTroops(troops);
            }
        }
    }
}
