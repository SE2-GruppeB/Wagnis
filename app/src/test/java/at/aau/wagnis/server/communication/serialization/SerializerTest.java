package at.aau.wagnis.server.communication.serialization;


import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

class SerializerTest {

    @Test
    void noMissingValues() {
        for (Serializer<?> serializer : ServiceLoader.load(Serializer.class)) {
            assertNotNull(
                    serializer.getTypeTag(),
                    String.format("Serializer '%s' has null target class", serializer.getClass().getName())
            );

            assertNotNull(
                    serializer.getTypeTag(),
                    String.format("Serializer '%s' has null type tag", serializer.getClass().getName())
            );

            assertNotEquals(
                    "",
                    serializer.getTypeTag(),
                    String.format("Serializer '%s' has empty type tag", serializer.getClass().getName())
            );
        }
    }

    @Test
    void noDuplicateSerializersForClass() {
        Set<Class<?>> seenClasses = new HashSet<>();

        for (Serializer<?> serializer : ServiceLoader.load(Serializer.class)) {
            if (!seenClasses.add(serializer.getTargetClass())) {
                fail(String.format("Duplicate serializers for class '%s'", serializer.getTargetClass().getName()));
            }
        }
    }

    @Test
    void noDuplicateSerializersForTag() {
        Set<String> seenTags = new HashSet<>();

        for (Serializer<?> serializer : ServiceLoader.load(Serializer.class)) {
            if (!seenTags.add(serializer.getTypeTag())) {
                fail(String.format("Duplicate serializers for tag '%s'", serializer.getTypeTag()));
            }
        }
    }
}
