package at.aau.wagnis.server.communication.connection;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.function.Function;

/**
 * Registers new client connections from a ServerSocket on a {@link ClientConnectionBus}
 */
public class ClientConnectionListener {

    private final ServerSocket serverSocket;
    private final ClientConnectionBus bus;
    private final ConnectionFactory connectionFactory;
    private final Function<Runnable, Thread> threadFactory;

    private Thread listenerThread = null;
    private boolean closed = false;

    public ClientConnectionListener(
            @NonNull ServerSocket serverSocket,
            @NonNull ClientConnectionBus bus,
            @NonNull ConnectionFactory connectionFactory,
            @NonNull Function<Runnable, Thread> threadFactory) {
        this.serverSocket = Objects.requireNonNull(serverSocket);
        this.bus = Objects.requireNonNull(bus);
        this.connectionFactory = Objects.requireNonNull(connectionFactory);
        this.threadFactory = Objects.requireNonNull(threadFactory);
    }

    /**
     * Start accepting new clients
     *
     * @throws IllegalStateException If the listener has already been started or is closed
     */
    public synchronized void start() {
        if (closed) {
            throw new IllegalStateException("Listener is closed");
        } else if (this.listenerThread != null) {
            throw new IllegalStateException("Listener has already been started");
        }

        this.listenerThread = threadFactory.apply(() -> {
            while (!Thread.interrupted()) {
                this.handleNextClient();
            }
        });
        this.listenerThread.start();
    }

    /**
     * Stop listening and close the wrapped ServerSocket
     */
    public synchronized void close() {
        if (closed) {
            return;
        }
        closed = true;

        if (listenerThread != null) {
            listenerThread.interrupt();
        }

        // TODO server logic should react to a closing listener

        try {
            serverSocket.close();
        } catch (IOException e) {
            // Nothing to do here
        }
    }

    @VisibleForTesting
    protected void handleNextClient() {
        Socket socket = null;

        try {
            socket = serverSocket.accept();
            NetworkClientConnection connection = connectionFactory.apply(socket, threadFactory);
            bus.registerConnection(connection);
            connection.start();
        } catch (IOException ex) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // Nothing to do here
                }
            }

            this.close();
        }
    }

    public interface ConnectionFactory {
        @NonNull
        NetworkClientConnection apply(@NonNull Socket socket, @NonNull Function<Runnable, Thread> threadFactory) throws IOException;
    }
}
