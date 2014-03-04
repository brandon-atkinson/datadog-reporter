package com.acknsyn.brandon.datadog.reporter;

import com.codahale.metrics.Metric;

/**
 * A {@link com.acknsyn.brandon.datadog.reporter.EmptyTagStrategy} which returns an
 * empty array of tags regardless of the name or type of metric.
 */
public final class EmptyTagStrategy implements TagStrategy {
    private final String[] EMPTY = new String[0];
    public String[] tags(String name, Metric metric) {
        return EMPTY;
    }
}
