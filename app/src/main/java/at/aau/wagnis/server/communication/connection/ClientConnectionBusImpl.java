package at.aau.wagnis.server.communication.connection;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

import at.aau.wagnis.server.communication.command.ClientCommand;
import at.aau.wagnis.server.communication.command.ClientOriginatedServerCommand;
import at.aau.wagnis.server.communication.command.HandleConnectionBusClosedCommand;
import at.aau.wagnis.server.communication.command.HandleConnectionClosedCommand;
import at.aau.wagnis.server.communication.command.ServerCommand;

public class ClientConnectionBusImpl implements ClientConnectionBus {

    private final LinkedBlockingQueue<ServerCommand> serverCommandQueue = new LinkedBlockingQueue<>();

    private final Map<Integer, ClientConnection> connectionsById = Collections.synchronizedMap(new HashMap<>());
    private boolean isClosed = false;
    private int nextClientId = 0;

    @Override
    public void registerConnection(@NonNull ClientConnection connection) throws IllegalStateException {
        Objects.requireNonNull(connection);

        synchronized (connectionsById) {
            if (isClosed) {
                throw new IllegalStateException("Bus is closed");
            }

            int clientId = nextClientId++;
            connection.setClientConnectionBus(this, clientId);
            this.connectionsById.put(clientId, connection);
        }
    }

    @Override
    public void close() {
        synchronized (connectionsById) {
            if (isClosed) {
                return;
            }
            isClosed = true;

            this.connectionsById.values().forEach(ClientConnection::close);
            this.connectionsById.clear();
        }
        serverCommandQueue.add(new HandleConnectionBusClosedCommand());
    }

    @Override
    public void broadcastCommand(@NonNull ClientCommand command) {
        Objects.requireNonNull(command);

        synchronized (connectionsById) {
            this.connectionsById.values().forEach(connection -> connection.send(command));
        }
    }

    @Override
    public void handleClosedConnection(int clientId) throws IllegalStateException {
        synchronized (connectionsById) {
            if (isClosed) {
                return;
            }

            if (connectionsById.remove(clientId) != null) {
                this.serverCommandQueue.add(new HandleConnectionClosedCommand(clientId));
            } else {
                throw new IllegalStateException(String.format("No connection with id %d found", clientId));
            }
        }
    }

    @Override
    public void reportReceivedCommand(@NonNull ClientOriginatedServerCommand command) {
        this.serverCommandQueue.add(Objects.requireNonNull(command));
    }

    @NonNull
    @Override
    public ServerCommand getNextCommand() throws InterruptedException {
        return serverCommandQueue.take();
    }

    public boolean hasNextCommand() {
        return serverCommandQueue.peek() != null;
    }
}
