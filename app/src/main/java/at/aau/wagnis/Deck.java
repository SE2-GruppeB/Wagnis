package at.aau.wagnis;

import android.util.Log;

import java.security.SecureRandom;
import java.util.Random;

public class Deck {

    private Cards[] cards;
    private boolean[] isinDeck;

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

    public Cards drawRandomCard(){
        Random randomGen = new SecureRandom();
        int cardPlaceinDeck = randomGen.nextInt(cards.length);
        return cards[cardPlaceinDeck];
    }


}
