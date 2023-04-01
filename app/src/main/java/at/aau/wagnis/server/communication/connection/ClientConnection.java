package at.aau.wagnis.server.communication.connection;

import at.aau.wagnis.server.communication.command.ClientCommand;

public interface ClientConnection {

    /**
     * Send a command to the connected client.
     *
     * @param command The command to send.
     */
    void send(ClientCommand command);

    /**
     * Close the underlying connection
     */
    void close();
}
