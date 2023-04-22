package at.aau.wagnis;

import android.util.Log;

import java.security.SecureRandom;
import java.util.Random;

public class Deck {

    private static Cards[] cards;
    private static boolean[] isinDeck;

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
                newCard = new Cards(hubId, Troops.INFANTRY);
            } else if (trooper == 1) {
                createMessageForCardGen(hubId, Troops.CAVALRY);
                newCard = new Cards(hubId, Troops.CAVALRY);
            } else {
                createMessageForCardGen(hubId, Troops.ARTILLERY);
                newCard = new Cards(hubId, Troops.ARTILLERY);
            }
            cards[i] = newCard;
            isinDeck[i] = true;
        }
    }

    public void createMessageForCardGen(int hubId, Troops trooper){
        String info ="Info",message = " Created new Card ID :", type = "Type";
        Log.d(info,message + hubId + type + trooper);

    }

    public static  Cards drawCardFromDeck(){
        Random randomGen = new SecureRandom();
        int cardPlaceinDeck = randomGen.nextInt(cards.length);
        if (isinDeck[cardPlaceinDeck]) {
            isinDeck[cardPlaceinDeck] = false;
            return cards[cardPlaceinDeck];
        } else if (numberOfCardsInDeck() == 0) {
            return null;
        } else {
            for (int i = 0; i < cards.length; i++) {
                if(i + cardPlaceinDeck < cards.length){
                    if (isinDeck[i + cardPlaceinDeck]) {
                        isinDeck[cardPlaceinDeck + i] = false;
                        return cards[cardPlaceinDeck + i];
                    }
                }else {
                    if(isinDeck[(cardPlaceinDeck + i) % cards.length]) {
                    isinDeck[(cardPlaceinDeck + i) % cards.length] = false;
                    return cards[(cardPlaceinDeck + i) % cards.length];
                    }
                }
            }
        }
        return null;
    }

    public static int numberOfCardsInDeck(){
        int count = 0;
        for (int i = 0; i < isinDeck.length; i++) {
            if (isinDeck[i]) {
                count++;
            }
        }
        if (count == 0) {Log.d("Deck", "Deck is empty");}
        return count;
    }

    public static void placeCardInDeck(Cards card){
        for (int i = 0; i < cards.length; i++) {
            if (cards[i].equals(card)){
                isinDeck[i] = true;
            }
        }
    }
}
