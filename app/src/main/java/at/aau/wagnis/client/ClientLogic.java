package at.aau.wagnis.client;

import androidx.annotation.NonNull;

import at.aau.wagnis.gamestate.GameLogicState;
import at.aau.wagnis.gamestate.GameState;

public interface ClientLogic {

    void updateGameLogicState(@NonNull GameLogicState gameLogicState);
}
