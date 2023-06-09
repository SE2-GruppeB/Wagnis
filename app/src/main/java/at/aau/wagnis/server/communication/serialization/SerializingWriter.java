package at.aau.wagnis.server.communication.serialization;

import androidx.annotation.NonNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class SerializingWriter<T> {

    private final DataOutputStream outputStream;
    private final SerializerLoader serializerLoader;

    public SerializingWriter(@NonNull DataOutputStream stream, @NonNull SerializerLoader serializerLoader) {
        this.outputStream = Objects.requireNonNull(stream);
        this.serializerLoader = Objects.requireNonNull(serializerLoader);
    }

    public void write(@NonNull T obj) throws IOException, SerializationException {
        Serializer<T> serializer = serializerLoader.forObject(obj);

        if (serializer == null) {
            throw new SerializationException(String.format("No serializer for object of type '%s' found", obj.getClass().getSimpleName()));
        }

        outputStream.writeUTF(serializer.getTypeTag());
        serializer.writeToStream(obj, outputStream);
    }

    public void flush() throws IOException {
        this.outputStream.flush();
    }

    public void close() throws IOException {
        this.outputStream.close();
    }
}
