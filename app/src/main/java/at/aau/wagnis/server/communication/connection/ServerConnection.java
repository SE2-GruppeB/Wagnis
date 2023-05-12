package at.aau.wagnis.server.communication.connection;

import androidx.annotation.NonNull;

import java.util.function.Consumer;

import at.aau.wagnis.server.communication.command.ClientCommand;
import at.aau.wagnis.server.communication.command.ClientOriginatedServerCommand;

public interface ServerConnection {

    /**
     * Send a command to the connected server.
     *
     * @param command The command to send.
     */
    void send(@NonNull ClientOriginatedServerCommand command);

    /**
     * @param commandConsumer A callback to run when a command is received.
     */
    void setCommandConsumer(@NonNull Consumer<ClientCommand> commandConsumer);

    /**
     * Close the underlying connection
     */
    void close();
}
