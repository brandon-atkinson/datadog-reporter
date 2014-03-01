package com.acknsyn.brandon.urlwriter.io;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

public interface InputReaderFactory {
    Reader getReader(InputStream inputStream, Charset charset);
}
