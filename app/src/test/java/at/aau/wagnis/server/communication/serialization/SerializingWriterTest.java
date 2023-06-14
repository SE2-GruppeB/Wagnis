package at.aau.wagnis.server.communication.serialization;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.DataOutputStream;
import java.io.IOException;

class SerializingWriterTest {

    private static final String TAG = "some-tag";

    @Mock
    private SerializerLoader loader;
    @Mock
    private DataOutputStream outputStream;
    @Mock
    private Serializer<Object> serializer;

    private SerializingWriter<Object> subject;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        subject = new SerializingWriter<>(outputStream, loader);
    }

    @Test
    void writesWithSerializer() throws SerializationException, IOException {
        // given
        when(loader.forObject(any())).thenReturn(serializer);
        when(serializer.getTypeTag()).thenReturn(TAG);
        Object input = new Object();

        // when
        subject.write(input);

        // then
        verify(loader).forObject(same(input));

        verify(serializer).getTypeTag();
        verify(outputStream).writeUTF(TAG);

        verify(serializer).writeToStream(same(input), same(outputStream));
        verifyNoMoreInteractions(loader, outputStream, serializer);
    }

    @Test
    void throwsWhenSerializerNotFound() {
        // given
        when(loader.forObject(any())).thenReturn(null);
        Object input = new Object();

        // when & then
        assertThrows(SerializationException.class, () -> subject.write(input));

        verify(loader).forObject(same(input));
        verifyNoMoreInteractions(loader, outputStream, serializer);
    }

    @Test
    void flush() throws IOException {
        // when
        subject.flush();

        // then
        verify(outputStream).flush();
    }

    @Test
    void close() throws IOException {
        // when
        subject.close();

        // then
        verify(outputStream).close();
    }
}