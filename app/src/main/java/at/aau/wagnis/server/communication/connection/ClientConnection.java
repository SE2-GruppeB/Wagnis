package at.aau.wagnis.server.communication.connection;

import androidx.annotation.NonNull;

import at.aau.wagnis.server.communication.command.ClientCommand;

public interface ClientConnection {

    /**
     * Set the bus that this connection is associated with, and the id of the connection on the bus.
     */
    void setClientConnectionBus(@NonNull ClientConnectionBus bus, int clientId);

    /**
     * Send a command to the connected client.
     *
     * @param command The command to send.
     */
    void send(@NonNull ClientCommand command);

    /**
     * Close the underlying connection
     */
    void close();
}
