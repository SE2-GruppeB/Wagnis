package at.aau.wagnis.server.communication.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

@RunWith(MockitoJUnitRunner.class)
public class ActiveDeserializingReaderTest {

    @Mock private DeserializingReader<Object> reader;
    @Mock private Function<Runnable, Thread> threadFactory;
    @Mock private Thread thread;
    @Mock private Runnable errorCallback;
    @Mock private Consumer<Object> receiveCallback;

    private ActiveDeserializingReader<Object> subject;

    @Before
    public void setup() {
        when(threadFactory.apply(any())).thenReturn(thread);

        subject = new ActiveDeserializingReader<>(reader, threadFactory);
    }

    @Test
    public void startCreatesThread() {
        // when
        subject.start(receiveCallback, errorCallback);

        // then
        verify(threadFactory).apply(any());
        verify(thread).start();
        verifyNoMoreInteractions(reader, threadFactory, thread, errorCallback, receiveCallback);
    }

    @Test
    public void callingStartTwiceThrowsIllegalStateException() {
        // given
        subject.start(receiveCallback, errorCallback);

        // when & then
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> subject.start(receiveCallback, errorCallback)
        );

        assertEquals("Reader has already been started", ex.getMessage());

        verify(threadFactory).apply(any());
        verify(thread).start();
        verifyNoMoreInteractions(reader, threadFactory, thread, errorCallback, receiveCallback);
    }

    @Test
    public void closeBeforeStartClosesReader() throws IOException {
        // when
        subject.close();

        // then
        verify(reader).close();
        verifyNoMoreInteractions(reader, threadFactory, thread, errorCallback, receiveCallback);
    }

    @Test
    public void closeAfterStartInterruptsThread() throws IOException {
        // given
        subject.start(receiveCallback, errorCallback);

        // when
        subject.close();

        // then
        verify(thread).interrupt();
        verify(reader).close();
        verifyNoInteractions(errorCallback, receiveCallback);
    }

    @Test
    public void closingTwiceDoesNotCloseReaderTwice() throws IOException {
        // when
        subject.close();
        subject.close();

        // then
        verify(reader).close();
        verifyNoMoreInteractions(reader, threadFactory, thread, errorCallback, receiveCallback);
    }

    @Test
    public void startAfterCloseThrowsIllegalStateException() {
        // given
        subject.close();

        // when & then
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> subject.start(receiveCallback, errorCallback)
        );

        assertEquals("Reader has been closed", ex.getMessage());
    }

    @Test
    public void ioExceptionOnCloseDoesNotCauseAnotherException() throws IOException {
        // given
        doThrow(new IOException()).when(reader).close();

        // when & then
        try {
            subject.close();
        } catch (Exception ex) {
            Assert.fail();
        }
    }

    @Test
    public void readInternalNotifiesOnReceive() throws SerializationException, IOException {
        // given
        Object readResult = new Object();
        when(reader.readNext()).thenReturn(readResult);
        subject.start(receiveCallback, errorCallback);

        // when
        subject.readInternal();

        // then
        verify(receiveCallback).accept(same(readResult));
    }

    @Test
    public void readInternalClosesReaderOnIOException() throws SerializationException, IOException {
        // given
        when(reader.readNext()).thenThrow(new IOException());
        subject.start(receiveCallback, errorCallback);

        // when
        subject.readInternal();

        // then
        verify(errorCallback).run();
        verify(reader).close();
    }

    @Test
    public void readInternalClosesReaderOnSerializationException() throws SerializationException, IOException {
        // given
        when(reader.readNext()).thenThrow(new SerializationException(""));
        subject.start(receiveCallback, errorCallback);

        // when
        subject.readInternal();

        // then
        verify(errorCallback).run();
        verify(reader).close();
    }
}
