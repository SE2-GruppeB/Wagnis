package at.aau.wagnis.GameStateTest;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;
import at.aau.wagnis.gamestate.MoveTroopsState;
import at.aau.wagnis.gamestate.VictoryState;


public class VictoryStateTest {

    private Hub sourceHub;
    private Hub targetHub;
    private MoveTroopsState moveTroopsState;

    @Mock
    private Context mockedContext;
    @Mock
    private Intent mockedIntent;
    @Mock
    private Player mockedWinner;


    @Test
    public void testGetWinner() {
        VictoryState state = new VictoryState(mockedWinner);
        assertEquals(mockedWinner, state.getWinner());
    }


}
