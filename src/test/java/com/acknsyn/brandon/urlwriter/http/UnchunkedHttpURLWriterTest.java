package com.acknsyn.brandon.urlwriter.http;

import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import com.acknsyn.brandon.urlwriter.URL;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UnchunkedHttpURLWriterTest {
    private URL url;
    private HttpURLConnection httpURLConnection;
    private OutputStream outputStream;

    @Before
    public void setup() {
        url = mock(URL.class);
        httpURLConnection = mock(HttpURLConnection.class);
        outputStream = new ByteArrayOutputStream();
    }

    @Test
    public void testConstruct_verifyOptions() throws IOException {
        when(url.openConnection()).thenReturn(httpURLConnection);

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(url);

        verify(url, times(1)).openConnection();
        verify(httpURLConnection, times(1)).setRequestMethod("POST");
        verify(httpURLConnection, times(1)).setDoOutput(true);
        verify(httpURLConnection, times(1)).setRequestProperty("Accept", "*/*");
        verify(httpURLConnection, times(1)).setRequestProperty(eq("Content-Type"), matches("application/json;\\s*charset=utf-8"));
        verify(httpURLConnection, times(1)).setUseCaches(false);
    }

    @Test
    public void testFirstWriteFlushes() throws IOException {
        String expected = "{\"message\":\"hello, world!\"}";

        when(url.openConnection()).thenReturn(httpURLConnection);
        when(httpURLConnection.getOutputStream()).thenReturn(outputStream);
        when(httpURLConnection.getResponseCode()).thenReturn(200);

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(url);

        writer.write(expected);

        assertEquals("", outputStream.toString());

        writer.flush();

        assertEquals(expected, outputStream.toString());

        verify(httpURLConnection, times(1)).getOutputStream();
        verify(httpURLConnection, times(1)).getResponseCode();
        verify(httpURLConnection, times(1)).disconnect();
    }

    @Test
    public void testFirstCloseFlushes() throws IOException {
        String expected = "{\"message\":\"first close flushes\"}";

        when(url.openConnection()).thenReturn(httpURLConnection);
        when(httpURLConnection.getOutputStream()).thenReturn(outputStream);
        when(httpURLConnection.getResponseCode()).thenReturn(200);


        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(url);

        writer.write(expected);

        assertEquals("", outputStream.toString());

        writer.close();

        assertEquals(expected, outputStream.toString());

        verify(httpURLConnection, times(1)).getOutputStream();
        verify(httpURLConnection, times(1)).getResponseCode();
        verify(httpURLConnection, times(1)).disconnect();
    }

    @Test
    public void testThrowsHttpException_100() throws IOException {
        int expectedStatus = 100;
        String expectedResponse = "{\"message\":\"first close flushes\"}";
        InputStream errorStream = new ByteArrayInputStream(expectedResponse.getBytes());

        when(url.openConnection()).thenReturn(httpURLConnection);
        when(httpURLConnection.getOutputStream()).thenReturn(outputStream);
        when(httpURLConnection.getResponseCode()).thenReturn(expectedStatus);
        when(httpURLConnection.getErrorStream()).thenReturn(errorStream);

        HttpException exception = null;

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(url);
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
        String expectedResponse = "{\"message\":\"first close flushes\"}";
        InputStream errorStream = new ByteArrayInputStream(expectedResponse.getBytes());

        when(url.openConnection()).thenReturn(httpURLConnection);
        when(httpURLConnection.getOutputStream()).thenReturn(outputStream);
        when(httpURLConnection.getResponseCode()).thenReturn(expectedStatus);
        when(httpURLConnection.getErrorStream()).thenReturn(errorStream);

        HttpException exception = null;

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(url);
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
        String expectedResponse = "{\"message\":\"first close flushes\"}";
        InputStream errorStream = new ByteArrayInputStream(expectedResponse.getBytes());

        when(url.openConnection()).thenReturn(httpURLConnection);
        when(httpURLConnection.getOutputStream()).thenReturn(outputStream);
        when(httpURLConnection.getResponseCode()).thenReturn(expectedStatus);
        when(httpURLConnection.getErrorStream()).thenReturn(errorStream);

        HttpException exception = null;

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(url);
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
        String expectedResponse = "{\"message\":\"first close flushes\"}";
        InputStream errorStream = new ByteArrayInputStream(expectedResponse.getBytes());

        when(url.openConnection()).thenReturn(httpURLConnection);
        when(httpURLConnection.getOutputStream()).thenReturn(outputStream);
        when(httpURLConnection.getResponseCode()).thenReturn(expectedStatus);
        when(httpURLConnection.getErrorStream()).thenReturn(errorStream);

        HttpException exception = null;

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(url);
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

        when(url.openConnection()).thenReturn(httpURLConnection);
        when(httpURLConnection.getOutputStream()).thenReturn(outputStream);
        when(httpURLConnection.getResponseCode()).thenReturn(expectedStatus);

        HttpException exception = null;

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(url);
        try {
            writer.write("boo");
            writer.close();
        } catch (HttpException he) {
            exception = he;
        }

        assertNull("should not throw HttpException", exception);

        verify(httpURLConnection, never()).getErrorStream();
    }

    @Test
    public void testWriteAfterFlush() throws IOException {
        int expectedStatus= 200;
        when(url.openConnection()).thenReturn(httpURLConnection);
        when(httpURLConnection.getOutputStream()).thenReturn(outputStream);
        when(httpURLConnection.getResponseCode()).thenReturn(expectedStatus);

        IOException exception = null;

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(url);
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
        int expectedStatus= 200;
        when(url.openConnection()).thenReturn(httpURLConnection);
        when(httpURLConnection.getOutputStream()).thenReturn(outputStream);
        when(httpURLConnection.getResponseCode()).thenReturn(expectedStatus);

        IOException exception = null;

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(url);
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
        int expectedStatus= 200;
        when(url.openConnection()).thenReturn(httpURLConnection);
        when(httpURLConnection.getOutputStream()).thenReturn(outputStream);
        when(httpURLConnection.getResponseCode()).thenReturn(expectedStatus);

        IOException exception = null;

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(url);
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
        int expectedStatus= 200;
        when(url.openConnection()).thenReturn(httpURLConnection);
        when(httpURLConnection.getOutputStream()).thenReturn(outputStream);
        when(httpURLConnection.getResponseCode()).thenReturn(expectedStatus);

        IOException exception = null;

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(url);
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
        int expectedStatus= 200;
        when(url.openConnection()).thenReturn(httpURLConnection);
        when(httpURLConnection.getOutputStream()).thenReturn(outputStream);
        when(httpURLConnection.getResponseCode()).thenReturn(expectedStatus);

        IOException exception = null;

        UnchunkedHttpURLWriter writer = new UnchunkedHttpURLWriter(url);
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
