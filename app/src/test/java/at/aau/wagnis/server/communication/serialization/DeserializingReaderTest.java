package at.aau.wagnis.server.communication.serialization;


import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.DataInputStream;
import java.io.IOException;

class DeserializingReaderTest {

    private static final String TAG = "some-tag";
    private static final Class<Object> EXPECTED_CLASS = Object.class;

    @Mock
    private SerializerLoader loader;
    @Mock
    private DataInputStream dataInputStream;
    @Mock
    private Serializer<Object> serializer;

    private DeserializingReader<Object> subject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        subject = new DeserializingReader<>(EXPECTED_CLASS, dataInputStream, loader);
    }

    @Test
    void readsWithSerializer() throws IOException, SerializationException {
        // given
        Object serializerOutput = new Object();

        when(dataInputStream.readUTF()).thenReturn(TAG);
        when(loader.forTag(any(), any())).thenReturn(serializer);
        when(serializer.readFromStream(any())).thenReturn(serializerOutput);

        // when
        Object result = subject.readNext();

        // then
        assertSame(serializerOutput, result);

        verify(dataInputStream).readUTF();
        verify(loader).forTag(eq(TAG), same(EXPECTED_CLASS));
        verify(serializer).readFromStream(dataInputStream);
        verifyNoMoreInteractions(loader, dataInputStream, serializer);
    }

    @Test
    void throwsWhenSerializerNotFound() throws IOException {
        // given
        when(dataInputStream.readUTF()).thenReturn(TAG);
        when(loader.forTag(any(), any())).thenReturn(null);

        // when & then
        assertThrows(SerializationException.class, () -> subject.readNext());

        verify(dataInputStream).readUTF();
        verify(loader).forTag(eq(TAG), same(EXPECTED_CLASS));
        verifyNoMoreInteractions(loader, dataInputStream, serializer);
    }

    @Test
    void close() throws IOException {
        // when
        subject.close();

        // then
        verify(dataInputStream).close();
    }
}