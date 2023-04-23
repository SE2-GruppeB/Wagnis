package at.aau.wagnis;


import android.graphics.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Player {

    private static int maxCardsInHand = 5;
    private static int baseTroopsPerRound = 3;

    private static int playerId;

    private int allTroopsPerRound;
    private Color playerColor;
    private Cards[] hand;
    private ArrayList<Hub> ownedHubs;

    public Player(Color playerColor, ArrayList<Hub> ownedHubs) {
        this.playerColor = playerColor;
        this.hand = new Cards[maxCardsInHand];
        this.ownedHubs = ownedHubs;
        allTroopsPerRound = baseTroopsPerRound;
    }

    public ArrayList<Hub> getOwnedHubs() {
        return ownedHubs;
    }

    public void setOwnedHubs(ArrayList<Hub> ownedHubs) {
        this.ownedHubs = ownedHubs;
    }

    public Color getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(Color playerColor) {
        this.playerColor = playerColor;
    }

    public Cards[] getHand() {
        return hand;
    }

    public void setHand(Cards[] hand) {
        this.hand = hand;
    }

    public Player(int playerId) {
        this.playerId = playerId;
    }

    public static int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public boolean addCardToHand(Cards card){
        for (int i = 0; i < maxCardsInHand ; i++){
            if (hand[i] == null){
                hand[i] = card;
            }
        }
        return false;
    }

    public void deleteCardById(int i){
        hand[i].placeCardInDeck();
        hand[i] = null;
    }

    public void sortHand(){
        Cards[] sortedHand = new Cards[maxCardsInHand];
        int y = 0;
        for (int i = 0; i < maxCardsInHand ; i++){
            if (hand[i] != null){
                sortedHand[y] = hand[i];
                y++;
            }
        }
        this.hand = sortedHand;
    }

    public void deleteGroupOfCardPerId(int id1,int id2,int id3){
        deleteCardById(id1);
        deleteCardById(id2);
        deleteCardById(id3);
    }

    public void useCards(int firstCId,int secCId,int thirdCId){
         if (Cards.checkIfCardSameType(hand[firstCId],hand[secCId],hand[thirdCId])){
             if (hand[firstCId].getType().equals(Troops.INFANTRY)){
                 allTroopsPerRound += 1;
             } else if (hand[firstCId].getType().equals(Troops.CAVALRY)) {
                 allTroopsPerRound += 3;
             } else {
                 allTroopsPerRound += 5;
             }
             deleteGroupOfCardPerId(firstCId,secCId,thirdCId);
         } else if (Cards.checkIfEachCardDiffType(hand[firstCId],hand[secCId],hand[thirdCId])){
             allTroopsPerRound += 10;
             deleteGroupOfCardPerId(firstCId,secCId,thirdCId);
         }
    }

    public int calcTroopsToDeploy(){
        //TODO implement Method waiting

        return allTroopsPerRound;
    }

    public void resetTroopsPerRound() {
       allTroopsPerRound = baseTroopsPerRound;
    }


    public void removeHub(Hub targetHub) {
        //TODO implement Method
    }
}
