package at.aau.wagnis.gamestate;


import java.util.List;
import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

public class ReinforceGameState extends GameLogicState{
    private List<Integer> hubs;
    private List<Integer> troopsToDeploy;

    public ReinforceGameState(List<Integer> hubs, List<Integer> troopsToDeploy) {
        if (hubs == null) {
            throw new IllegalArgumentException("Hubs cannot be null");
        }
        if (troopsToDeploy == null) {
            throw new IllegalArgumentException("Troops to Deploy cannot be null");
        }
        this.hubs = hubs;
        this.troopsToDeploy = troopsToDeploy;
    }
    public ReinforceGameState(){
        super();
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
            throw new IllegalArgumentException("Troops to Deploy cannot be null");
        }
        this.troopsToDeploy = troopsToDeploy;
    }


    public void reinforce() {
        if (hubs.size() != troopsToDeploy.size()) {
            throw new IllegalArgumentException("Hubs and troops To Deploy must have the same size");
        }
        if (hubs.isEmpty()) {
            throw new IllegalArgumentException("There must be at least one");
        }
        for (int i = 0; i < hubs.size(); i++) {
            reinforceSingle(hubs.get(i),troopsToDeploy.get(i));
        }
    }

    @Override
    public void onEntry() {
        Player currentPlayer = gameServer.getGameData().getPlayers().get(gameServer.getGameData().getCurrentPlayer());
        int unassigned = currentPlayer.getUnassignedAvailableTroops() + currentPlayer.getOwnedHubs().size()/3 > 3 ? currentPlayer.getOwnedHubs().size()/3 : 3;
        currentPlayer.setUnassignedAvailableTroops(unassigned);
    }

    @Override
    public void reinforce(List<Integer> hubsId , List<Integer> troops) {
        if (hubsId.size() != troops.size()) {
            throw new IllegalArgumentException("Hubs and troopsToDeploy must have the same size");
        }
        if (hubsId.isEmpty()) {
            throw new IllegalArgumentException("There must be at least one");
        }
        for (int i = 0; i < hubsId.size(); i++) {
            reinforceSingle(hubsId.get(i),troops.get(i));
        }
    }

    public void reinforceSingle (int hubId, int troops){
        List<Hub> hubsFromServer = this.gameServer.getGameData().getHubs();
        for (Hub hub : hubsFromServer) {
            if (hub.getId() == hubId) {
                hub.addTroops(troops);
                hub.getOwner().assignTroops(troops);
            }
        }
    }
    @Override
    public void next(){
        gameServer.setGameLogicState(new ChooseAttackGameState());
    }
}
