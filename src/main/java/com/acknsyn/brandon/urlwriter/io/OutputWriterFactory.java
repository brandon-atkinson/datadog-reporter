package com.acknsyn.brandon.urlwriter.io;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

public interface OutputWriterFactory {
    Writer getWriter(OutputStream outputStream, Charset charset);
}
