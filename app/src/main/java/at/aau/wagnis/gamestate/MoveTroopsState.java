package at.aau.wagnis.gamestate;

import java.util.Map;

import at.aau.wagnis.GlobalVariables;
import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

public class MoveTroopsState {
    private Hub sourceHub, targetHub;
    private Map<String, Integer> sourceHubTroops;

    private Map<String, Integer> targetHubTroops;
    public MoveTroopsState(Hub sourceHub, Hub targetHub) {
        this.sourceHub = sourceHub;
        this.targetHub = targetHub;
    }
    public void move(){
        MoveTroopsBetweenHubs();

    }

    private Map<Integer, Map<String, Integer>> MoveTroopsBetweenHubs(){



        if (sourceHub.getId() != targetHub.getId()) {
            throw new IllegalArgumentException("The target hub does not exist.");
        }
        if (sourceHub.getOwner() != targetHub.getOwner()) {
            throw new IllegalArgumentException("Cannot move troops between hubs owned by different players.");
        }
        if (sourceHubTroops.get(GlobalVariables.troop) <= 1) {
            throw new IllegalArgumentException("Illegal move not enough troops at source hub");
        }

        return null;
    }

}

