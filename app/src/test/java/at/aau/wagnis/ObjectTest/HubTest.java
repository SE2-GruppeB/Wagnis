package at.aau.wagnis.ObjectTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.widget.Button;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

class HubTest {

    static final int buttonId = 1;
    @Mock
    Button hubButton;
    Hub hub;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(hubButton.getId()).thenReturn(buttonId);
        hub = new Hub(hubButton);
    }

    @AfterEach
    void tearDown() {
        verify(hubButton).getId();

        hub = null;
    }

    @Test
    void getHubButton() {
        assertSame(hubButton, hub.getHubButton());
    }

    @Test
    void getId() {
        assertEquals(buttonId, hub.getId());
    }
/*
    @Test
    void setText() {
        doNothing().when(hubButton).setText(anyInt());

        hub.setText(12);

        verify(hubButton).setText(anyInt());
    }
    */


    @Test
    void getAmountTroops() {
        assertEquals(0, hub.getAmountTroops());
    }

    @Test
    void setAmountTroops() {
        int expectedTroopAmount = 14;
        hub.setAmountTroops(expectedTroopAmount);

        assertEquals(expectedTroopAmount, hub.getAmountTroops());
    }
    @Test
    void testCompareTo() {
        Hub tester = new Hub(5);
        Hub bigger = new Hub(6);
        Hub smaller = new Hub (4);
        Hub same = new Hub(5);

        assertEquals(-1,tester.compareTo(bigger));
        assertEquals(1,tester.compareTo(smaller));
        assertEquals(0,tester.compareTo(same));

    }
    @Test
    void testEquals() {
        Hub hub1 = new Hub(10);
        Hub hub2 = new Hub(15);

        assertTrue(hub1.equals(hub1));
        assertFalse(hub1.equals(hub2));
    }

    @Test
    void testSetText() {
        when(hubButton.getText()).thenReturn("Test");
        hub.setText("Test");
        verify(hub.getHubButton()).setText("Test");
        assertEquals("Test",hub.getHubButton().getText());
    }

    @Test
    void testHash() {
        Player owner = new Player(14);
        Hub hub1 = new Hub(10);
        hub1.setOwner(owner);
        Hub hub2 = new Hub(10);
        hub2.setOwner(owner);

        assertEquals(hub1.hashCode(),hub2.hashCode());
    }


    @Test
    void testGetSetOwner() {
        Player newOwner = mock(Player.class);
        hub.setOwner(newOwner);
        assertSame(newOwner, hub.getOwner());
    }



    @ParameterizedTest
    @ValueSource(strings = {"ESA", "NASA", "ISRO", "JAXA", "Roskosmos", "China Manned Space Agency", "AAU"})
    void setHubImage(String agency) {
        assertDoesNotThrow(() -> hub.setHubImage(agency));
    }
}