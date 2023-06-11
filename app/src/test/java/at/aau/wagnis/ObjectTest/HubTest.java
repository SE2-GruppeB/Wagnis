package at.aau.wagnis.ObjectTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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