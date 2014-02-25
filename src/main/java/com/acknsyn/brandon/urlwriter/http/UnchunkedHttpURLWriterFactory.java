package com.acknsyn.brandon.urlwriter.http;

import com.acknsyn.brandon.urlwriter.URLWriterFactory;

import java.io.IOException;
import java.io.Writer;
import com.acknsyn.brandon.urlwriter.URL;

public class UnchunkedHttpURLWriterFactory implements URLWriterFactory {
    public Writer getWriter(URL url) throws IOException {
        return new UnchunkedHttpURLWriter(url);
    }
}
