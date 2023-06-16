package at.aau.wagnis;

import android.util.Log;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

public class Deck {

    private Cards[] cards;
    private final boolean[] isinDeck;

    public Deck(int maxHubCount) {
        if (maxHubCount == 0) {
            throw new IllegalArgumentException("maxHubCount cannot be 0");
        }
        this.cards = new Cards[maxHubCount];
        this.isinDeck = new boolean[maxHubCount];
        this.fillDeck();
    }

    public Deck() {
        this.cards = new Cards[5];
        this.isinDeck = new boolean[5];
    }

    public void setCards(Cards[] cards){
        this.cards = cards;
        Arrays.fill(isinDeck, false);
    }

    public Cards[] getCards() {
        return cards;
    }

    public boolean[] getIsInDeck() {
        return isinDeck;
    }

    private void fillDeck() {
        for (int i = 0; i < cards.length; i++) {
            Cards newCard;
            int trooper = i % 3;
            int hubId = 100 + i;
            if (trooper == 0) {
                createMessageForCardGen(hubId, Troops.INFANTRY);
                newCard = new Cards(hubId, Troops.INFANTRY, this);
            } else if (trooper == 1) {
                createMessageForCardGen(hubId, Troops.CAVALRY);
                newCard = new Cards(hubId, Troops.CAVALRY, this);
            } else {
                createMessageForCardGen(hubId, Troops.ARTILLERY);
                newCard = new Cards(hubId, Troops.ARTILLERY, this);
            }
            cards[i] = newCard;
            isinDeck[i] = true;
        }
    }

    public String createMessageForCardGen(int hubId, Troops trooper) {
        String message = " Created new Card ID :";
        String type = "Type :";
        return message + hubId + type + trooper;
    }

    public int numberOfCardsInDeck() {
        int count = 0;
        for (boolean b : isinDeck) {
            if (b) {
                count++;
            }
        }
        return count;
    }

    public void placeCardInDeck(Cards card) {
        for (int i = 0; i < cards.length; i++) {
            Log.d("test", "" + cards[i].toString() + card .toString());
            if (cards[i].equals(card)) {
                isinDeck[i] = true;
                return;
            }
        }
        throw new IllegalArgumentException("Card is not in deck");
    }


    public Cards drawCardFromDeck() {
        Random randomGen = new SecureRandom();
        if (numberOfCardsInDeck() == 0) {
            return null;
        }
        int placeInDeck = randomGen.nextInt(numberOfCardsInDeck());
        return drawCardFromDeckPerID(placeInDeck);
    }

    public Cards drawCardFromDeckPerID(int placeInDeck) {
        if (numberOfCardsInDeck() == 0) {
            return null;
        }
        placeInDeck = placeInDeck % cards.length;
        if (isinDeck[placeInDeck]) {
            isinDeck[placeInDeck] = false;
            return cards[placeInDeck];
        } else {
            return drawCardFromDeckPerID(placeInDeck + 1);
        }
    }
}
