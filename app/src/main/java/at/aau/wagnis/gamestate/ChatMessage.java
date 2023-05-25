package at.aau.wagnis.gamestate;

public class ChatMessage {

    private final int clientId;
    private final String message;

    public ChatMessage(int clientId, String message) {
        this.clientId = clientId;
        this.message = message;
    }

    public int getClientId() {
        return clientId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "[" +
                + clientId +
                "] "
                + message;
    }
}
