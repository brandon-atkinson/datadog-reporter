package com.acknsyn.brandon.datadog.reporter;

import com.codahale.metrics.Metric;

/**
 *  An {@link com.acknsyn.brandon.datadog.reporter.AliasStrategy} which returns
 *  the original name of the metric, unchanged. Effectively, this alias strategy
 *  causes no change.
 */
public final class NoChangeAliasStrategy implements AliasStrategy {
    public String alias(String name, Metric metric) {
        return name;
    }
}
