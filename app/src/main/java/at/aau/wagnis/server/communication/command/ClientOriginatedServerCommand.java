package at.aau.wagnis.server.communication.command;

public interface ClientOriginatedServerCommand extends ServerCommand {

    void setClientId(int clientId);

    int getClientId();
}
