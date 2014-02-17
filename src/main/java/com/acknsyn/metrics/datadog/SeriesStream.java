package com.acknsyn.metrics.datadog;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

public class SeriesStream implements Closeable {

    private Writer writer;
    private boolean firstMetric = true;

    public SeriesStream(Writer connection) throws IOException {
        this.writer = connection;
        connection.write("{\"series\":[");
    }

    public void writeMetric(String metricName, long timestamp, Number dataPoint, String host,
                            String[] tags) throws IOException {
        writeMetric(metricName, timestamp, dataPoint, MetricType.GAUGE, host, tags);
    }

    public void writeMetric(String metricName, long timestamp, Number dataPoint, MetricType metricType, String host,
                            String[] tags) throws IOException {
        if (firstMetric) {
            firstMetric = false;
        } else {
            writer.write(',');
        }

        writer.write('{');

        writer.write("\"metric\":\"");
        writer.write(metricName);
        writer.write("\",");

        writer.write("\"points\":[[");
        writer.write(format(timestamp));
        writer.write(',');
        writer.write(format(dataPoint));
        writer.write("]]");

        if (metricType != null) {
            writer.write(",\"type\":\"");
            writer.write(metricType.toString());
            writer.write('"');
        }

        if (host != null) {
            writer.write(",\"host\":\"");
            writer.write(host);
            writer.write('"');
        }

        if (tags != null && tags.length > 0) {
            writer.write(",\"tags\":[");
            for (int i=0; i < tags.length; i++) {
                String tag = tags[i];
                if (i > 0) {
                    writer.write(',');
                }
                writer.write('"');
                writer.write(tag);
                writer.write('"');
            }
            writer.write(']');
        }

        writer.write('}');
    }

    public void close() throws IOException {
        writer.write("]}");
    }

    private String format(Number n) {
        if (n instanceof Float) {
            return format(n.doubleValue());
        } else if (n instanceof Double) {
            return format(n.doubleValue());
        } else if (n instanceof Byte) {
            return format(n.longValue());
        } else if (n instanceof Short) {
            return format(n.longValue());
        } else if (n instanceof Integer) {
            return format(n.longValue());
        } else if (n instanceof Long) {
            return format(n.longValue());
        }
        return null;
    }

    private String format(long n) {
        return Long.toString(n);
    }

    private String format(double v) {
        return String.format(Locale.US, "%2.2f", v);
    }

    public static enum MetricType {
        GAUGE("gauge"), COUNTER("counter");

        private final String value;
        private MetricType(String value) {
            this.value  = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
