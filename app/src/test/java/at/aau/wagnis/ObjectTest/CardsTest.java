package at.aau.wagnis.ObjectTest;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import at.aau.wagnis.Cards;
import at.aau.wagnis.Deck;
import at.aau.wagnis.Troops;

class CardsTest {

    //getterSetterTests
    @Test
    void getSetDecktest() {
        Deck deck = new Deck(1);
        Deck deck2 = new Deck(1);
        Cards cards = new Cards(1, Troops.ARTILLERY,deck);
        assertEquals(deck,cards.getDeck());
        cards.setDeck(deck2);
        assertEquals(deck2,cards.getDeck());
    }

    @Test
    void SetDeckNulltest() {
        Cards cards = new Cards(1,Troops.ARTILLERY,new Deck(1));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {cards.setDeck(null);});
        String expectedMessage = "Deck can not be null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    void getSetIdtest() {
        int id = 1;
        int id2 = 2;
        Cards cards = new Cards(id,Troops.ARTILLERY,new Deck(1));
        assertEquals(id,cards.getId());
        cards.setId(id2);
        assertEquals(id2,cards.getId());
    }

    @Test
    void SetIdNegativetest() {
        Cards cards = new Cards(1,Troops.ARTILLERY,new Deck(1));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {cards.setId(-1);});
        String expectedMessage = "Id can not be negative";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    void getSetTroopstest() {
        Cards cards = new Cards(1,Troops.ARTILLERY,new Deck(1));
        assertEquals(Troops.ARTILLERY,cards.getType());
        cards.setType(Troops.INFANTRY);
        assertEquals(Troops.INFANTRY,cards.getType());
    }

    @Test
    void SetTroopNulltest() {
        Cards cards = new Cards(1,Troops.ARTILLERY,new Deck(1));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {cards.setType(null);});
        String expectedMessage = "Type can not be null";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage,actualMessage);
    }

    //checkIfCardSameType(Cards first, Cards second, Cards third) Tests

    @Test
    void checkIfCardHaveTheSameTypeTest() {
        Cards cards = new Cards(1,Troops.ARTILLERY,new Deck(3));
        Cards cards2 = new Cards(2,Troops.ARTILLERY,new Deck(3));
        Cards cards3 = new Cards(3,Troops.ARTILLERY,new Deck(3));

        assertTrue(Cards.checkIfCardSameType(cards, cards2, cards3));
    }

    @Test
    void checkIfCardHaveNotTheSameTypeTest() {
        Cards cards = new Cards(1,Troops.ARTILLERY,new Deck(3));
        Cards cards2 = new Cards(2,Troops.CAVALRY,new Deck(3));
        Cards cards3 = new Cards(3,Troops.ARTILLERY,new Deck(3));

        assertFalse(Cards.checkIfCardSameType(cards, cards2, cards3));
    }

    @Test
    void checkIfCardSameTypeExceptionTest() {
        Cards cards = new Cards(1,Troops.ARTILLERY,new Deck(3));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {Cards.checkIfCardSameType(cards,cards,cards);});
        String expectedMessage = "you cant use the same card thrice";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage,actualMessage);
    }

    //checkIfEachCardDiffType(Cards first, Cards second, Cards third)

    @Test

    void checkIfEachCardNotDiffTest() {
        Cards cards = new Cards(1,Troops.ARTILLERY,new Deck(3));
        Cards cards2 = new Cards(2,Troops.ARTILLERY,new Deck(3));
        Cards cards3 = new Cards(3,Troops.ARTILLERY,new Deck(3));

        assertFalse(Cards.checkIfEachCardDiffType(cards, cards2, cards3));
    }

    @Test

    void checkIfEachCardNot2DiffTest() {
        Cards cards = new Cards(1,Troops.ARTILLERY,new Deck(3));
        Cards cards2 = new Cards(2,Troops.CAVALRY,new Deck(3));
        Cards cards3 = new Cards(3,Troops.CAVALRY,new Deck(3));

        assertFalse(Cards.checkIfEachCardDiffType(cards, cards2, cards3));
    }

    @Test

    void checkIfEachCardNot3DiffTest() {
        Cards cards = new Cards(1,Troops.CAVALRY,new Deck(3));
        Cards cards2 = new Cards(2,Troops.CAVALRY,new Deck(3));
        Cards cards3 = new Cards(3,Troops.ARTILLERY,new Deck(3));

        assertFalse(Cards.checkIfEachCardDiffType(cards, cards2, cards3));
    }

    @Test

    void checkIfEachCardNot4DiffTest() {
        Cards cards = new Cards(1,Troops.ARTILLERY,new Deck(3));
        Cards cards2 = new Cards(2,Troops.CAVALRY,new Deck(3));
        Cards cards3 = new Cards(3,Troops.ARTILLERY,new Deck(3));

        assertFalse(Cards.checkIfEachCardDiffType(cards, cards2, cards3));
    }

    @Test

    void checkIfEachCardDiffTest() {
        Cards cards = new Cards(1,Troops.ARTILLERY,new Deck(3));
        Cards cards2 = new Cards(2,Troops.CAVALRY,new Deck(3));
        Cards cards3 = new Cards(3,Troops.INFANTRY,new Deck(3));

        assertTrue(Cards.checkIfEachCardDiffType(cards, cards2, cards3));
    }

    @Test

    void checkIfEachCardDiffExceptionTest() {
        Cards cards = new Cards(1,Troops.ARTILLERY,new Deck(3));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {Cards.checkIfEachCardDiffType(cards,cards,cards);});
        String expectedMessage = "you cant use the same card thrice";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage,actualMessage);
    }

    // equals Test

    @Test

    void equalsTest() {
        Cards cards = new Cards(1, Troops.ARTILLERY, new Deck(3));
        Cards cards2 = new Cards(1, Troops.ARTILLERY, new Deck(3));

        assertNotEquals(cards, cards2);
    }

    @Test

    void equalsTest2() {
        Cards cards = new Cards(1,Troops.ARTILLERY,new Deck(3));
        Cards cards3 = new Cards(2,Troops.ARTILLERY,new Deck(3));

        assertNotEquals(cards, cards3);
    }

    @Test

    void equalsTest3() {
        Cards cards = new Cards(1,Troops.ARTILLERY,new Deck(3));
        Cards cards4 = new Cards(1,Troops.ARTILLERY,new Deck(4));

        assertNotEquals(cards, cards4);
    }

    @Test

    void equalsTest4() {
        Cards cards = new Cards(1,Troops.ARTILLERY,new Deck(3));
        Cards cards5 = new Cards(1,Troops.ARTILLERY,new Deck(5));

        assertNotEquals(cards, cards5);
    }

    @Test

    void equalsTest5() {
        Cards cards = new Cards(1,Troops.ARTILLERY,new Deck(3));
        Cards cards6 = new Cards(1,Troops.ARTILLERY,new Deck(6));

        assertNotEquals(cards, cards6);
    }

    //HaschCode Test

    @Test

    void hashCodeTest() {
        Cards cards = new Cards(1, Troops.ARTILLERY, new Deck(3));
        Cards cards2 = new Cards(1, Troops.ARTILLERY, new Deck(3));

        assertEquals(cards.hashCode(), cards2.hashCode());
    }

    @Test

    void hashCodeTest2() {
        Cards cards = new Cards(1, Troops.ARTILLERY, new Deck(3));
        Cards cards2 = new Cards(12, Troops.ARTILLERY, new Deck(3));

        assertNotEquals(cards.hashCode(), cards2.hashCode());
    }
}
