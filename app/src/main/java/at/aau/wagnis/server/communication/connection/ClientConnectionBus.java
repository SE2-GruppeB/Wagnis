package at.aau.wagnis.server.communication.connection;

import androidx.annotation.NonNull;

import at.aau.wagnis.server.communication.command.ClientCommand;
import at.aau.wagnis.server.communication.command.ClientOriginatedServerCommand;
import at.aau.wagnis.server.communication.command.ServerCommand;

public interface ClientConnectionBus {

    /**
     * Add a new connection to the bus, will call {@link ClientConnection#setClientConnectionBus}
     *
     * @throws IllegalStateException if the bus is closed
     */
    void registerConnection(@NonNull ClientConnection connection) throws IllegalStateException;

    /**
     * Close the bus, in turn closing all managed ClientConnections
     */
    void close();

    /**
     * Send a command to all connected clients
     */
    void broadcastCommand(@NonNull ClientCommand command);

    /**
     * Read the next command on the bus, blocks until a command is available
     *
     * @throws InterruptedException -
     */
    @NonNull
    ServerCommand getNextCommand() throws InterruptedException;

    /**
     * Notify the bus that a command from a client has been received
     */
    void reportReceivedCommand(@NonNull ClientOriginatedServerCommand command);

    /**
     * Notify the bus that a ClientConnection is closing
     *
     * @throws IllegalStateException if there is no connection with the given id
     */
    void handleClosedConnection(int clientId) throws IllegalStateException;
}
