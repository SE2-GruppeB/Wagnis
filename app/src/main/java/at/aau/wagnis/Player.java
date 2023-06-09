package at.aau.wagnis;


import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class Player {

    public static final int MAX_CARDS_IN_HAND = 5;
    private static final int BASE_TROOPS_PER_ROUND = 3;
    private int playerId;
    private int allTroopsPerRound;
    private int unassignedAvailableTroops;
    private Color playerColor;
    private Cards[] hand;
    private List<Hub> ownedHubs;

    public Player(Color playerColor, List<Hub> hubsToOwn) {
        this.playerColor = playerColor;
        this.hand = new Cards[MAX_CARDS_IN_HAND];
        this.ownedHubs = hubsToOwn;
        allTroopsPerRound = BASE_TROOPS_PER_ROUND;
        this.unassignedAvailableTroops = 60;
    }

    public Player() {
        this.hand = new Cards[MAX_CARDS_IN_HAND];
        allTroopsPerRound = BASE_TROOPS_PER_ROUND;
        this.unassignedAvailableTroops = 60;
    }

    public Player(int playerId) {
        this.ownedHubs = new ArrayList<>();
        this.playerId = playerId;
        //this.playerColor = Color.valueOf(Color.BLACK);
        this.hand = new Cards[MAX_CARDS_IN_HAND];
        allTroopsPerRound = BASE_TROOPS_PER_ROUND;
        this.unassignedAvailableTroops = 60;
    }

    public List<Hub> getOwnedHubs() {
        return ownedHubs;
    }

    public void setOwnedHubs(List<Hub> ownedHubs) {
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
        if (hand.length != MAX_CARDS_IN_HAND) {
            throw new IllegalArgumentException("Unsupported length of Cards Array");
        }
        this.hand = hand;
    }

    public void addHub(Hub hub) {
        this.ownedHubs.add(hub);
    }

    public void removeHub(Hub hub) {
        this.ownedHubs.remove(hub);
    }

    public int getUnassignedAvailableTroops() {
        return this.unassignedAvailableTroops;
    }

    public void setUnassignedAvailableTroops(int unassignedAvailableTroops) {
        this.unassignedAvailableTroops = unassignedAvailableTroops;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public boolean addCardToHand(Cards card) {
        for (int i = 0; i < MAX_CARDS_IN_HAND; i++) {
            if (hand[i] == null) {
                hand[i] = card;
                return true;
            }
        }
        return false;
    }

    public void deleteCardById(int i) {
        if (hand[i] == null) {
            throw new IllegalArgumentException("Card not in players' hand");
        }
        hand[i].placeCardInDeck();
        hand[i] = null;
    }

    public void sortHand() {
        Cards[] sortedHand = new Cards[MAX_CARDS_IN_HAND];
        int y = 0;
        for (int i = 0; i < MAX_CARDS_IN_HAND; i++) {
            if (hand[i] != null) {
                sortedHand[y] = hand[i];
                y++;
            }
        }
        this.hand = sortedHand;
    }

    public void deleteGroupOfCardPerId(int id1, int id2, int id3) {
        deleteCardById(id1);
        deleteCardById(id2);
        deleteCardById(id3);
    }

    public void useCards(int firstCId, int secCId, int thirdCId) {
        if (Cards.checkIfCardSameType(hand[firstCId], hand[secCId], hand[thirdCId])) {
            if (hand[firstCId].getType().equals(Troops.INFANTRY)) {
                allTroopsPerRound += 1;
            } else if (hand[firstCId].getType().equals(Troops.CAVALRY)) {
                allTroopsPerRound += 3;
            } else {
                allTroopsPerRound += 5;
            }
            deleteGroupOfCardPerId(firstCId, secCId, thirdCId);
        } else if (Cards.checkIfEachCardDiffType(hand[firstCId], hand[secCId], hand[thirdCId])) {
            allTroopsPerRound += 10;
            deleteGroupOfCardPerId(firstCId, secCId, thirdCId);
        }
    }

    public int getUnassignedTroops() {
        return this.unassignedAvailableTroops;
    }

    public int getAllTroopsPerRound() {
        return this.allTroopsPerRound;
    }

    public int calcTroopsToDeploy() {
        double ownedhubs = ownedHubs.size();
        double oneTenth = ownedhubs / 10;
            while (ownedhubs - oneTenth > 0){
                allTroopsPerRound++;
                ownedhubs-= oneTenth;
            }


        return allTroopsPerRound;
    }

    public void resetTroopsPerRound() {
        allTroopsPerRound = BASE_TROOPS_PER_ROUND;
    }

    public int countAmountTroops() {
        int amountTroops = 0;
        for (Hub hub : this.ownedHubs) {
            amountTroops += hub.getAmountTroops();
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
