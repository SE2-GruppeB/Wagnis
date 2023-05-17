package at.aau.wagnis.ObjectTest;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import at.aau.wagnis.Cards;
import at.aau.wagnis.Deck;
import at.aau.wagnis.Troops;

class DeckTest {

    Deck deck;
    int cardCount = 9;

    @BeforeEach
    void setUp() {
        deck = new Deck(cardCount);
    }

    @Test
    void constructorTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Deck(0);
        });
        String expectedMessage = "maxHubCount cannot be 0";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    // fillDeckTests and numberOfCardsInDeck test

    @Test
    void fillDeckTest() {
        assertEquals(deck.numberOfCardsInDeck(), cardCount);
    }

    @Test
    void fillDeckTest2() {
        for (int i = 0; i < deck.getCards().length; i++) {
            Cards cards;
            if (i % 3 == 0) {
                cards = new Cards(100 + i, Troops.INFANTRY, deck);
            } else if (i % 3 == 1) {
                cards = new Cards(100 + i, Troops.CAVALRY, deck);
            } else {
                cards = new Cards(100 + i, Troops.ARTILLERY, deck);
            }
            assertEquals(deck.getCards()[i], cards);
        }

    }

    @Test
    void numberOfCardsInDeckTest() {
        int i = 0;
        for (int y = 0; y < deck.getCards().length; y++) {
            i++;
        }
        assertEquals(i, cardCount);
    }

    //DrawCardFromDeckTests

    @Test
    void drawCardFromDeckTest() {
        Cards cards = deck.drawCardFromDeck();
        while (cards != null) {
            cards = deck.drawCardFromDeck();
        }

        for (int i = 0; i < deck.getIsInDeck().length; i++) {
            assertFalse(deck.getIsInDeck()[i]);
        }
    }

    @Test
    void drawCardFromDeckTest2() {
        for (int i = 0; i < cardCount; i++) {
            deck.drawCardFromDeck();
        }

        assertNull(deck.drawCardFromDeck());
    }

    @Test
    void drawCardFromDeckPerIdTest() {
        for (int i = 0; i < cardCount; i++) {
            assertEquals(deck.getCards()[i], deck.drawCardFromDeckPerID(i));
            assertFalse(deck.getIsInDeck()[i]);

        }

    }
}
