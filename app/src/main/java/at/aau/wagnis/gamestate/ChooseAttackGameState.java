package at.aau.wagnis.gamestate;

import static at.aau.wagnis.GlobalVariables.adjacencies;

import at.aau.wagnis.Adjacency;
import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

public class ChooseAttackGameState extends GameLogicState {
    private boolean areHubsAdjacent(Hub sourceHub, Hub targetHub) {
    for (Adjacency adjacency : adjacencies) {
        if ((adjacency.getHub1() == sourceHub && adjacency.getHub2() == targetHub) ||
                (adjacency.getHub1() == targetHub && adjacency.getHub2() == sourceHub)) {
            return true;
        }
    }
    return false;
}
    @Override
    public void chooseAttack(int playerId, int sourceHubId, int targetHubId) {
        Hub sHub = new Hub(sourceHubId);
        Hub tHub = new Hub(targetHubId);
        if(areHubsAdjacent(sHub, tHub) == true) {
            this.gameServer.setGameLogicState(new AttackGameState(sourceHubId, targetHubId));
            //Erstellt zwei neue Hubs, mit übergebner ID von Source und Target prüft auf adjacency
        }
        else{
            throw new IllegalArgumentException("Nur angrenzende Hubs können Angegriffen werden!");
        }
    }

}
