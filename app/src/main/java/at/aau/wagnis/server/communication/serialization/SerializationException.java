package at.aau.wagnis.server.communication.serialization;

/**
 * Indicates an error during (de-)serialization
 */
public class SerializationException extends Exception {

    public SerializationException(String message) {
        super(message);
    }
}
