package at.aau.wagnis.server.communication.serialization;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;
import java.util.ServiceLoader;

public class SerializerLoader {

    @Nullable
    public <T> Serializer<T> forObject(@NonNull T obj) {
        Objects.requireNonNull(obj);

        for (Serializer<?> serializer : ServiceLoader.load(Serializer.class)) {
            if (serializer.getTargetClass().isAssignableFrom(obj.getClass())) {
                return castUnchecked(serializer);
            }
        }

        return null;
    }

    @Nullable
    public <T> Serializer<T> forTag(@NonNull String tag, @NonNull Class<T> expectedClass) {
        Objects.requireNonNull(tag);
        Objects.requireNonNull(expectedClass);

        for (Serializer<?> serializer : ServiceLoader.load(Serializer.class)) {
            if (serializer.getTypeTag().equals(tag) && expectedClass.isAssignableFrom(serializer.getTargetClass())) {
                return castUnchecked(serializer);
            }
        }

        return null;
    }

    /**
     * The ServiceLoader does not support loading by type parameter, so this cast is necessary.
     * (Type safety is ensured before calling by checking against {@link Serializer#getTargetClass()})
     */
    @SuppressWarnings("unchecked")
    private <T> Serializer<T> castUnchecked(Serializer<?> serializer) {
        return (Serializer<T>) serializer;
    }
}
