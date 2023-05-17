package at.aau.wagnis.server.communication.connection;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import at.aau.wagnis.server.communication.command.ClientCommand;
import at.aau.wagnis.server.communication.command.ClientOriginatedServerCommand;
import at.aau.wagnis.server.communication.serialization.ActiveDeserializingReader;
import at.aau.wagnis.server.communication.serialization.ActiveSerializingWriter;

public class NetworkClientConnection implements ClientConnection {
    private final ActiveDeserializingReader<ClientOriginatedServerCommand> input;
    private final ActiveSerializingWriter<ClientCommand> output;

    private final AtomicReference<BusConfiguration> busConfigReference = new AtomicReference<>(null);
    private final AtomicBoolean closeNotificationSent = new AtomicBoolean(false);

    public NetworkClientConnection(
            @NonNull ActiveDeserializingReader<ClientOriginatedServerCommand> input,
            @NonNull ActiveSerializingWriter<ClientCommand> output
    ) {
        this.input = Objects.requireNonNull(input);
        this.output = Objects.requireNonNull(output);
    }

    @NonNull
    public static NetworkClientConnection fromSocket(
            @NonNull Socket socket,
            @NonNull Function<Runnable, Thread> threadFactory
    ) throws IOException {
        return new NetworkClientConnection(
                ActiveDeserializingReader.fromStream(ClientOriginatedServerCommand.class, socket.getInputStream(), threadFactory),
                ActiveSerializingWriter.fromStream(socket.getOutputStream(), threadFactory)
        );
    }

    @Override
    public void init(@NonNull ClientConnectionBus bus, int clientId) {
        this.busConfigReference.set(new BusConfiguration(bus, clientId));
        start();
    }

    /**
     * Initialize the connection
     *
     * @throws IllegalStateException If the connection has been started already, has been closed,
     *                               or if the associated bus has not been set
     */
    public void start() throws IllegalStateException {
        if (busConfigReference.get() == null) {
            throw new IllegalStateException("Connection bus has not been set");
        }

        synchronized (this) { // Starting input and output may not interleave with closing
            input.start(this::onReceive, this::onError);
            output.start(this::onError);
        }
    }

    @Override
    public void send(@NonNull ClientCommand command) {
        output.write(command);
    }

    @Override
    public void close() {
        if (!closeNotificationSent.compareAndSet(false, true)) {
            return;
        }

        BusConfiguration busConfig = busConfigReference.get();
        if (busConfig != null) {
            busConfig.bus.handleClosedConnection(busConfig.clientId);
        }

        synchronized (this) {
            input.close();
            output.close();
        }
    }

    protected void onError() {
        this.close();
    }

    protected void onReceive(@NonNull ClientOriginatedServerCommand receivedObj) {
        Objects.requireNonNull(receivedObj);
        BusConfiguration busConfig = busConfigReference.get();

        if (busConfig == null) {
            throw new IllegalStateException("Received an object before bus configuration was set");
        }

        receivedObj.setClientId(busConfig.clientId);
        busConfig.bus.reportReceivedCommand(receivedObj);
    }

    private static class BusConfiguration {
        private final ClientConnectionBus bus;
        private final int clientId;

        private BusConfiguration(@NonNull ClientConnectionBus bus, int clientId) {
            this.bus = Objects.requireNonNull(bus);
            this.clientId = clientId;
        }
    }
}
