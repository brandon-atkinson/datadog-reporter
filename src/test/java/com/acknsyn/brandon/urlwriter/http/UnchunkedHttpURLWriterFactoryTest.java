package com.acknsyn.brandon.urlwriter.http;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Writer;
import com.acknsyn.brandon.urlwriter.URL;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UnchunkedHttpURLWriterFactoryTest {
    private UnchunkedHttpURLWriterFactory factory;

    @Before
    public void setup() {
        factory = new UnchunkedHttpURLWriterFactory();
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
