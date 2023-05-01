package at.aau.wagnis.application;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.function.Function;

import at.aau.wagnis.client.GameClient;
import at.aau.wagnis.server.communication.connection.NetworkServerConnection;

public class NetworkGameClientFactory {

    private final Function<Runnable, Thread> threadFactory;
    private final SocketFactory socketFactory;
    private final NetworkServerConnectionFactory serverConnectionFactory;

    public NetworkGameClientFactory(
            @NonNull Function<Runnable, Thread> threadFactory,
            @NonNull SocketFactory socketFactory,
            @NonNull NetworkServerConnectionFactory serverConnectionFactory
    ) {
        this.threadFactory = Objects.requireNonNull(threadFactory);
        this.socketFactory = Objects.requireNonNull(socketFactory);
        this.serverConnectionFactory = Objects.requireNonNull(serverConnectionFactory);
    }

    @NonNull
    public GameClient createGameClient(@NonNull String address, int port) throws IOException {
        Socket socket = null;

        try {
            socket = socketFactory.create(address, port);
            NetworkServerConnection serverConnection = serverConnectionFactory.fromSocket(socket, threadFactory);

            GameClient gameClient = new GameClient();
            gameClient.setServerConnection(serverConnection);
            serverConnection.start();

            return gameClient;
        } catch (IOException e) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException closeException) {
                    e.addSuppressed(closeException);
                }
            }
            throw e;
        }
    }

    public interface SocketFactory {
        @NonNull
        Socket create(@NonNull String address, int port) throws IOException;
    }

    public interface NetworkServerConnectionFactory {
        @NonNull
        NetworkServerConnection fromSocket(@NonNull Socket socket, @NonNull Function<Runnable, Thread> threadFactory) throws IOException;
    }
}
