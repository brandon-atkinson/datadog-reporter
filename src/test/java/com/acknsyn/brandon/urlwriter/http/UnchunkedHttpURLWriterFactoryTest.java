package com.acknsyn.brandon.urlwriter.http;

import com.acknsyn.brandon.urlwriter.io.InputReaderFactory;
import com.acknsyn.brandon.urlwriter.io.OutputWriterFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class UnchunkedHttpURLWriterFactoryTest {
    private OutputWriterFactory mockOutputWriterFactory;
    private InputReaderFactory mockInputReaderFactory;
    private UnchunkedHttpURLWriterFactory factory;

    @Before
    public void setup() {
        mockOutputWriterFactory = mock(OutputWriterFactory.class);
        mockInputReaderFactory = mock(InputReaderFactory.class);
        factory = new UnchunkedHttpURLWriterFactory(mockOutputWriterFactory, mockInputReaderFactory);
    }

    @Test
    public void testFactory_httpUrl() throws IOException {
        URL url = new URL("http://example.com");

        Writer writer = factory.getWriter(url);

        assertNotNull("should return non-null writer", writer);
        assertTrue("should be instance of UnchunkedHttpURLWriter", writer instanceof UnchunkedHttpURLWriter);
    }

    @Test
    public void testFactory_httpsUrl() throws IOException {
        URL url = new URL("https://example.com");

        Writer writer = factory.getWriter(url);

        assertNotNull("should return non-null writer", writer);
        assertTrue("should be instance of UnchunkedHttpURLWriter", writer instanceof UnchunkedHttpURLWriter);
    }

    @Test
    public void testFactory_nonHttpUrl() throws IOException {
        URL fileUrl = new URL("file:///Users/brandon/hello_world.txt");

        boolean classCastExceptionThrown = false;
        try {
            Writer writer = factory.getWriter(fileUrl);
        } catch (ClassCastException cce) {
            classCastExceptionThrown = true;
        }

        assertTrue("should throw class cast exception for non http urls", classCastExceptionThrown);
    }
}
