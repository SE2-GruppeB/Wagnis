package at.aau.wagnis.server.communication.connection;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import at.aau.wagnis.server.communication.command.ClientCommand;
import at.aau.wagnis.server.communication.command.ClientOriginatedServerCommand;
import at.aau.wagnis.server.communication.serialization.ActiveDeserializingReader;
import at.aau.wagnis.server.communication.serialization.ActiveSerializingWriter;

public class NetworkServerConnection implements ServerConnection {

    private final ActiveDeserializingReader<ClientCommand> input;
    private final ActiveSerializingWriter<ClientOriginatedServerCommand> output;

    private final AtomicReference<Consumer<ClientCommand>> commandConsumerReference = new AtomicReference<>(null);

    public NetworkServerConnection(
            @NonNull ActiveDeserializingReader<ClientCommand> input,
            @NonNull ActiveSerializingWriter<ClientOriginatedServerCommand> output
    ) {
        this.input = Objects.requireNonNull(input);
        this.output = Objects.requireNonNull(output);
    }

    public static NetworkServerConnection fromSocket(
            @NonNull Socket socket,
            @NonNull Function<Runnable, Thread> threadFactory
    ) throws IOException {
        return new NetworkServerConnection(
                ActiveDeserializingReader.fromStream(ClientCommand.class, socket.getInputStream(), threadFactory),
                ActiveSerializingWriter.fromStream(socket.getOutputStream(), threadFactory)
        );
    }

    /**
     * Initialize the connection
     * @throws IllegalStateException If the connection has been started already, has been closed or
     *                               if no command consumer has been set yet.
     */
    public void start() {
        if (commandConsumerReference.get() == null) {
            throw new IllegalStateException("Command consumer has not been set");
        }

        synchronized (this) {
            input.start(this::onReceive, this::onError);
            output.start(this::onError);
        }
    }

    public void setCommandConsumer(@NonNull Consumer<ClientCommand> commandConsumer) {
        this.commandConsumerReference.set(Objects.requireNonNull(commandConsumer));
    }

    protected void onError() {
        this.close();
    }

    protected void onReceive(@NonNull ClientCommand command) {
        this.commandConsumerReference.get().accept(command);
    }

    @Override
    public void send(@NonNull ClientOriginatedServerCommand command) {
        output.write(command);
    }

    @Override
    public void close() {
        // TODO client logic should react on close

        synchronized (this) {
            input.close();
            output.close();
        }
    }
}
