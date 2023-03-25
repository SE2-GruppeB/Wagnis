package at.aau.wagnis.server.communication.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Serializer<T> {

    /**
     * @return The type that should be serialized by this serializer.
     */
    Class<T> getTargetClass();

    /**
     * @return A string used to identify which serializer is responsible to deserialize an object. (Must be unique across all serializers)
     */
    String getTypeTag();

    /**
     * Serialize the object to the provided DataOutputStream.
     * @param obj The object to serialize
     * @param stream The stream to write to
     * @throws IOException If an IOException occurs while writing to the stream
     */
    void writeToStream(T obj, DataOutputStream stream) throws IOException;

    /**
     * Deserialize an object from the provided DataInputStream
     * @param stream The stream to read from
     * @return An object of class {@link #getTargetClass()}
     * @throws IOException If an IOException occurs while reading or if the data
     * read from the stream does not have the correct format for this serializer.
     */
    T readFromStream(DataInputStream stream) throws IOException;
}
