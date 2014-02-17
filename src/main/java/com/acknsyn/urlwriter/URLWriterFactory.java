package com.acknsyn.urlwriter;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;

/**
 *
 */
public interface URLWriterFactory {
    Writer getWriter(URL url) throws IOException;
}
