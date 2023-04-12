package at.aau.wagnis.server.communication.connection;

import androidx.annotation.NonNull;

import at.aau.wagnis.server.communication.command.ClientOriginatedServerCommand;

public interface ServerConnection {

    /**
     * Send a command to the connected server.
     *
     * @param command The command to send.
     */
    void send(@NonNull ClientOriginatedServerCommand command);

    /**
     * Close the underlying connection
     */
    void close();
}
