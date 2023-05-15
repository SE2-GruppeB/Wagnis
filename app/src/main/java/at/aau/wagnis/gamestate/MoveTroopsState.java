package at.aau.wagnis.gamestate;

import at.aau.wagnis.Hub;

public class MoveTroopsState extends GameLogicState {
    private Hub sourceHub;
    private Hub targetHub;
    private int sourceHubId;
    private int targetHubId;
    private int numTroops;

    public MoveTroopsState(Hub sourceHub, Hub targetHub) {
        this.sourceHub = sourceHub;
        this.targetHub = targetHub;
    }

    public MoveTroopsState(int sourceHubId, int targetHubId, int numTroops) {
        this.sourceHubId = sourceHubId;
        this.targetHubId = targetHubId;
        this.numTroops = numTroops;
    }

    public void move(int numTroops) { moveTroopsBetweenHubs(numTroops); }

    private void moveTroopsBetweenHubs(int numTroops)  {


        if (sourceHub == null || targetHub == null) {
            throw new IllegalArgumentException("Invalid source or target hub.");
        }

        if (sourceHub.getOwner() != targetHub.getOwner()) {
            throw new IllegalArgumentException("Cannot move troops between hubs owned by different players.");
        }

        if (sourceHub.getAmountTroops() <= 1 || sourceHub.getAmountTroops() < numTroops) {
            throw new IllegalArgumentException("Illegal move: not enough troops at source hub.");
        }

        sourceHub.setAmountTroops(sourceHub.getAmountTroops() - numTroops);
        targetHub.setAmountTroops(targetHub.getAmountTroops() + numTroops);
    }



    public int getSourceHubId() {
        return sourceHubId;
    }
    public void setSourceHubId(int sourceHubId) {
        this.sourceHubId = sourceHubId;
    }
    public int getTargetHubId() {
        return targetHubId;
    }
    public void setTargetHubId(int targetHubId) {
        this.targetHubId = targetHubId;
    }
}

