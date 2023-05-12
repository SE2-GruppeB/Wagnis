package at.aau.wagnis;

import java.security.SecureRandom;
import java.util.Random;

public class Deck {

    private final Cards[] cards;
    private final boolean[] isinDeck;

    public Deck(int maxHubCount) {
        if (maxHubCount == 0) { throw new IllegalArgumentException("maxHubCount cannot be 0"); }
        this.cards = new Cards[maxHubCount];
        this.isinDeck = new boolean[maxHubCount];
        this.fillDeck();
    }

    public Cards[] getCards() {
        return cards;
    }

    public boolean[] getIsInDeck() {
        return isinDeck;
    }

    private void fillDeck(){
        for (int i = 0; i < cards.length; i++) {
            Cards newCard;
            int trooper =  i % 3;
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
        //Log.d(info,message + hubId + type + trooper);

    }

    public  int numberOfCardsInDeck(){
        int count = 0;
        for (boolean b : isinDeck) {
            if (b) {
                count++;
            }
        }
        if (count == 0) {
            //Log.d("Deck", "Deck is empty");
        }
        return count;
    }

    public  void placeCardInDeck(Cards card){
        for (int i = 0; i < cards.length; i++) {
            if (cards[i].equals(card)){
                isinDeck[i] = true;
                return;
            }
        }
        throw new IllegalArgumentException("Card is not in deck");
    }


    public  Cards drawCardFromDeck() {
        Random randomGen = new SecureRandom();
        if (numberOfCardsInDeck() == 0) {return null;}
        int placeInDeck = randomGen.nextInt(numberOfCardsInDeck());
        return drawCardFromDeckPerID(placeInDeck);
    }

    public  Cards drawCardFromDeckPerID(int placeInDeck) {
        if (numberOfCardsInDeck() == 0) {return null;}
        placeInDeck = placeInDeck % cards.length;
        if (isinDeck[placeInDeck]) {
            isinDeck[placeInDeck] = false;
            return cards[placeInDeck];
        } else {
           return drawCardFromDeckPerID(placeInDeck + 1);
        }
    }
}
