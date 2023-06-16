package at.aau.wagnis.gamestate;


import at.aau.wagnis.Adjacency;

import at.aau.wagnis.GlobalVariables;

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

    @Override
    public void onEntry() {
        if (sourceHub == null) {
            this.sourceHub = gameServer.getGameData().getHubs().stream().filter(h -> h.getId() == sourceHubId).findFirst().orElseThrow(() -> new IllegalStateException("Hub not found"));
        }
        if (targetHub == null) {
            this.targetHub = gameServer.getGameData().getHubs().stream().filter(h -> h.getId() == targetHubId).findFirst().orElseThrow(() -> new IllegalStateException("Hub not found"));
        }
        try {
            move(numTroops);
        }
        finally {
            this.gameServer.setGameLogicState(new ChooseMoveState());
        }

    }

    @Override
    public boolean move(int numTroops) {
        if (isMoveValid(numTroops)) {
            moveTroopsBetweenHubs(numTroops);
            return true; // Successful move
        }
        return false;
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
    private boolean areHubsAdjacent(Hub sourceHub, Hub targetHub) {
        for (Adjacency adjacency : GlobalVariables.getAdjacencies()) {
            if ((adjacency.getHub1() == sourceHub && adjacency.getHub2() == targetHub) ||
                    (adjacency.getHub1() == targetHub && adjacency.getHub2() == sourceHub)) {
                return true;
            }
        }
        return false;
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
