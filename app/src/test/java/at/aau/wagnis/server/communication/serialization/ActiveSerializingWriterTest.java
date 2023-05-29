package at.aau.wagnis.server.communication.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.function.Function;

@Timeout(5)
class ActiveSerializingWriterTest {

    @Mock
    private SerializingWriter<Object> writer;
    @Mock
    private Function<Runnable, Thread> threadFactory;
    @Mock
    private Thread thread;
    @Mock
    private Runnable errorCallback;

    private ActiveSerializingWriter<Object> subject;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(threadFactory.apply(any())).thenReturn(thread);

        subject = new ActiveSerializingWriter<>(writer, threadFactory);
    }

    @Test
    void startCreatesThread() {
        // when
        subject.start(errorCallback);

        // then
        verify(threadFactory).apply(any());
        verify(thread).start();
    }

    @Test
    void callingStartTwiceThrowsIllegalStateException() {
        // given
        subject.start(errorCallback);

        // when & then
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> subject.start(errorCallback)
        );

        assertEquals("Writer has already been started", ex.getMessage());
    }

    @Test
    void startAfterCloseThrowsIllegalStateException() {
        // given
        subject.close();

        // when & then
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> subject.start(errorCallback)
        );

        assertEquals("Writer has been closed", ex.getMessage());
    }

    @Test
    void closeBeforeStartClosesWriter() throws IOException {
        // when
        subject.close();

        // then
        verify(writer).close();
        verifyNoMoreInteractions(writer, threadFactory, thread, errorCallback);
    }

    @Test
    void closeAfterStartInterruptsThread() throws IOException {
        // given
        subject.start(errorCallback);

        // when
        subject.close();

        // then
        verify(thread).interrupt();
        verify(writer).close();
        verifyNoInteractions(errorCallback);
    }

    @Test
    void closingTwiceDoesNotCloseWriterTwice() throws IOException {
        // when
        subject.close();
        subject.close();

        // then
        verify(writer).close();
        verifyNoMoreInteractions(writer, errorCallback, thread);
    }

    @Test
    void ioExceptionOnCloseDoesNotCauseAnotherException() throws IOException {
        // given
        doThrow(new IOException()).when(writer).close();

        // when & then
        try {
            subject.close();
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    void writeBeforeStartThrowsIllegalStateException() {
        // given
        Object input = new Object();

        // when & then
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> subject.write(input)
        );

        assertEquals("Writer has not been started", ex.getMessage());
    }

    @Test
    void writeAfterCloseThrowsIllegalStateException() {
        // given
        Object input = new Object();

        subject.start(errorCallback);
        subject.close();

        // when & then
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> subject.write(input)
        );

        assertEquals("Writer has been closed", ex.getMessage());
    }

    @Test
    void writeInternalWritesObjectsInOrder() throws SerializationException, IOException {
        // given
        Object input1 = new Object();
        Object input2 = new Object();

        subject.start(errorCallback);

        subject.write(input1);
        subject.write(input2);

        // when
        subject.writeInternal();
        subject.writeInternal();

        // then
        InOrder inOrder = Mockito.inOrder(writer);
        inOrder.verify(writer).write(input1);
        inOrder.verify(writer).flush();
        inOrder.verify(writer).write(input2);
        inOrder.verify(writer).flush();
        verifyNoMoreInteractions(writer);
    }

    @Test
    void writeInternalCallsErrorCallbackOnIOException() throws SerializationException, IOException {
        // given
        doThrow(new IOException()).when(writer).write(any());

        Object input = new Object();
        subject.start(errorCallback);

        // when
        subject.write(input);
        subject.writeInternal();

        // then
        verify(errorCallback).run();
        verify(writer).close();
    }

    @Test
    void writeInternalCallsErrorCallbackOnSerializationException() throws SerializationException, IOException {
        // given
        doThrow(new SerializationException("")).when(writer).write(any());

        Object input = new Object();
        subject.start(errorCallback);

        // when
        subject.write(input);
        subject.writeInternal();

        // then
        verify(errorCallback).run();
        verify(writer).close();
    }
}
