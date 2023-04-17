package at.aau.wagnis.gamestate;

import java.util.List;
import java.util.Map;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

public abstract class GameLogicState {
    public void start(){}
    public void reinforce(){}
    public void attack(){}
    public void move(){}
    public void end(){}
    public void checkForVictory(){}


}
