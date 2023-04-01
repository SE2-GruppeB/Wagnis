package at.aau.wagnis.server.communication.serialization;

import androidx.annotation.NonNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Objects;

public class DeserializingReader<T> {

    private final Class<T> inputType;
    private final DataInputStream inputStream;
    private final SerializerLoader serializerLoader;

    public DeserializingReader(
            @NonNull Class<T> inputType,
            @NonNull DataInputStream stream,
            @NonNull SerializerLoader serializerLoader
    ) {
        this.inputType = Objects.requireNonNull(inputType);
        this.inputStream = Objects.requireNonNull(stream);
        this.serializerLoader = Objects.requireNonNull(serializerLoader);
    }

    @NonNull
    public T readNext() throws IOException, SerializationException {
        String messageTag = inputStream.readUTF();

        Serializer<T> serializer = serializerLoader.forTag(messageTag, inputType);

        if (serializer == null) {
            throw new SerializationException(String.format(
                    "Could not find a deserializer for tag '%s' and type '%s'",
                    messageTag,
                    inputType.getSimpleName()
            ));
        }

        return serializer.readFromStream(inputStream);
    }

    public void close() throws IOException {
        inputStream.close();
    }
}
