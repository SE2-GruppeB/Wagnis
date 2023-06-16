package at.aau.wagnis.ObjectTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.graphics.Color;
import android.widget.Button;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import at.aau.wagnis.Cards;
import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;
import at.aau.wagnis.Troops;

class PlayerTest {

    @Mock
    ArrayList<Hub> ownedHubs;
    @Mock
    Color color;
    Cards card1;
    Cards card2;
    Cards card3;
    Player player;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        player = new Player(color, ownedHubs);
    }

    void setUpCardMocks() {
        card1 = mock(Cards.class);
        card2 = mock(Cards.class);
        card3 = mock(Cards.class);
    }

    @AfterEach
    void tearDown() {
        player = null;
    }

    @Test
    void testGetSetOwnedHubs() {
        assertSame(ownedHubs, player.getOwnedHubs());

        player.setOwnedHubs(null);
        assertNull(player.getOwnedHubs());
    }

    @Test
    void testGetSetColor() {
        assertSame(color, player.getPlayerColor());

        player.setPlayerColor(null);
        assertNull(player.getPlayerColor());
    }


    @Test
    void testGetSetHand() {
        Cards[] cards = new Cards[5];
        assertDoesNotThrow(() -> player.setHand(cards));

        assertSame(cards, player.getHand());

        assertThrows(IllegalArgumentException.class, () -> player.setHand(new Cards[4]));
        assertSame(cards, player.getHand());
    }

    @Test
    void testAddRemoveHub() {
        Button button = mock(Button.class);
        when(ownedHubs.add(any())).thenReturn(true);
        when(ownedHubs.remove(any())).thenReturn(true);
        when(button.getId()).thenReturn(-1);

        player.addHub(new Hub(button));
        player.removeHub(new Hub(button));

        verify(ownedHubs).add(any());
        verify(ownedHubs).remove(any());
        verify(button, times(2)).getId();
    }

    @Test
    void testGetSetUnassignedTroops() {
        assertEquals(60, player.getUnassignedAvailableTroops());
        player.setUnassignedAvailableTroops(10);
        assertEquals(10, player.getUnassignedAvailableTroops());
    }

    @Test
    void testSetGetPlayerId() {
        player = new Player(7);

        assertEquals(7, player.getPlayerId());

        player.setPlayerId(8);
        assertEquals(8, player.getPlayerId());
    }


    @Test
    void addCardToHand() {
        Cards card = mock(Cards.class);

        assertTrue(player.addCardToHand(card));
    }

    @Test
    void deleteCardById() {
        setUpCardMocks();

        doNothing().when(card1).placeCardInDeck();
        doNothing().when(card2).placeCardInDeck();
        doNothing().when(card3).placeCardInDeck();

        Cards[] playerHand = new Cards[]{card1, card2, card3, null, null};
        player.setHand(playerHand);

        player.deleteCardById(1);
        Cards[] actualPlayerHand = player.getHand();
        assertEquals(2, countCardsInHand(actualPlayerHand));

        assertThrows(IllegalArgumentException.class, () -> player.deleteCardById(1));
        actualPlayerHand = player.getHand();
        assertEquals(2, countCardsInHand(actualPlayerHand));

        player.deleteCardById(2);
        actualPlayerHand = player.getHand();
        assertEquals(1, countCardsInHand(actualPlayerHand));

        verify(card1, times(0)).placeCardInDeck();
        verify(card2).placeCardInDeck();
        verify(card3).placeCardInDeck();
    }

    private int countCardsInHand(Cards[] hand) {
        int n = 0;
        for (Cards card : hand) {
            if (card != null) {
                n++;
            }
        }
        return n;
    }

    @Test
    void sortHand() {
        setUpCardMocks();

        Cards[] expectedHand = new Cards[]{null, null, card1, null, card2};

        player.setHand(expectedHand);
        player.sortHand();

        expectedHand = new Cards[]{card1, card2, null, null, null};
        Cards[] actualHand = player.getHand();

        assertTrue(checkEqualCardDeck(expectedHand, actualHand));
    }

    private boolean checkEqualCardDeck(Cards[] expectedHand, Cards[] actualHand) {
        if (expectedHand.length != actualHand.length) {
            return false;
        }

        for (int i = 0; i < expectedHand.length; i++) {
            if (expectedHand[i] != actualHand[i]) {
                return false;
            }
        }
        return true;
    }

    @Test
    void deleteGroupOfCardPerId() {
        setUpCardMocks();

        Cards[] expectedHand = new Cards[]{null, card1, card2, null, card3};
        player.setHand(expectedHand);
        player.deleteGroupOfCardPerId(1, 2, 4);

        expectedHand = new Cards[]{null, null, null, null, null};
        Cards[] actualHand = player.getHand();

        assertTrue(checkEqualCardDeck(expectedHand, actualHand));

        assertThrows(IllegalArgumentException.class, () -> player.deleteGroupOfCardPerId(1, 3, 4));
    }

    @Test
    void useCardsSameType() {
        setUpCardMocks();

        when(card1.getType()).thenReturn(Troops.CAVALRY);
        when(card2.getType()).thenReturn(Troops.CAVALRY);
        when(card3.getType()).thenReturn(Troops.CAVALRY);

        Cards[] playerHand = new Cards[]{card1, card2, card3, null, null};
        player.setHand(playerHand);


        int troopsBeforeUsingCards = player.getAllTroopsPerRound();
        player.useCards(0, 1, 2);

        Cards[] expectedHand = new Cards[]{null, null, null, null, null};
        Cards[] actualHand = player.getHand();

        assertTrue(checkEqualCardDeck(expectedHand, actualHand));
        assertEquals(troopsBeforeUsingCards + 3, player.getAllTroopsPerRound());

        verify(card1, times(4)).getType();
        verify(card2, times(1)).getType();
        verify(card3, times(1)).getType();
    }

    @Test
    void useCardsDifferentType() {
        setUpCardMocks();

        when(card1.getType()).thenReturn(Troops.CAVALRY);
        when(card2.getType()).thenReturn(Troops.INFANTRY);
        when(card3.getType()).thenReturn(Troops.ARTILLERY);

        Cards[] playerHand = new Cards[]{card1, card2, card3, null, null};
        player.setHand(playerHand);

        int troopsBeforeUsingCards = player.getAllTroopsPerRound();
        player.useCards(0, 1, 2);

        Cards[] expectedHand = new Cards[]{null, null, null, null, null};
        Cards[] actualHand = player.getHand();


        assertTrue(checkEqualCardDeck(expectedHand, actualHand));
        assertEquals(troopsBeforeUsingCards + 10, player.getAllTroopsPerRound());

        verify(card1, times(3)).getType();
        verify(card2, times(3)).getType();
        verify(card3, times(2)).getType();

        player.resetTroopsPerRound();
        assertEquals(3, player.getAllTroopsPerRound());
    }

    @Test
    void countAmountTroops() {
        Hub hub = mock(Hub.class);
        ownedHubs = new ArrayList<>();
        ownedHubs.add(hub);
        player = new Player(color, ownedHubs);

        when(hub.getAmountTroops()).thenReturn(7);

        assertEquals(7, player.countAmountTroops());

        verify(hub).getAmountTroops();
    }

    @Test
    void calculateTroopsTest() {
        Player player2 = new Player();
        ArrayList<Hub> hubs = new ArrayList<>();
        hubs.add(new Hub(1));
        hubs.add(new Hub(2));
        hubs.add(new Hub(3));
        hubs.add(new Hub(4));
        hubs.add(new Hub(5));
        hubs.add(new Hub(6));
        player2.setOwnedHubs(hubs);
        assertEquals(4,player2.calcTroopsToDeploy());

    }

    @ParameterizedTest
    @ValueSource(ints = {7, 2, 9, 22, 8})
    void testAssignTroops(int troopsToDeploy) {
        int expectedUnassigned = player.getUnassignedAvailableTroops() - troopsToDeploy;

        player.assignTroops(troopsToDeploy);

        assertEquals(expectedUnassigned, player.getUnassignedAvailableTroops());
    }

    @Test
    void addCardToFullHand() {
        for(int i = 0; i < 5; i++) {
            assertTrue(player.addCardToHand(mock(Cards.class)));
        }

        assertFalse(player.addCardToHand(mock(Cards.class)));
    }

}
