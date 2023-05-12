package at.aau.wagnis.gamestate;

import at.aau.wagnis.Hub;

public class MoveTroopsState {
    private Hub sourceHub;
    private Hub targetHub;

    private int numTroops;

    public MoveTroopsState(Hub sourceHub, Hub targetHub) {
        this.sourceHub = sourceHub;
        this.targetHub = targetHub;
    }

    public void move(int numTroops) {
        moveTroopsBetweenHubs(numTroops);
    }

    private void moveTroopsBetweenHubs(int numTroops) {

        if (sourceHub.getOwner() != targetHub.getOwner()) {
            throw new IllegalArgumentException("Cannot move troops between hubs owned by different players.");
        }
        if (this.sourceHub.getAmountTroops() <= 1 || sourceHub.getAmountTroops() <= numTroops) {
            throw new IllegalArgumentException("Illegal move not enough troops at source hub");
        }

        this.sourceHub.setAmountTroops(this.sourceHub.getAmountTroops()-numTroops);
        this.targetHub.setAmountTroops(this.targetHub.getAmountTroops()+numTroops);

        //Log.d("TAG", "sourceHub");
        //Log.d("TAG", "targetHub");
    }
}

