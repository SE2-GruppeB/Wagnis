package at.aau.wagnis.gamestate;

import java.util.Objects;

/**
 * Diese Klasse repräsentiert eine Chat-Funktion.
 */
public class ChatMessage {

    private final int clientId;
    // Eine eindeutige ID, die den Client identifiziert
    private final String message;
    // Die Nachricht welche gesendet wurde

    /**
     * Erzeugt eine neue Chat-Nachricht mit dem angegebenen Client und der Nachricht.
     *
     * @param clientId Die ID des Clients, der die Nachricht gesendet hat.
     * @param message  Die gesendete Nachricht.
     */
    public ChatMessage(int clientId, String message) {
        this.clientId = clientId;
        this.message = message;
    }

    /**
     * Gibt die ID des Clients zurück, der die Nachricht gesendet hat.
     *
     * @return Die ID des Clients.
     */
    public int getClientId() {
        return clientId;
    }

    /**
     * Gibt die gesendete Nachricht zurück.
     *
     * @return Die Nachricht.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gibt eine formatierte String-Repräsentation der Chat-Nachricht zurück.
     *
     * @return Eine formatierte String-Repräsentation der Chat-Nachricht.
     */
    @Override
    public String toString() {
        return "[" +
                +clientId +
                "] "
                + message;
    }

    /**
     * Überprüft, ob das übergebene Objekt mit dieser Chat-Nachricht identisch ist.
     *
     * @param o Das Objekt, das mit dieser Chat-Nachricht verglichen werden soll.
     * @return true, wenn das Objekt identisch ist, andernfalls false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return clientId == that.clientId && message.equals(that.message);
    }

    /**
     * Berechnet den Hash-Code für diese Chat-Nachricht.
     *
     * @return Der berechnete Hash-Code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(clientId, message);
    }
}
