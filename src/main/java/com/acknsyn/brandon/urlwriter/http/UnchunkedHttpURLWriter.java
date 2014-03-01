package com.acknsyn.brandon.urlwriter.http;

import com.acknsyn.brandon.urlwriter.io.InputReaderFactory;
import com.acknsyn.brandon.urlwriter.io.OutputWriterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class UnchunkedHttpURLWriter extends Writer {
    private static final Logger log = LoggerFactory.getLogger(UnchunkedHttpURLWriter.class);

    private static final int HTTP_CONNECT_TIMEOUT_MILLIS = 2500;
    private static final int HTTP_READ_TIMEOUT_MILLIS = 2500;
    private static final String HTTP_ACCEPT_KEY = "Accept";
    private static final String HTTP_ACCEPT_VALUE = "*/*";
    private static final String HTTP_CONTENT_TYPE_KEY = "Content-Type";
    private static final String HTTP_CONTENT_TYPE_VALUE = "application/json; charset=utf-8";
    private static final String HTTP_POST = "POST";
    private static final String HTTP_CHARSET = "UTF-8";

    private HttpURLConnection connection;
    private OutputWriterFactory writerFactory;
    private InputReaderFactory readerFactory;

    private ByteArrayOutputStream request;
    private boolean flushed = false;
    private boolean closed = false;

    public UnchunkedHttpURLWriter(URL url, OutputWriterFactory writerFactory, InputReaderFactory readerFactory) throws IOException {
        connection = (HttpURLConnection) url.openConnection();
        this.writerFactory = writerFactory;
        this.readerFactory = readerFactory;

        connection.setConnectTimeout(HTTP_CONNECT_TIMEOUT_MILLIS);
        connection.setReadTimeout(HTTP_READ_TIMEOUT_MILLIS);
        connection.setDoOutput(true);
        connection.setRequestMethod(HTTP_POST);
        connection.setRequestProperty(HTTP_CONTENT_TYPE_KEY, HTTP_CONTENT_TYPE_VALUE);
        connection.setRequestProperty(HTTP_ACCEPT_KEY, HTTP_ACCEPT_VALUE);
        connection.setUseCaches(false);
        request = new ByteArrayOutputStream();
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (closed) throw new IOException("write failed: writer is closed");
        if (flushed) throw new IOException("write failed: write disallowed after first flush");

        for (int i = off; i < off + len; i++) {
            request.write(cbuf[i]);
        }
    }

    @Override
    public void flush() throws IOException {
        if (closed) throw new IOException("flush failed: writer closed");
        if (flushed) throw new IOException("flush failed: only a single flush supported");

        //now that we know there's no more data, set content-length
        connection.setFixedLengthStreamingMode(request.size());

        //write out the data
        Writer writer = null;
        try {
            writer = writerFactory.getWriter(connection.getOutputStream(), Charset.forName(HTTP_CHARSET));
            writer.write(request.toString());
            writer.flush();

            log.debug("made http request {}", request);

            int responseCode = connection.getResponseCode();

            if (responseCode >= 300 || responseCode < 200) {
                throw new HttpException(responseCode, getResponse());
            }

        } catch (HttpException hwe) {
            throw hwe;
        } finally {
            flushed = true;

            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    log.error("couldn't close http writer", e);
                }
            }
        }
        connection.disconnect();
    }

    @Override
    public void close() throws IOException {
        try {
            if (!flushed) flush();
        } finally {
            closed = true;
        }
    }

    private String getResponse() throws HttpException {
        String response = null;

        Reader reader = null;

        try {
            InputStream errorStream = connection.getErrorStream();

            if (errorStream != null) {
                reader = readerFactory.getReader(errorStream, Charset.forName(HTTP_CHARSET));

                StringBuilder sb = new StringBuilder();

                int character;
                while ((character = reader.read()) > -1) {
                    sb.append((char) character);
                }

                response = sb.toString();
            }
        } catch (Exception e) {
            log.error("couldn't read error stream", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    log.error("couldn't close error stream", e);
                }
            }
        }

        return response;
    }
}
