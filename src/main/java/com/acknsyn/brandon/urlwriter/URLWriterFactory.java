package com.acknsyn.brandon.urlwriter;

import java.io.IOException;
import java.io.Writer;

public interface URLWriterFactory {
    Writer getWriter(URL url) throws IOException;
}
