package at.aau.wagnis.gamestate;

import java.util.ArrayList;
import java.util.List;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

public class LobbyState extends GameLogicState{
    List<Player> players;
    List<Hub> hubs;

    public LobbyState(){
        players = new ArrayList<>();
        hubs = new ArrayList<>();
    }

    public void addPlayer(Player player){
        players.add(player);
    }

    public void next(){
        gameServer.setGameLogicState(new StartGameState(hubs, players));
    }
}
