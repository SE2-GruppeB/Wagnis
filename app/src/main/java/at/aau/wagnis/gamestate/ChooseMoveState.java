package at.aau.wagnis.gamestate;

import at.aau.wagnis.Hub;

public class ChooseMoveState extends GameLogicState{


    public void moveToNextState() {
        this.gameServer.getGameState();

    }
}
