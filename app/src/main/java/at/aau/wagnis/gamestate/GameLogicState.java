package at.aau.wagnis.gamestate;

import java.util.List;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;
import at.aau.wagnis.server.GameServer;

public abstract class GameLogicState {

    protected GameServer gameServer;

    public void setGameServer(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    public void start() {
    }

    public void chooseStart(List<Hub> unassignedCountries, List<Player> players) {
    }

    public void reinforce() {
    }

    public void attack() {
    }

    public void chooseAttack(int playerId, int sourceHubId, int targetHubId) {
    }

    public void move() {
    }

    public void chooseMove() {
    }

    public void handleConnectionBusClosed() {
        Thread.currentThread().interrupt();
    }

    public void handleClosedConnection(int clientId) {
    }

    public void end() {
    }

    public void checkForVictory() {
    }


}
