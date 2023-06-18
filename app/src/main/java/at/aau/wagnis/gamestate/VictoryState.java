package at.aau.wagnis.gamestate;

import android.content.Context;
import android.content.Intent;

import at.aau.wagnis.Player;
import at.aau.wagnis.VictoryScreen;

/**
 * Die VictoryState-Klasse repräsentiert den Zustand, wenn ein Spieler alle Hubs erobert hat.
 */
public class VictoryState extends GameLogicState {
    private Player winner;

    /**
     * Konstruktor für VictoryState.
     *
     * @param winner Spieler, welcher das Spiel gewonnen hat.
     */
    public VictoryState(Player winner) {
        this.winner = winner;
    }

    /**
     * Gibt den Gewinner des Spiels zurück.
     *
     * @return Der Gewinner des Spiels.
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Öffnet das VictoryScreen-Activity, um den Gewinner anzuzeigen.
     *
     * @param context Der Kontext der Aufrufenden Aktivität.
     */
    public void toVictoryScreen(Context context) {
        Intent intent = new Intent(context, VictoryScreen.class);
        context.startActivity(intent);
    }
}
