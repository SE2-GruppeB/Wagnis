package at.aau.wagnis.gamestate;

import java.util.List;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

public abstract class GameLogicState {
    public void start(List<Hub> unassignedCountries, List<Player> players){}
    public void reinforce(){}
    public void attack(){}
    public void move(){}
    public void end(){}


}