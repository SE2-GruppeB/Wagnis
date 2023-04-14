package at.aau.wagnis;

import android.graphics.Color;

public class Player {

    private static int maxCardsInHand = 5;
    private Color playerColor;
    private Cards[] hand;

    public Player(Color playerColor) {
        this.playerColor = playerColor;
        this.hand = new Cards[maxCardsInHand];
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

    public boolean addCardToHand(Cards card){
        for (int i = 0; i < maxCardsInHand ; i++){
            if (hand[i] == null){
                hand[i] = card;
            }
        }
        return false;
    }

    public void deleteCardById(int i){
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


}
