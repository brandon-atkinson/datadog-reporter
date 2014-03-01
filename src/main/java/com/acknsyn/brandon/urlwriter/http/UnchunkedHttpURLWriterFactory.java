package com.acknsyn.brandon.urlwriter.http;

import com.acknsyn.brandon.urlwriter.URLWriterFactory;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;

import com.acknsyn.brandon.urlwriter.io.InputReaderFactory;
import com.acknsyn.brandon.urlwriter.io.OutputWriterFactory;

public class UnchunkedHttpURLWriterFactory implements URLWriterFactory {
    private OutputWriterFactory writerFactory;
    private InputReaderFactory readerFactory;

    public UnchunkedHttpURLWriterFactory(OutputWriterFactory writerFactory, InputReaderFactory readerFactory) {
        this.writerFactory = writerFactory;
        this.readerFactory = readerFactory;
    }

    public Writer getWriter(URL url) throws IOException {
        return new UnchunkedHttpURLWriter(url, writerFactory, readerFactory);
    }
}
