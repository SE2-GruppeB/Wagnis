package at.aau.wagnis.server.communication.serialization;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

/**
 * Wraps a {@link SerializingWriter} and asynchronously writes objects to it.
 * Automatically closes itself and calls an error callback in case of a communication error.
 */
public class ActiveSerializingWriter<T> {

    private final SerializingWriter<T> writer;
    private final Function<Runnable, Thread> threadFactory;

    private final LinkedBlockingQueue<T> objectQueue = new LinkedBlockingQueue<>();

    private Thread thread = null;
    private boolean closed = false;
    private Runnable errorCallback = null;

    public ActiveSerializingWriter(
            @NonNull SerializingWriter<T> writer,
            @NonNull Function<Runnable, Thread> threadFactory
    ) {
        this.writer = Objects.requireNonNull(writer);
        this.threadFactory = Objects.requireNonNull(threadFactory);
    }

    @NonNull
    public static <T> ActiveSerializingWriter<T> fromStream(
            @NonNull OutputStream outputStream,
            @NonNull Function<Runnable, Thread> threadFactory
    ) {
        Objects.requireNonNull(outputStream);
        Objects.requireNonNull(threadFactory);

        return new ActiveSerializingWriter<>(
                new SerializingWriter<>(new DataOutputStream(outputStream), new SerializerLoader()),
                threadFactory
        );
    }

    /**
     * Initialize this writer.
     *
     * @param errorCallback A callback that is run on error, indicating that the writer is about to close.
     * @throws IllegalStateException If the writer has already been started or is closed.
     */
    public synchronized void start(@NonNull Runnable errorCallback) throws IllegalStateException {
        Objects.requireNonNull(errorCallback);

        if (closed) {
            throw new IllegalStateException("Writer has been closed");
        } else if (thread != null) {
            throw new IllegalStateException("Writer has already been started");
        }

        this.errorCallback = errorCallback;

        thread = threadFactory.apply(() -> {
            while (!Thread.interrupted()) {
                this.writeInternal();
            }
        });
        thread.start();
    }

    /**
     * Close this writer, also closing the wrapped writer.
     * Objects not delivered yet will be dropped.
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
            writer.close();
        } catch (IOException e) {
            // Nothing to do here
        }

        objectQueue.clear();
    }

    /**
     * Enqueue an object to be written asynchronously
     *
     * @param obj The object to enqueue
     * @throws IllegalStateException If the writer has not been started yet, has been closed, or in case of queue overflow
     */
    public synchronized void write(@NonNull T obj) throws IllegalStateException {
        Objects.requireNonNull(obj);

        if (closed) {
            throw new IllegalStateException("Writer has been closed");
        } else if (thread == null) {
            throw new IllegalStateException("Writer has not been started");
        }

        objectQueue.add(obj);
    }

    @VisibleForTesting
    void writeInternal() {
        try {
            writer.write(objectQueue.take());
            writer.flush();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException | SerializationException ex) {
            this.errorCallback.run();
            this.close();
        }
    }
}
