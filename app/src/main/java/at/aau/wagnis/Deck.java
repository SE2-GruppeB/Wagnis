package at.aau.wagnis;

import android.util.Log;

import java.security.SecureRandom;
import java.util.Random;

public class Deck {

    private final Cards[] cards;
    private final boolean[] isinDeck;

    public Deck(int maxHubCount) {
        this.cards = new Cards[maxHubCount];
        this.isinDeck = new boolean[maxHubCount];
    }

    public void fillDeck(){
        for (int i = 0; i < cards.length; i++) {
            Cards newCard;
            int trooper = (100 + i) % 3;
            int hubId = 100 + i;
            if (trooper == 0){
                createMessageForCardGen(hubId, Troops.INFANTRY);
                newCard = new Cards(hubId, Troops.INFANTRY,this);
            } else if (trooper == 1) {
                createMessageForCardGen(hubId, Troops.CAVALRY);
                newCard = new Cards(hubId, Troops.CAVALRY,this);
            } else {
                createMessageForCardGen(hubId, Troops.ARTILLERY);
                newCard = new Cards(hubId, Troops.ARTILLERY,this);
            }
            cards[i] = newCard;
            isinDeck[i] = true;
        }
    }

    public void createMessageForCardGen(int hubId, Troops trooper){
        String info ="Info";
        String message = " Created new Card ID :";
        String type = "Type";
        Log.d(info,message + hubId + type + trooper);

    }

    public  int numberOfCardsInDeck(){
        int count = 0;
        for (boolean b : isinDeck) {
            if (b) {
                count++;
            }
        }
        if (count == 0) {Log.d("Deck", "Deck is empty");}
        return count;
    }

    public  void placeCardInDeck(Cards card){
        for (int i = 0; i < cards.length; i++) {
            if (cards[i].equals(card)){
                isinDeck[i] = true;
            }
        }
    }


    public  Cards drawCardFromDeck() {
        Random randomGen = new SecureRandom();
        if (numberOfCardsInDeck() == 0) {return null;}
        int placeInDeck = randomGen.nextInt(numberOfCardsInDeck());
        return drawCardFromDeckperID(placeInDeck);
    }

    public  Cards drawCardFromDeckperID(int placeInDeck) {
        if (numberOfCardsInDeck() == 0) {return null;}
        placeInDeck = placeInDeck % cards.length;
        if (isinDeck[placeInDeck]) {
            isinDeck[placeInDeck] = false;
            return cards[placeInDeck];
        } else {
           return drawCardFromDeckperID(placeInDeck + 1);
        }
    }
}
