package at.aau.wagnis.gamestate;

import java.util.List;

import at.aau.wagnis.Adjacency;
import at.aau.wagnis.Hub;

public class ChooseMoveState extends GameLogicState {

    Hub sourceHub;
    Hub targetHub;


    @Override
    public void chooseMove(int playerId, int sourceHubId, int targetHubId, int numTroops) {

        List<Hub> hubs = this.gameServer.getGameData().getHubs();
        List<Adjacency> adj = this.gameServer.getGameData().getAdjacencies();



        for(Hub h : hubs){
            if(h.getId()==sourceHubId){
                sourceHub=h;
                // Findet den Hub welcher übereinstimmt und weise ihn sourceHub zu
            }
            if(h.getId()==targetHubId){
                targetHub=h;
                // Findet den Hub welcher übereinstimmt und weise ihn targetHub zu
            }
        }

        for(Adjacency a : adj){
            // Überprüfe, ob das aktuelle Hubpaar entweder den sourceHub und targetHub und andersrum
            if(a.isInPair(sourceHub, targetHub)){
                this.gameServer.setGameLogicState(new MoveTroopsState(sourceHubId, targetHubId));
                // Spielzustand auf AttackGameState mit den übergebenen Angreifer-Hub und Verteidiger Hub
                return;
            }
        }
        // Nicht benachbart -> Fehlermeldung ausgeben oder anzeigen
        throw new IllegalArgumentException("Hubs sind nicht benachbart!");


    }


}