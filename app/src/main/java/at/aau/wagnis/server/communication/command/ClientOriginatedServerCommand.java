package at.aau.wagnis.server.communication.command;

public interface ClientOriginatedServerCommand extends ServerCommand {

    int getClientId();

    void setClientId(int clientId);
}
