package at.aau.wagnis.gamestate;

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

    public void handleNewConnection(int clientId) { }

    public void handleClosedConnection(int clientId) { }

    public void end(){}
    public void checkForVictory(){}


}
