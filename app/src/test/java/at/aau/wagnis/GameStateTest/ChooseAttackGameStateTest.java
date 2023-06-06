/**
 * Dies ist eine Testklasse für ChooseAttackGameState.
 */

package at.aau.wagnis.GameStateTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import at.aau.wagnis.Adjacency;
import at.aau.wagnis.Hub;
import at.aau.wagnis.gamestate.AttackGameState;
import at.aau.wagnis.gamestate.ChooseAttackGameState;
import at.aau.wagnis.gamestate.GameData;
import at.aau.wagnis.server.GameServer;

public class ChooseAttackGameStateTest {

    private ChooseAttackGameState gameState;
    private GameServer gameServer;
    private GameData gameData;

    @BeforeEach
    public void setUp() {
        // Initialisierung der Testumgebung
        gameState = new ChooseAttackGameState();
        gameServer = mock(GameServer.class);
        gameData = mock(GameData.class);
        when(gameServer.getGameData()).thenReturn(gameData);
        gameState.setGameServer(gameServer);
    }

    /**
     * Testet, ob `chooseAttack()` einen neuen `AttackGameState` festlegt, indem `gameServer.setGameLogicState()` aufgerufen wird.
     */
    @Test
    public void testChooseAttack_ValidAttack() {
        //Hubs für einen gültigen Angriff
        Hub sourceHub = new Hub(1);
        Hub targetHub = new Hub(2);
        Adjacency adjacency = new Adjacency(sourceHub, targetHub);
        List<Adjacency> adjacencies = new ArrayList<>();
        List<Hub> hubs = new ArrayList<>();
        hubs.add(sourceHub);
        hubs.add(targetHub);
        adjacencies.add(adjacency);
        when(gameData.getAdjacencies()).thenReturn(adjacencies);
        when(gameData.getHubs()).thenReturn(hubs);

        // Durchführung des Angriffs
        gameState.chooseAttack(1, 1, 2);

        // Überprüfen, ob der Spielzustand aktualisiert wurde
        verify(gameServer).setGameLogicState(any(AttackGameState.class));
    }

    /**
     * Testet, ob `chooseAttack()` den richtigen `AttackGameState` festlegt, indem `gameServer.setGameLogicState()` aufgerufen wird.
     */
    @Test
    public void testChooseAttack_InvalidAttack() {
        // Hubs für einen ungültigen Angriff
        Hub sourceHub = new Hub(1);
        Hub targetHub = new Hub(2);
        List<Hub> hubs = new ArrayList<>();
        hubs.add(sourceHub);
        hubs.add(targetHub);
        when(gameData.getHubs()).thenReturn(hubs);

        // Angriff, von nicht benachbarten Hubs
        assertThrows(IllegalArgumentException.class, () -> gameState.chooseAttack(1, 1, 3));
    }

    @Test
    public void testChooseAttack_SourceHubNotFound() {
        // Angriff-Hub nicht gefunden.
        Hub targetHub = new Hub(2);
        List<Hub> hubs = new ArrayList<>();
        hubs.add(targetHub);
        when(gameData.getHubs()).thenReturn(hubs);

        // Angriff über nicht vorhanden Hub
        assertThrows(IllegalArgumentException.class, () -> gameState.chooseAttack(1, 1, 2));
    }

    @Test
    public void testChooseAttack_TargetHubNotFound() {
        // Verteidiger Hub nicht vorhanden
        Hub sourceHub = new Hub(1);
        List<Hub> hubs = new ArrayList<>();
        hubs.add(sourceHub);
        when(gameData.getHubs()).thenReturn(hubs);

        // nicht vorhandenen Vertidiger-Hub angreifen
        assertThrows(IllegalArgumentException.class, () -> gameState.chooseAttack(1, 1, 2));
    }
}
