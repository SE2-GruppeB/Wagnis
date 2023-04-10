package at.aau.wagnis.server.communication.serialization;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Wraps a {@link DeserializingReader} and creates a notification when an object is received.
 * Automatically closes itself and calls an error callback in case of a communication error.
 */
public class ActiveDeserializingReader<T> {

    private final DeserializingReader<T> reader;
    private final Function<Runnable, Thread> threadFactory;

    private Thread thread = null;
    private boolean closed = false;
    private Consumer<T> receiveCallback = null;
    private Runnable errorCallback = null;

    public ActiveDeserializingReader(
            @NonNull DeserializingReader<T> reader,
            @NonNull Function<Runnable, Thread> threadFactory
    ) {
        this.reader = Objects.requireNonNull(reader);
        this.threadFactory = Objects.requireNonNull(threadFactory);
    }

    /**
     * Initialize this reader.
     *
     * @param receiveCallback A callback that runs for every received object.
     * @param errorCallback A callback that is run on error, indicating that the reader is about to close.
     * @throws IllegalStateException In case the reader has already been started or is closed.
     */
    public synchronized void start(
            @NonNull Consumer<T> receiveCallback,
            @NonNull Runnable errorCallback
    ) throws IllegalStateException {
        Objects.requireNonNull(receiveCallback);
        Objects.requireNonNull(errorCallback);

        if (closed) {
            throw new IllegalStateException("Reader has been closed");
        } else if (thread != null) {
            throw new IllegalStateException("Reader has already been started");
        }

        this.receiveCallback = receiveCallback;
        this.errorCallback = errorCallback;

        thread = threadFactory.apply(() -> {
            while(!Thread.interrupted()) {
               this.readInternal();
            }
        });
        thread.start();
    }

    /**
     * Close this reader, also closing the wrapped reader.
     */
    public synchronized void close() {
        if (closed) {
            return;
        }
        closed = true;

        if (thread != null) {
            thread.interrupt();
        }

        try {
            reader.close();
        } catch (IOException e) {
            // Nothing to do here
        }
    }

    @VisibleForTesting
    void readInternal() {
        try {
            T readResult = reader.readNext();
            receiveCallback.accept(readResult);
        } catch (IOException | SerializationException ex) {
            this.errorCallback.run();
            this.close();
        }
    }
}
