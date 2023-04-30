package at.aau.wagnis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DeckTest {

    Deck deck;
    int cardCount = 9;

    @Before
    public void setUp() {
        deck = new Deck(cardCount);
    }

    @After
    public void tearDown() {
        deck = new Deck(cardCount);
    }

    @Test
    public void constructorTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {new Deck(0);});
        String expectedMessage = "maxHubCount cannot be 0";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage,actualMessage);
    }

    // fillDeckTests and numberOfCardsInDeck test

    @Test
    public void fillDeckTest() {
        assertEquals(deck.numberOfCardsInDeck(), cardCount);
    }

    @Test
    public void fillDeckTest2() {
        for (int i = 0; i < deck.getCards().length; i++) {
            Cards cards;
            if(i % 3 == 0){
                 cards = new Cards(100 + i,Troops.INFANTRY,deck);
            } else if (i % 3 == 1){
                 cards = new Cards(100 + i,Troops.CAVALRY,deck);
            } else {
                 cards = new Cards(100 + i,Troops.ARTILLERY,deck);
            }
            assertEquals(deck.getCards()[i], cards);
        }

    }

    @Test
    public void numberOfCardsInDeckTest() {
        int i = 0;
        for (int y = 0; y < deck.getCards().length; y++) {
            i++;
        }
        assertEquals(i, cardCount);
    }

    //DrawCardFromDeckTests

    @Test
    public void drawCardFromDeckTest() {
        Cards cards = deck.drawCardFromDeck();
        while (cards != null) {
            cards = deck.drawCardFromDeck();
        }

        for (int i = 0; i < deck.getIsinDeck().length; i++) {
           assertFalse(deck.getIsinDeck()[i]);
        }
    }

    @Test
    public void drawCardFromDeckTest2() {
        for (int i = 0; i < cardCount; i++) {
           deck.drawCardFromDeck();
        }

        assertNull(deck.drawCardFromDeck());
    }

    @Test
    public void drawCardFromDeckPerIdTest() {
        for (int i = 0; i < cardCount; i++) {
            assertEquals(deck.getCards()[i],deck.drawCardFromDeckperID(i));
            assertFalse(deck.getIsinDeck()[i]);

        }

    }
}
