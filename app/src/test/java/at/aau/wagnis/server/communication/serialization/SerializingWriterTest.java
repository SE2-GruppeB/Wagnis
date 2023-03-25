package at.aau.wagnis.server.communication.serialization;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.DataOutputStream;
import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class SerializingWriterTest {

    private static final String TAG = "some-tag";

    @Mock private SerializerLoader loader;
    @Mock private DataOutputStream outputStream;
    @Mock private Serializer<Object> serializer;

    private SerializingWriter<Object> subject;

    @Before
    public void setup() {
        subject = new SerializingWriter<>(outputStream, loader);
    }

    @Test
    public void writesWithSerializer() throws SerializationException, IOException {
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
    public void throwsWhenSerializerNotFound() {
        // given
        when(loader.forObject(any())).thenReturn(null);
        Object input = new Object();

        // when & then
        assertThrows(SerializationException.class, () -> subject.write(input));

        verify(loader).forObject(same(input));
        verifyNoMoreInteractions(loader, outputStream, serializer);
    }

    @Test
    public void flush() throws IOException {
        // when
        subject.flush();

        // then
        verify(outputStream).flush();
    }
}