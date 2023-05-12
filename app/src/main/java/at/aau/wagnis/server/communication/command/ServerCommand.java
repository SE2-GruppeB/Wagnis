package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import at.aau.wagnis.gamestate.GameLogicState;

/**
 * A command to be run by the server
 */
public interface ServerCommand {

    void execute(@NonNull GameLogicState gameLogicState);
}
