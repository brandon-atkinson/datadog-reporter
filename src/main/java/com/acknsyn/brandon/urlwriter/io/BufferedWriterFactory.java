package com.acknsyn.brandon.urlwriter.io;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

public class BufferedWriterFactory implements OutputWriterFactory {
    public Writer getWriter(OutputStream outputStream, Charset charset) {
        return new BufferedWriter(new OutputStreamWriter(outputStream, charset));
    }
}
