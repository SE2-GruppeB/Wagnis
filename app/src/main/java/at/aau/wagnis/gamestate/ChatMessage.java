package at.aau.wagnis.gamestate;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return clientId == that.clientId && message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, message);
    }
}
