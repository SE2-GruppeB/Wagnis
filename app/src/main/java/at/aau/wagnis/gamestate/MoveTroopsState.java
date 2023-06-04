package at.aau.wagnis.gamestate;

import android.content.Context;

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

    public boolean move(int numTroops) {
        if (isMoveValid(numTroops)) {
            moveTroopsBetweenHubs(numTroops);
            return true; // Successful move
        } else {
            return false; // Invalid move
        }
    }


    private boolean isMoveValid(int numTroops) {
        if (sourceHub == null || targetHub == null) {
            return false;
        }

        if (sourceHub.getOwner() != targetHub.getOwner()) {
            return false;
        }

        return sourceHub.getAmountTroops() > 1 && sourceHub.getAmountTroops() >= numTroops;
    }

    private void moveTroopsBetweenHubs(int numTroops) {
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

