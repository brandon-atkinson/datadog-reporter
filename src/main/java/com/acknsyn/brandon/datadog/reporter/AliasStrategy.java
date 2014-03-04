package com.acknsyn.brandon.datadog.reporter;

import com.codahale.metrics.Metric;

public interface AliasStrategy {
    String alias(String name, Metric metric);
}
