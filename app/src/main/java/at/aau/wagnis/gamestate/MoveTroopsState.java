package at.aau.wagnis.gamestate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import at.aau.wagnis.GlobalVariables;
import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

public class MoveTroopsState {
    private Hub sourceHub, targetHub;

    private int numTroops;

    public MoveTroopsState(Hub sourceHub, Hub targetHub) {
        this.sourceHub = sourceHub;
        this.targetHub = targetHub;
    }

    public void move(int numTroops) {
        moveTroopsBetweenHubs(numTroops);
    }

    private void moveTroopsBetweenHubs(int numTroops) {

        Map<String, Integer> sourceHubTroops = sourceHub.getTroops();
        Map<String, Integer> targetHubTroops = targetHub.getTroops();

        if (sourceHub.getOwner() != targetHub.getOwner()) {
            throw new IllegalArgumentException("Cannot move troops between hubs owned by different players.");
        }
        if (sourceHubTroops.get(GlobalVariables.troop) < 1) {
            throw new IllegalArgumentException("Illegal move not enough troops at source hub");
        }

        if (sourceHubTroops.get(GlobalVariables.troop) > 1 && sourceHub.getOwner() == targetHub.getOwner()) {
            sourceHubTroops.put(GlobalVariables.troop, sourceHubTroops.get(GlobalVariables.troop) - numTroops);
            targetHubTroops.put(GlobalVariables.troop, targetHubTroops.get(GlobalVariables.troop) + numTroops);

        }
    }
}

