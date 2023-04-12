package at.aau.wagnis.server.communication.command;

public class HandleConnectionClosedCommand implements ServerCommand {

    private final int clientId;

    public HandleConnectionClosedCommand(int clientId) {
        this.clientId = clientId;
    }

    public int getClientId() {
        return clientId;
    }

    @Override
    public void execute() {
        // TODO logic for closed connections
    }
}
