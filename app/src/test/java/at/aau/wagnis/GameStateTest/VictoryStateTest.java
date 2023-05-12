package at.aau.wagnis.GameStateTest;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;
import at.aau.wagnis.VictoryScreen;
import at.aau.wagnis.gamestate.MoveTroopsState;
import at.aau.wagnis.gamestate.VictoryState;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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
