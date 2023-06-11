package at.aau.wagnis.client;

import androidx.annotation.NonNull;

import at.aau.wagnis.gamestate.GameData;

public interface ClientLogic {

    void updateGameState(@NonNull GameData gameData);
}
