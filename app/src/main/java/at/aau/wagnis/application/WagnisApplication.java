package at.aau.wagnis.application;

import android.app.Application;

import androidx.annotation.NonNull;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import at.aau.wagnis.gamestate.StartGameState;
import at.aau.wagnis.server.communication.connection.NetworkServerConnection;

public class WagnisApplication extends Application {

    private final GameManager gameManager = new GameManager(
            new NetworkGameClientFactory(
                    Thread::new,
                    Socket::new,
                    NetworkServerConnection::fromSocket
            ),
            new GameServerFactory(
                    Thread::new,
                    ServerSocket::new,
                    () -> new StartGameState(new ArrayList<>(), new ArrayList<>())
            )
    );

    @NonNull
    public GameManager getGameManager() {
        return gameManager;
    }
}
