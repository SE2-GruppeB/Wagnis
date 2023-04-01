package at.aau.wagnis.server.communication.connection;

import at.aau.wagnis.server.communication.command.ServerCommand;

public interface ServerConnection {

    /**
     * Send a command to the connected server.
     *
     * @param command The command to send.
     */
    void send(ServerCommand command);

    /**
     * Close the underlying connection
     */
    void close();
}
