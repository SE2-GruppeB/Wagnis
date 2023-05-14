package at.aau.wagnis.gamestate;

public class ChooseMoveState extends GameLogicState {


    public void moveToNextState() {
        this.gameServer.getGameState();

    }
}
