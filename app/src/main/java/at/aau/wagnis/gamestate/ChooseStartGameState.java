package at.aau.wagnis.gamestate;

import java.util.List;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

public class ChooseStartGameState extends GameLogicState {


    @Override
    public void start() {
        this.gameServer.getGameState();
        //StartGameState startGameState = new StartGameState();
        //gameServer.setGameLogicState(startGameState);
        //startGameState.start();
    }
}
