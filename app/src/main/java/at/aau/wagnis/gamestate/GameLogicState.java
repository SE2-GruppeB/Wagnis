package at.aau.wagnis.gamestate;

import java.util.List;
import java.util.Map;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;
import at.aau.wagnis.server.GameServer;

public abstract class GameLogicState {

    public void setGameServer(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    protected GameServer gameServer;
    public void start(){}
    public void reinforce(){}
    public void attack(){}
    public void chooseAttack(int playerId, int sourceHubId, int targetHubId){}
    public void move(){}

    public void handleConnectionBusClosed() {
        Thread.currentThread().interrupt();
    }

    public void handleClosedConnection(int clientId) { }

    public void end(){}
    public void checkForVictory(){}


}
