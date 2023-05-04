package at.aau.wagnis;


import android.graphics.Color;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {

    private static int maxCardsInHand = 5;
    private static int baseTroopsPerRound = 3;
    private int playerId;
    private int allTroopsPerRound;
    private int unassignedAvailableTroops;
    private Color playerColor;
    private Cards[] hand;
    private ArrayList<Hub> ownedHubs;

    public Player(Color playerColor, ArrayList<Hub> HubsToOwn) {
        this.playerColor = playerColor;
        this.hand = new Cards[maxCardsInHand];
        this.ownedHubs = HubsToOwn;
        allTroopsPerRound = baseTroopsPerRound;
        this.unassignedAvailableTroops = 60;
    }

    public Player(){
        this.hand = new Cards[maxCardsInHand];
        allTroopsPerRound = baseTroopsPerRound;
        this.unassignedAvailableTroops = 60;
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

    public void addHub(Hub hub){
        this.ownedHubs.add(hub);
    }

    public void removeHub(Hub hub){
        this.ownedHubs.remove(hub);
    }
    public void setHand(Cards[] hand) {
        if(hand.length != maxCardsInHand){
            throw new IllegalArgumentException("Unsupported length of Cards Array");
        }
        this.hand = hand;
    }

    public int getUnassignedAvailableTroops() {
        return this.unassignedAvailableTroops;
    }

    public void setUnassignedAvailableTroops(int unassignedAvailableTroops) {
        this.unassignedAvailableTroops = unassignedAvailableTroops;
    }

    public Player(int playerId) {
        this.ownedHubs = new ArrayList<>();
        this.playerId = playerId;
        //this.playerColor = Color.valueOf(Color.BLACK);
        this.hand = new Cards[maxCardsInHand];
        allTroopsPerRound = baseTroopsPerRound;
        this.unassignedAvailableTroops = 60;
    }

    public int getPlayerId() {
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

    public int countAmountTroops(){
        int amountTroops = 0;
        for(Hub hub : this.ownedHubs){
            amountTroops+=hub.getAmountTroops();
        }
        return amountTroops;
    }

    @Override
    public String toString() {
        return "Player{" +
                "playerId=" + playerId +
                ", unassignedAvailableTroops=" + unassignedAvailableTroops +
                ", amountOfTroopsTotal=" + countAmountTroops() +
                ", ownedHubs=" + ownedHubs +
                '}';
    }
}
