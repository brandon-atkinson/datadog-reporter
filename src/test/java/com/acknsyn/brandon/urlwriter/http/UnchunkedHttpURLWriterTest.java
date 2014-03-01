package com.acknsyn.brandon.urlwriter.http;

import com.acknsyn.brandon.urlwriter.io.InputReaderFactory;
import com.acknsyn.brandon.urlwriter.io.OutputWriterFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({UnchunkedHttpURLWriter.class})
public class UnchunkedHttpURLWriterTest {
    private URL mockUrl;
    private HttpURLConnection mockHttpURLConnection;

    private OutputWriterFactory mockOutputWriterFactory;
    private Writer mockWriter;

    private InputReaderFactory mockInputReaderFactory;
    private Reader mockReader;

    private InputStream mockInputStream;

    @Before
    public void setup() throws IOException {
        mockUrl = PowerMockito.mock(URL.class);
        mockHttpURLConnection = PowerMockito.mock(HttpURLConnection.class);
        when(mockUrl.openConnection()).thenReturn(mockHttpURLConnection);

        mockOutputWriterFactory = mock(OutputWriterFactory.class);
        mockWriter = mock(Writer.class);
        when(mockOutputWriterFactory.getWriter(any(OutputStream.class), any(Charset.class))).thenReturn(mockWriter);

        mockInputReaderFactory = mock(InputReaderFactory.class);
        mockReader = mock(Reader.class);
        when(mockInputReaderFactory.getReader(any(InputStream.class), any(Charset.class))).thenReturn(mockReader);
        mockInputStream = mock(InputStream.class);
    }

    @Test
    public void testConstruct_verifyOptions() throws Exception {
        UnchunkedHttpURLWriter httpURLWriter = new UnchunkedHttpURLWriter(mockUrl, mockOutputWriterFactory, mockInputReaderFactory);

//        verify(mockUrl).openConnection(); //cannot be verified; mockito limitation
        verify(mockHttpURLConnection).setRequestMethod("POST");
        verify(mockHttpURLConnection).setDoOutput(true);
        verify(mockHttpURLConnection).setRequestProperty("Accept", "*/*");
        verify(mockHttpURLConnection).setRequestProperty(eq("Content-Type"), matches("application/json;\\s*charset=utf-8"));
        verify(mockHttpURLConnection).setUseCaches(false);
    }

    @Test
    public void testFirstFlushFlushes() throws IOException {
        String expected = "{\"message\":\"hello, world!\"}";

        when(mockHttpURLConnection.getResponseCode()).thenReturn(200);

        UnchunkedHttpURLWriter unchunkedHttpURLWriter = new UnchunkedHttpURLWriter(mockUrl, mockOutputWriterFactory, mockInputReaderFactory);

        unchunkedHttpURLWriter.write(expected);
        verifyNoMoreInteractions(mockWriter);

        unchunkedHttpURLWriter.flush();
        verify(mockWriter).write(expected);
        verify(mockWriter).flush();
        verify(mockHttpURLConnection).disconnect();
    }

    @Test
    public void testFirstCloseFlushes() throws IOException {
        String expected = "{\"message\":\"first close flushes\"}";

        when(mockUrl.openConnection()).thenReturn(mockHttpURLConnection);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(200);


        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(mockUrl, mockOutputWriterFactory, mockInputReaderFactory);

        writer.write(expected);
        verifyNoMoreInteractions(mockWriter);

        writer.close();
        verify(mockWriter).write(expected);
        verify(mockWriter).flush();
        verify(mockHttpURLConnection).disconnect();
    }

    private void setErrorResponse(String errorResponse) throws IOException {
        char[] errorResponseChars = errorResponse.toCharArray();
        OngoingStubbing stub = when(mockReader.read());
        for (int i = 0; i < errorResponseChars.length; i++) {
            stub = stub.thenReturn((int) errorResponseChars[i]);
        }
        stub.thenReturn(-1);
    }

    @Test
    public void testThrowsHttpException_100() throws IOException {
        int expectedStatus = 100;
        String expectedResponse = "continue";

        when(mockUrl.openConnection()).thenReturn(mockHttpURLConnection);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(expectedStatus);
        when(mockHttpURLConnection.getErrorStream()).thenReturn(mockInputStream);
        setErrorResponse(expectedResponse);

        HttpException exception = null;

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(mockUrl, mockOutputWriterFactory, mockInputReaderFactory);
        try {
            writer.write("boo");
            writer.close();
        } catch (HttpException he) {
            exception = he;
        }

        assertNotNull("should throw HttpException", exception);
        assertEquals("should return correct status", expectedStatus, exception.getStatus());
        assertEquals("should return error response", expectedResponse, exception.getResponse());
    }

    @Test
    public void testThrowsHttpException_304() throws IOException {
        int expectedStatus = 304;
        String expectedResponse = "temporarily moved";

        when(mockUrl.openConnection()).thenReturn(mockHttpURLConnection);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(expectedStatus);
        when(mockHttpURLConnection.getErrorStream()).thenReturn(mockInputStream);
        setErrorResponse(expectedResponse);

        HttpException exception = null;

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(mockUrl, mockOutputWriterFactory, mockInputReaderFactory);
        try {
            writer.write("boo");
            writer.close();
        } catch (HttpException he) {
            exception = he;
        }

        assertNotNull("should throw HttpException", exception);
        assertEquals("should return correct status", expectedStatus, exception.getStatus());
        assertEquals("should return error response", expectedResponse, exception.getResponse());
    }

    @Test
    public void testThrowsHttpException_400() throws IOException {
        int expectedStatus = 400;
        String expectedResponse = "not found";

        when(mockUrl.openConnection()).thenReturn(mockHttpURLConnection);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(expectedStatus);
        when(mockHttpURLConnection.getErrorStream()).thenReturn(mockInputStream);
        setErrorResponse(expectedResponse);

        HttpException exception = null;

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(mockUrl, mockOutputWriterFactory, mockInputReaderFactory);
        try {
            writer.write("boo");
            writer.close();
        } catch (HttpException he) {
            exception = he;
        }

        assertNotNull("should throw HttpException", exception);
        assertEquals("should return correct status", expectedStatus, exception.getStatus());
        assertEquals("should return error response", expectedResponse, exception.getResponse());
    }

    @Test
    public void testThrowsHttpException_500() throws IOException {
        int expectedStatus = 500;
        String expectedResponse = "server error";

        when(mockUrl.openConnection()).thenReturn(mockHttpURLConnection);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(expectedStatus);
        when(mockHttpURLConnection.getErrorStream()).thenReturn(mockInputStream);
        setErrorResponse(expectedResponse);

        HttpException exception = null;

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(mockUrl, mockOutputWriterFactory, mockInputReaderFactory);
        try {
            writer.write("boo");
            writer.close();
        } catch (HttpException he) {
            exception = he;
        }

        assertNotNull("should throw HttpException", exception);
        assertEquals("should return correct status", expectedStatus, exception.getStatus());
        assertEquals("should return error response", expectedResponse, exception.getResponse());
    }

    @Test
    public void testThrowsHttpException_200() throws IOException {
        int expectedStatus = 200;

        when(mockUrl.openConnection()).thenReturn(mockHttpURLConnection);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(expectedStatus);

        HttpException exception = null;

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(mockUrl, mockOutputWriterFactory, mockInputReaderFactory);
        try {
            writer.write("boo");
            writer.close();
        } catch (HttpException he) {
            exception = he;
        }

        assertNull("should not throw HttpException", exception);

        verify(mockHttpURLConnection, never()).getErrorStream();
    }

    @Test
    public void testWriteAfterFlush() throws IOException {
        int expectedStatus = 200;
        when(mockUrl.openConnection()).thenReturn(mockHttpURLConnection);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(expectedStatus);

        IOException exception = null;

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(mockUrl, mockOutputWriterFactory, mockInputReaderFactory);
        try {
            writer.write("boo");
            writer.flush();
        } catch (IOException he) {
            exception = he;
        }

        assertNull("should not throw IOException if write called before flush", exception);

        try {
            writer.write("boo");
        } catch (IOException he) {
            exception = he;
        }

        assertNotNull("should throw IOException if write called after flush", exception);
    }

    @Test
    public void testWriteAfterClose() throws IOException {
        int expectedStatus = 200;
        when(mockUrl.openConnection()).thenReturn(mockHttpURLConnection);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(expectedStatus);

        IOException exception = null;

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(mockUrl, mockOutputWriterFactory, mockInputReaderFactory);
        try {
            writer.write("boo");
            writer.close();
        } catch (IOException he) {
            exception = he;
        }

        assertNull("should not throw IOException if write called before close", exception);

        try {
            writer.write("fail!");
        } catch (IOException he) {
            exception = he;
        }

        assertNotNull("should throw IOException if write called after close", exception);
    }

    @Test
    public void testFlushAfterFlush() throws IOException {
        int expectedStatus = 200;
        when(mockUrl.openConnection()).thenReturn(mockHttpURLConnection);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(expectedStatus);

        IOException exception = null;

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(mockUrl, mockOutputWriterFactory, mockInputReaderFactory);
        try {
            writer.write("boo");
            writer.flush();
        } catch (IOException he) {
            exception = he;
        }

        assertNull("should not throw IOException after first flush", exception);

        try {
            writer.flush();
        } catch (IOException he) {
            exception = he;
        }


        assertNotNull("should throw IOException if flush called after flush", exception);
    }

    @Test
    public void testFlushAfterClose() throws IOException {
        int expectedStatus = 200;
        when(mockUrl.openConnection()).thenReturn(mockHttpURLConnection);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(expectedStatus);

        IOException exception = null;

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(mockUrl, mockOutputWriterFactory, mockInputReaderFactory);
        try {
            writer.write("boo");
            writer.close();
        } catch (IOException he) {
            exception = he;
        }

        assertNull("should not throw IOException during write/close", exception);

        try {
            writer.flush();
        } catch (IOException he) {
            exception = he;
        }


        assertNotNull("should throw IOException if flush called after close", exception);
    }

    @Test
    public void testCloseAfterClose() throws IOException {
        int expectedStatus = 200;
        when(mockUrl.openConnection()).thenReturn(mockHttpURLConnection);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(expectedStatus);

        IOException exception = null;

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(mockUrl, mockOutputWriterFactory, mockInputReaderFactory);
        try {
            writer.write("boo");
            writer.close();
        } catch (IOException he) {
            exception = he;
        }

        assertNull("should not throw IOException during write/close", exception);

        try {
            writer.close();
        } catch (IOException he) {
            exception = he;
        }


        assertNull("should not throw IOException if close called after close", exception);
    }
}
