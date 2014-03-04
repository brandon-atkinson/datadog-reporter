package com.acknsyn.brandon.datadog.reporter;

import com.codahale.metrics.Metric;

public interface TagStrategy {
    String[] tags(String name, Metric metric);
}
