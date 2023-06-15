package at.aau.wagnis.gamestate;


import java.util.List;
import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;
import at.aau.wagnis.server.GameServer;

public abstract class GameLogicState {

    private static final int CHEAT_TROOPS_COUNT = 10;
    private static final String CHEAT_COMMAND = "/givetroops";
    protected GameServer gameServer;

    public void setGameServer(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    public void start() {
    }

    public void chooseStart(List<Hub> unassignedCountries, List<Player> players) {
    }

    public void reinforce(List<Integer> hubsId , List<Integer> troops) {
    }

    public void attack() {
    }
    public void handleChatMessage(Integer clientId, String message) {
        this.gameServer.getGameData().addMessage(clientId,message);
        if(message.toLowerCase().trim().equals(CHEAT_COMMAND)) {
            Player cheatPlayer = this.gameServer.getGameData().getPlayers()
                    .stream()
                    .filter(player -> player.getPlayerId() == clientId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Could not find ClientId!"));

            int troopCount = cheatPlayer.getUnassignedAvailableTroops();
            troopCount = troopCount+ CHEAT_TROOPS_COUNT ;

            cheatPlayer.setUnassignedAvailableTroops(troopCount);
        }
    }

    public void chooseAttack(int playerId, int sourceHubId, int targetHubId) {
    }

    public boolean move(int numTroops) {
        return false;
    }

    public void chooseMove(int playerId, int sourceHubId, int targetHubId, int numTroops) {
    }

    public void handleConnectionBusClosed() {
        Thread.currentThread().interrupt();
    }


    public void handleNewConnection(int clientId) { }

    public void handleClosedConnection(int clientId) { }


    public void end() {
    }

    public void checkForVictory() {
    }

    public void onEntry() {
    }
    public void next(){

    }
}
