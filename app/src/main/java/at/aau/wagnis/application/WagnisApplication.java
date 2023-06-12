package at.aau.wagnis.application;

import android.app.Application;

import androidx.annotation.NonNull;

import java.net.ServerSocket;
import java.net.Socket;

import at.aau.wagnis.gamestate.LobbyState;
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
                    LobbyState::new
            )
    );

    @NonNull
    public GameManager getGameManager() {
        return gameManager;
    }
}
