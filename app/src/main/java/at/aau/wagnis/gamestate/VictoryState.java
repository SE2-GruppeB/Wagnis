package at.aau.wagnis.gamestate;

import android.content.Context;
import android.content.Intent;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;
import at.aau.wagnis.VictoryScreen;

import java.util.ArrayList;

/**
 * Die ViktoryState Klasse soll den Zustand repräsentieren, wenn ein Spieler alle Hubs erobert hat.
 */
public class VictoryState extends GameLogicState {
    private Player winner;

    public VictoryState(Player winner) {
        this.winner = winner;
    }

    public Player getWinner() {
        return winner;
    }

    public void toVictoryScreen(Context context) {
        Intent intent = new Intent(context, VictoryScreen.class);
        //intent.putExtra("player", this.winner);
        context.startActivity(intent);
    }
}