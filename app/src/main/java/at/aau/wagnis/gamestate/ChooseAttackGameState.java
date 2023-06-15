package at.aau.wagnis.gamestate;

import java.util.List;

import at.aau.wagnis.Adjacency;
import at.aau.wagnis.Hub;

public class ChooseAttackGameState extends GameLogicState {

    Hub sourceHub;
    //  Hub Angriff
    Hub targetHub;
    // Hub Verteidiger


    /**
     * Wählt einen Angriff aus und ändert den Spielzustand entsprechend.
     *
     * @param playerId    Die ID des Spielers, der den Angriff auswählt.
     * @param sourceHubId Die ID des Hubs, von dem aus der Angriff erfolgt.
     * @param targetHubId Die ID des Ziels des Angriffs.
     */

    @Override
    public void chooseAttack(int playerId, int sourceHubId, int targetHubId){
        if (sourceHubId == targetHubId) {
            throw new IllegalArgumentException("Der Quell-Hub und der Ziel-Hub dürfen nicht identisch sein!");
        }


        //Prüft ob source und target nicht der gleiche Hub sind
        List<Hub> hubs = this.gameServer.getGameData().getHubs();
        // Liste aller Hubs im Spiel
        List<Adjacency> adj = this.gameServer.getGameData().getAdjacencies();
        // Liste aller benachbarten Hubpaare

        // Durchlaufen aller Knoten
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

        // Überprüfen, ob der Spieler den Angriff auf den eigenen Hub startet
        if (sourceHub.getOwner() == targetHub.getOwner()) {
            throw new IllegalArgumentException("Du kannst deinen eigenen Hub nicht angreifen!");
        }

        /*
        if(sourceHub.getOwner().getPlayerId() != gameServer.getGameData().getCurrentPlayer()){
            throw new IllegalArgumentException("Sourcehub muss dir gehören!");
        }*/

        // Durchlaufe alle benachbarten Hubpaare in der Liste
        for(Adjacency a : adj){
            //Überprüfe, ob das aktuelle Hubpaar entweder den sourceHub und targetHub und andersrum
            if(a.isInPair(sourceHub, targetHub)){
                this.gameServer.setGameLogicState(new AttackGameState(sourceHubId, targetHubId));
                // Spielzustand auf AttackGameState mit den übergebenen Angreifer-Hub und Verteidiger Hub
               return;
            }
        }
        // Nicht benachbart -> Fehlermeldung ausgeben oder anzeigen
        throw new IllegalArgumentException("Hubs sind nicht benachbart!");
    }
}
