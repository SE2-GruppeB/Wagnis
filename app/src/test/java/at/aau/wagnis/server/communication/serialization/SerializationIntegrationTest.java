package at.aau.wagnis.server.communication.serialization;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import at.aau.wagnis.server.communication.command.ProcessChatMessageCommand;
import at.aau.wagnis.server.communication.command.ServerCommand;

public class SerializationIntegrationTest {

    @Test
    public void serializedCommandCanBeDeserialized() throws SerializationException, IOException {
        // given
        ServerCommand command = new ProcessChatMessageCommand("Hello, World!");
        SerializerLoader serializerLoader = new SerializerLoader();

        // when
        byte[] bytes = writeToByteArray(command, serializerLoader);
        DeserializingReader<ServerCommand> reader = readerForByteArray(
                bytes,
                ServerCommand.class,
                serializerLoader
        );

        ServerCommand result = reader.readNext();

        // then
        assertEquals(command, result);
    }

    @Test
    public void commandIsNotDeserializedToIncompatibleClass() throws SerializationException, IOException {
        // given
        ServerCommand command = new ProcessChatMessageCommand("Hello, World!");
        SerializerLoader serializerLoader = new SerializerLoader();

        // when
        byte[] bytes = writeToByteArray(command, serializerLoader);
        DeserializingReader<String> reader = readerForByteArray(
                bytes,
                String.class,
                serializerLoader
        );

        // then
        assertThrows(SerializationException.class, reader::readNext);
    }

    @Test
    public void plainObjectCausesSerializationException() {
        // given
        Object input = new Object();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        SerializingWriter<Object> writer = new SerializingWriter<>(dataOutputStream, new SerializerLoader());

        // when & then
        assertThrows(SerializationException.class, () -> writer.write(input));
    }

    private <T> byte[] writeToByteArray(T command, SerializerLoader serializerLoader) throws SerializationException, IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        SerializingWriter<T> writer = new SerializingWriter<>(dataOutputStream, serializerLoader);
        writer.write(command);

        return byteArrayOutputStream.toByteArray();
    }

    private <T> DeserializingReader<T> readerForByteArray(byte[] bytes, Class<T> targetClass, SerializerLoader serializerLoader) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        return new DeserializingReader<>(
                targetClass,
                dataInputStream,
                serializerLoader
        );
    }
}
