package at.aau.wagnis.GameStateTest; // Ein magischer Ort, an dem Testfälle tanzen und die Welt des Spiels erzittern lässt.

import org.junit.jupiter.api.BeforeEach; // Bevor der Spaß beginnt, müssen wir uns vorbereiten - wie ein Jongleur, der seine Bälle sortiert.
import org.junit.jupiter.api.Test; // Jetzt wird's ernst! Es ist Zeit für den großen Auftritt der Tests - wie eine Zirkusvorstellung für Code.

import static org.junit.jupiter.api.Assertions.*; // Hier gibt es keine Kompromisse! Wir sind bereit, Fehler aufzudecken und sie in die Schranken zu weisen.
import static org.mockito.Mockito.*; // Wir brauchen unsere treuen Mocks, die wie Marionetten in unseren Händen tanzen.

import java.util.ArrayList; // Eine Schatztruhe voller Listen, die darauf warten, geöffnet zu werden.
import java.util.List; // Die Mitglieder des Listenklubs - eine Gruppe von Objekten, die gemeinsam auftreten.

import at.aau.wagnis.Adjacency; // Ein geheimer Pfad, der zwei Hubs miteinander verbindet.
import at.aau.wagnis.Hub; // Die Hauptquartiere, in denen unsere Abenteuer beginnen und enden.
import at.aau.wagnis.gamestate.AttackGameState; // Der Zustand des Spiels, in dem wir gnadenlos angreifen.
import at.aau.wagnis.gamestate.ChooseAttackGameState; // Die Wahl des Angriffs - eine entscheidende Entscheidung im Spiel.
import at.aau.wagnis.gamestate.GameData; // Unsere Schatzkammer, die Daten des Spiels speichert.
import at.aau.wagnis.server.GameServer; // Der Meister des Spielservers, der die Fäden in der Hand hält.

public class ChooseAttackGameStateTest {

    private ChooseAttackGameState gameState; // Unser tapferer Held, der den Angriffsmodus beherrscht.
    private GameServer gameServer; // Der große GameServer, der über das Spiel wacht.
    private GameData gameData; // Unsere verborgenen Geheimnisse des Spiels.

    @BeforeEach
    public void prepareForBattle() { // Bereite dich auf die große Schlacht vor, wie ein wütender Marshmallow!
        gameState = new ChooseAttackGameState(); // Unser Held ist bereit, die Wahl des Angriffs zu meistern.
        gameServer = mock(GameServer.class); // Unser treuer Begleiter, ein getarnter GameServer.
        gameData = mock(GameData.class); // Die verborgene Wahrheit des Spiels.
        when(gameServer.getGameData()).thenReturn(gameData); // Der GameServer enthüllt seine Daten dem mutigen Helden.
        gameState.setGameServer(gameServer); // Der GameServer steht unserem tapferen Helden zur Seite.
    }

    @Test
    public void testExplosiveAttack() { // Feuerwerk! Eine farbenfrohe Attacke, die die Einhörner vor Neid erblassen lässt!
        Hub sourceHub = new Hub(1); // Der Ausgangshub, von dem der Angriff startet.
        Hub targetHub = new Hub(2); // Der Zielhub, der unser Begehr geworden ist.
        Adjacency adjacency = new Adjacency(sourceHub, targetHub); // Der geheime Pfad zwischen den Hubs.
        List<Adjacency> adjacencies = new ArrayList<>(); // Eine Liste von geheimen Pfaden.
        List<Hub> hubs = new ArrayList<>(); // Eine Gruppe von Hubs, die zusammenhalten.
        hubs.add(sourceHub); // Unser Held schließt sich den Hubs an.
        hubs.add(targetHub); // Der begehrte Zielhub wird Teil der Gemeinschaft.
        adjacencies.add(adjacency); // Der geheime Pfad wird zur Sammlung hinzugefügt.
        when(gameData.getAdjacencies()).thenReturn(adjacencies); // Das Spiel gibt seine geheimen Pfade preis.
        when(gameData.getHubs()).thenReturn(hubs); // Die Hubs öffnen ihre Tore und begrüßen uns.

        gameState.chooseAttack(1, 1, 2); // Unser Held wählt die explosive Attacke!

        // Überprüfen wir, ob der Spielzustand aktualisiert wurde - wie eine Nachricht an die Zauberkugel des Schicksals!
        verify(gameServer).setGameLogicState(any(AttackGameState.class));
    }

    @Test
    public void testInvalidAttack() { // Achtung, Angriff fehlgeschlagen! Bitte versuchen Sie es erneut.
        Hub sourceHub = new Hub(1); // Der Ausgangshub, der unsere Hoffnungen zerstört hat.
        Hub targetHub = new Hub(2); // Der Zielhub, der sich vor unserem Angriff versteckt.
        List<Hub> hubs = new ArrayList<>(); // Eine Gruppe von Hubs, die uns den Weg versperren.
        hubs.add(sourceHub); // Unser Held stellt sich mutig dem Ausgangshub.
        hubs.add(targetHub); // Der gemeine Zielhub versteckt sich in der Dunkelheit.
        when(gameData.getHubs()).thenReturn(hubs); // Die Hubs lüften ihr Geheimnis.

        // Versuchen wir, von nicht angrenzenden Hubs anzugreifen - wie ein Elefant, der versucht, durch eine Mauseloch zu passen!
        assertThrows(IllegalArgumentException.class, () -> gameState.chooseAttack(1, 1, 3));
    }

    @Test
    public void testSourceHubNotFound() { // Achtung, Ausgangshub nicht gefunden! Wir haben eine Leere in der Matrix!
        Hub targetHub = new Hub(2); // Der Zielhub, der uns anlacht und verspottet.
        List<Hub> hubs = new ArrayList<>(); // Eine mysteriöse Gruppe von Hubs, ohne Ausgangshub.
        hubs.add(targetHub); // Der gemeine Zielhub genießt seine Einsamkeit.
        when(gameData.getHubs()).thenReturn(hubs); // Die Hubs teilen uns ihre traurige Geschichte mit.

        // Versuchen wir, von einem nicht existierenden Ausgangshub anzugreifen - wie ein Hamster, der versucht, einen Drachen zu zähmen!
        assertThrows(IllegalArgumentException.class, () -> gameState.chooseAttack(1, 1, 2));
    }

    @Test
    public void testTargetHubNotFound() { // Achtung, Zielhub nicht gefunden! Ein trauriges Schicksal für unseren Helden.
        Hub sourceHub = new Hub(1); // Der tapfere Ausgangshub, der sein Ziel verloren hat.
        List<Hub> hubs = new ArrayList<>(); // Eine Gruppe von Hubs, in der das Ziel verschwunden ist.
        hubs.add(sourceHub); // Unser Held trauert um sein verlorenes Ziel.
        when(gameData.getHubs()).thenReturn(hubs); // Die Hubs flüstern uns ihre Trauer zu.

        // Versuchen wir, einen nicht existierenden Zielhub anzugreifen - wie ein Eiswürfel, der die Sonne besiegen möchte!
        assertThrows(IllegalArgumentException.class, () -> gameState.chooseAttack(1, 1, 2));
    }
}
