package at.aau.wagnis.application;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import at.aau.wagnis.gamestate.GameLogicState;
import at.aau.wagnis.server.GameServer;
import at.aau.wagnis.server.communication.connection.ClientConnectionBus;
import at.aau.wagnis.server.communication.connection.ClientConnectionBusImpl;
import at.aau.wagnis.server.communication.connection.ClientConnectionListener;
import at.aau.wagnis.server.communication.connection.NetworkClientConnection;

public class GameServerFactory {

    private final Function<Runnable, Thread> threadFactory;
    private final ServerSocketFactory serverSocketFactory;
    private final Supplier<GameLogicState> initialGameLogicStateSupplier;

    public GameServerFactory(
            @NonNull Function<Runnable, Thread> threadFactory,
            @NonNull ServerSocketFactory serverSocketFactory,
            @NonNull Supplier<GameLogicState> initialGameLogicStateSupplier
    ) {
        this.threadFactory = Objects.requireNonNull(threadFactory);
        this.serverSocketFactory = Objects.requireNonNull(serverSocketFactory);
        this.initialGameLogicStateSupplier = Objects.requireNonNull(initialGameLogicStateSupplier);
    }

    @NonNull
    public GameServer createGameServer(int port) throws IOException {
        ServerSocket serverSocket = serverSocketFactory.create(port);

        ClientConnectionBus bus = new ClientConnectionBusImpl();

        ClientConnectionListener clientConnectionListener = new ClientConnectionListener(
                serverSocket,
                bus,
                NetworkClientConnection::fromSocket,
                threadFactory
        );
        clientConnectionListener.start();

        GameServer server = new GameServer(
                bus,
                clientConnectionListener,
                initialGameLogicStateSupplier.get()
        );

        threadFactory.apply(server).start();

        return server;
    }

    interface ServerSocketFactory {
        @NonNull
        ServerSocket create(int listenPort) throws IOException;
    }
}
