package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

import at.aau.wagnis.server.communication.serialization.Serializer;

public class ProcessChatMessageCommand implements ServerCommand, ClientCommand {

    private final String message;

    public ProcessChatMessageCommand(String message) {
        this.message = Objects.requireNonNull(message);
    }

    @Override
    public void execute() {
        // TODO
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessChatMessageCommand command = (ProcessChatMessageCommand) o;

        return message.equals(command.message);
    }

    @NonNull
    @Override
    public String toString() {
        return "ProcessChatMessageCommand{" +
                "message='" + message + '\'' +
                '}';
    }

    public static class CommandSerializer implements Serializer<ProcessChatMessageCommand> {

        @Override
        public Class<ProcessChatMessageCommand> getTargetClass() {
            return ProcessChatMessageCommand.class;
        }

        @Override
        public String getTypeTag() {
            return "process-chat-message";
        }

        @Override
        public void writeToStream(ProcessChatMessageCommand command, DataOutputStream stream) throws IOException {
            stream.writeUTF(command.message);
        }

        @Override
        public ProcessChatMessageCommand readFromStream(DataInputStream stream) throws IOException {
            return new ProcessChatMessageCommand(stream.readUTF());
        }
    }
}
