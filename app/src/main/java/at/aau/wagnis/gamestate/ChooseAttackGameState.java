package at.aau.wagnis.gamestate;

import java.util.List;

import at.aau.wagnis.Adjacency;
import at.aau.wagnis.Hub;

public class ChooseAttackGameState extends GameLogicState {
   Hub sourceHub;
   //  Hub Angriff
   Hub targetHub;
   // Hub Verteidiger

   @Override
   public void chooseAttack(int playerId, int sourceHubId, int targetHubId){
      List<Hub> hubs = this.gameServer.getGameData().getHubs();
      // Liste aller Hubs im Spiel
      List<Adjacency> adj = this.gameServer.getGameData().getAdjacencies();
      //Liste aller benachbarten Hubpaare

      // Durchlaufen aller Knoten
      for(Hub h :hubs){
         if(h.getId()==sourceHubId){
            sourceHub=h;
            // Findet den Hub welcher übereinstimmt und weise ihn sourceHub zu
         }
         if(h.getId()==sourceHubId){
            sourceHub=h;
            // Findet den Hub welcher übereinstimmt und weise ihn targetHub zu
         }
      }
      // Durchlaufe alle benachbarten Hubpaare in der Liste
      for(Adjacency a : adj){
         // Überprüfe, ob das aktuelle Hubpaar entweder den sourceHub und targetHub und andersrum
         if(a.getHub1().equals(sourceHub)&& a.getHub2().equals(targetHub)||a.getHub1().equals(targetHub)&& a.getHub2().equals(sourceHub)){
            this.gameServer.setGameLogicState(new AttackGameState(sourceHubId, targetHubId));
            // Spielzustand auf AttackGameState mit den übergebenen Angreifer-Hub und Verteidiger Hub
         }
      }
   }
}
