package at.aau.wagnis.server.communication.connection;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import at.aau.wagnis.server.communication.command.ClientCommand;
import at.aau.wagnis.server.communication.command.ClientOriginatedServerCommand;
import at.aau.wagnis.server.communication.serialization.ActiveDeserializingReader;
import at.aau.wagnis.server.communication.serialization.ActiveSerializingWriter;

public class NetworkServerConnection implements ServerConnection {

    private final ActiveDeserializingReader<ClientCommand> input;
    private final ActiveSerializingWriter<ClientOriginatedServerCommand> output;
    private final Consumer<ClientCommand> commandConsumer;

    public NetworkServerConnection(
            @NonNull ActiveDeserializingReader<ClientCommand> input,
            @NonNull ActiveSerializingWriter<ClientOriginatedServerCommand> output,
            @NonNull Consumer<ClientCommand> commandConsumer
    ) {
        this.input = Objects.requireNonNull(input);
        this.output = Objects.requireNonNull(output);
        this.commandConsumer = Objects.requireNonNull(commandConsumer);
    }

    @NonNull
    public static NetworkServerConnection fromSocket(
            @NonNull Socket socket,
            @NonNull Function<Runnable, Thread> threadFactory,
            @NonNull Consumer<ClientCommand> commandConsumer
    ) throws IOException {
        return new NetworkServerConnection(
                ActiveDeserializingReader.fromStream(ClientCommand.class, socket.getInputStream(), threadFactory),
                ActiveSerializingWriter.fromStream(socket.getOutputStream(), threadFactory),
                commandConsumer
        );
    }

    /**
     * Initialize the connection
     * @throws IllegalStateException If the connection has been started already or has been closed
     */
    public void start() {
        synchronized (this) {
            input.start(commandConsumer, this::onError);
            output.start(this::onError);
        }
    }

    protected void onError() {
        this.close();
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
