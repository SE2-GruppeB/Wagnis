package at.aau.wagnis.client;

import androidx.annotation.NonNull;

import at.aau.wagnis.gamestate.GameData;

public interface ClientLogic {

    void updateGameData(@NonNull GameData gameData);
}
