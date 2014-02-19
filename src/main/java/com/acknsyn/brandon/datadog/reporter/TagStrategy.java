package com.acknsyn.brandon.datadog.reporter;

import com.codahale.metrics.Metric;

public interface TagStrategy {

    String[] tags(String name, Metric metric);

    TagStrategy NO_TAGS = new TagStrategy() {
        private final String[] EMPTY = new String[0];
        public String[] tags(String name, Metric metric) {
            return EMPTY;
        }
    };

}
