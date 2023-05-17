package at.aau.wagnis.client;

import androidx.annotation.NonNull;

import at.aau.wagnis.gamestate.GameState;

public interface ClientLogic {

    void updateGameState(@NonNull GameState gameState);
}
