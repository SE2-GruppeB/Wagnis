package at.aau.wagnis.GameStateTest;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;
import at.aau.wagnis.gamestate.StartGameState;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StartGameStateTest {

    private StartGameState startGameState;
    private ArrayList<Player> players;
    private ArrayList<Hub> hubs;
    final int num_players = 6;
    final int num_hubs = 6;

    @BeforeEach
    public void setUp() {
        hubs = new ArrayList<>();
        players = new ArrayList<>();
        startGameState = new StartGameState(hubs, players);
    }

    @Test
    public void testAssignCountries() {

        for (int i = 1; i <= num_players; i++) {
            Player player = new Player(i);
            players.add(player);
        }

        for (int i = 1; i <= num_hubs; i++) {
            Hub hub = new Hub(Mockito.mock(Button.class));
            hubs.add(hub);
        }

        StartGameState startGameState = new StartGameState(hubs, players);
        startGameState.assignCountries();

        for (Hub hub : hubs) {
            assertNotNull(hub.getOwner());
        }
    }

    @Test
    public void testAssignTroopsToHubs() {

        for (int i = 1; i <= 6; i++) {
            Hub hub = new Hub(mock(Button.class));
            hubs.add(hub);
            Player player = new Player(i);
            players.add(player);
            player.addHub(hub);
        }

        StartGameState startGameState = new StartGameState(hubs, players);

        startGameState.assignTroopsToHubs();

        for (Hub hub : hubs) {
            assertTrue(hub.getAmountTroops() > 0);
        }
    }

}