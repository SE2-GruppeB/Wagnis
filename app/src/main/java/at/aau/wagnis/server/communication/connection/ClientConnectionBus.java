package at.aau.wagnis.server.communication.connection;

import at.aau.wagnis.server.communication.command.ClientCommand;
import at.aau.wagnis.server.communication.command.ServerCommand;

public interface ClientConnectionBus {

    /**
     * Send a command to all connected clients
     */
    void broadcastCommand(ClientCommand command);

    /**
     * Read the next command on the bus, blocks until a command is available
     *
     * @throws InterruptedException -
     */
    ServerCommand getNextCommand() throws InterruptedException;
}
