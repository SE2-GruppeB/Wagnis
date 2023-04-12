package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

import at.aau.wagnis.server.communication.serialization.Serializer;

public class ProcessChatMessageCommand implements ClientCommand, ClientOriginatedServerCommand {

    private final String message;
    private Integer clientId = null;

    public ProcessChatMessageCommand(@NonNull String message) {
        this.message = Objects.requireNonNull(message);
    }

    @Override
    public void execute() {
        // TODO
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessChatMessageCommand command = (ProcessChatMessageCommand) o;

        if (!message.equals(command.message)) return false;
        return Objects.equals(clientId, command.clientId);
    }

    @Override
    public int hashCode() {
        int result = message.hashCode();
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "ProcessChatMessageCommand{" +
                "message='" + message + '\'' +
                '}';
    }

    @Override
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    @Override
    public int getClientId() {
        if (this.clientId != null) {
            return clientId;
        } else {
            throw new IllegalStateException("ClientId has not been set");
        }
    }

    public static class CommandSerializer implements Serializer<ProcessChatMessageCommand> {

        @NonNull
        @Override
        public Class<ProcessChatMessageCommand> getTargetClass() {
            return ProcessChatMessageCommand.class;
        }

        @NonNull
        @Override
        public String getTypeTag() {
            return "process-chat-message";
        }

        @Override
        public void writeToStream(
                @NonNull ProcessChatMessageCommand command,
                @NonNull DataOutputStream stream
        ) throws IOException {
            stream.writeUTF(command.message);
        }

        @NonNull
        @Override
        public ProcessChatMessageCommand readFromStream(@NonNull DataInputStream stream) throws IOException {
            return new ProcessChatMessageCommand(stream.readUTF());
        }
    }
}
