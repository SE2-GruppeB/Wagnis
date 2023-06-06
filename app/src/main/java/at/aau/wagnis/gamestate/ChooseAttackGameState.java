package at.aau.wagnis.gamestate;

import java.util.List;

import at.aau.wagnis.Adjacency;
import at.aau.wagnis.Hub;

public class ChooseAttackGameState extends GameLogicState {
   Hub sourcehub;
   //  Hub Angriff
   Hub targethub;
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
            sourcehub=h;
            // Findet den Hub welcher übereinstimmt und weise ihn sourcehub zu
         }
         if(h.getId()==sourceHubId){
            sourcehub=h;
            // Findet den Hub welcher übereinstimmt und weise ihn targethub zu
         }
      }
      // Durchlaufe alle benachbarten Hubpaare in der Liste
      for(Adjacency a : adj){
         // Überprüfe, ob das aktuelle Hubpaar entweder den sourcehub und targethub und andersrum
         if(a.getHub1().equals(sourcehub)&& a.getHub2().equals(targethub)||a.getHub1().equals(targethub)&& a.getHub2().equals(sourcehub)){
            this.gameServer.setGameLogicState(new AttackGameState(sourceHubId, targetHubId));
            // Spielzustand auf AttackGameState mit den übergebenen Angreifer-Hub und Verteidiger Hub
         }
      }
   }
}
