package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import at.aau.wagnis.client.ClientLogic;

/**
 * A command to be run by the client
 */
public interface ClientCommand {

    void execute(@NonNull ClientLogic clientLogic);
}