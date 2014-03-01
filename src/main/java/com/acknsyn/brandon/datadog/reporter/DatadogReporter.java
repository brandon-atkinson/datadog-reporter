package com.acknsyn.brandon.datadog.reporter;

import com.acknsyn.brandon.urlwriter.URLWriterFactory;
import com.acknsyn.brandon.urlwriter.http.HttpException;
import com.acknsyn.brandon.urlwriter.http.UnchunkedHttpURLWriterFactory;
import com.acknsyn.brandon.urlwriter.io.BufferedReaderFactory;
import com.acknsyn.brandon.urlwriter.io.BufferedWriterFactory;
import com.codahale.metrics.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;

public class DatadogReporter extends ScheduledReporter {
    private static final Logger log = LoggerFactory.getLogger(DatadogReporter.class);
    private static final String REPORTER_NAME = "datadog-reporter";
    private static final String DATADOG_API_BASE_URL = "https://app.datadoghq.com/api/v1";
    private static final String SERIES_URL_TEMPLATE = DATADOG_API_BASE_URL + "/series?api_key=%s";
    private static final String EVENTS_URL_TEMPLATE = DATADOG_API_BASE_URL + "/events?api_key=%s";
    private static final int MILLIS_PER_SECOND = 1000;

    private final Clock clock;
    private final AliasStrategy aliasStrategy;
    private final TagStrategy tagStrategy;
    private final LifecycleEventStrategy lifecycleEventStrategy;
    private final String host;
    private final String apiKey;
    private final URLWriterFactory urlWriterFactory;

    private DatadogReporter(MetricRegistry metricRegistry, Clock clock, MetricFilter metricFilter, TimeUnit rateUnit,
                            TimeUnit durationUnit, AliasStrategy aliasStrategy, TagStrategy tagStrategy,
                            LifecycleEventStrategy lifecycleEventStrategy, String host, String apiKey) {
        this(metricRegistry, clock, metricFilter, rateUnit,
                durationUnit, aliasStrategy, tagStrategy,
                lifecycleEventStrategy, host, apiKey,
                new UnchunkedHttpURLWriterFactory(
                        new BufferedWriterFactory(),
                        new BufferedReaderFactory()));
    }

    private DatadogReporter(MetricRegistry metricRegistry, Clock clock, MetricFilter metricFilter, TimeUnit rateUnit,
                            TimeUnit durationUnit, AliasStrategy aliasStrategy, TagStrategy tagStrategy,
                            LifecycleEventStrategy lifecycleEventStrategy, String host, String apiKey,
                            URLWriterFactory connectionFactory) {
        super(metricRegistry, REPORTER_NAME, metricFilter, rateUnit, durationUnit);
        this.clock = clock;
        this.aliasStrategy = aliasStrategy;
        this.tagStrategy = tagStrategy;
        this.lifecycleEventStrategy = lifecycleEventStrategy;
        this.host = host;
        this.apiKey = apiKey;
        this.urlWriterFactory = connectionFactory;
    }

    public static Builder forRegistry(MetricRegistry registry) {
        return new Builder(registry);
    }

    public static class Builder {
        private MetricRegistry registry;
        private Clock clock = Clock.defaultClock();
        private MetricFilter filter = MetricFilter.ALL;
        private TimeUnit rateUnit = TimeUnit.SECONDS;
        private TimeUnit durationUnit = TimeUnit.MILLISECONDS;
        private AliasStrategy aliasStrategy = AliasStrategy.NO_ALIASES;
        private TagStrategy tagStrategy = TagStrategy.NO_TAGS;
        private LifecycleEventStrategy lifecycleEventStrategy = LifecycleEventStrategy.NO_EVENTS;

        public Builder(MetricRegistry registry) {
            this.registry = registry;
        }

        public Builder withClock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }

        public Builder convertRatesTo(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        public Builder convertDurationsTo(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        public Builder aliasUsing(AliasStrategy nameConverter) {
            this.aliasStrategy = nameConverter;
            return this;
        }

        public Builder tagUsing(TagStrategy tagStrategy) {
            this.tagStrategy = tagStrategy;
            return this;
        }

        public Builder lifecycleEventsUsing(LifecycleEventStrategy lifecycleEventStrategy) {
            this.lifecycleEventStrategy = lifecycleEventStrategy;
            return this;
        }

        public DatadogReporter build(String host, String apiKey) {
            return new DatadogReporter(registry, clock, filter, rateUnit, durationUnit, aliasStrategy, tagStrategy,
                    lifecycleEventStrategy, host, apiKey);
        }
    }

    @Override
    public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters,
                       SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters,
                       SortedMap<String, Timer> timers) {
        reportSeries(gauges, counters, histograms, meters, timers);
    }

    @Override
    public void start(long period, TimeUnit unit) {
        try {
            super.start(period, unit);
            reportEvent(lifecycleEventStrategy.event(Lifecycle.START));
            log.info("datadog reporter started");
        } catch (Exception e) {
            log.error("failed to start datadog reporter", e);
        }
    }

    @Override
    public void stop() {
        try {
            super.stop();
            reportEvent(lifecycleEventStrategy.event(Lifecycle.STOP));
            log.info("datadog reporter stopped");
        } catch (Exception e) {
            log.error("failed to stop datadog reporter", e);
        }
    }

    private void reportSeries(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters,
                              SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters,
                              SortedMap<String, Timer> timers) {
        final long timestamp = clock.getTime() / MILLIS_PER_SECOND;

        Writer urlWriter = null;
        try {
            URL seriesUrl = new URL(String.format(SERIES_URL_TEMPLATE, apiKey));
            urlWriter = urlWriterFactory.getWriter(seriesUrl);

            SeriesStream series = new SeriesStream(urlWriter);

            for (Map.Entry<String, Gauge> entry : gauges.entrySet()) {
                reportGauge(series, entry.getKey(), entry.getValue(), timestamp);
            }

            for (Map.Entry<String, Counter> entry : counters.entrySet()) {
                reportCounter(series, entry.getKey(), entry.getValue(), timestamp);
            }

            for (Map.Entry<String, Histogram> entry : histograms.entrySet()) {
                reportHistogram(series, entry.getKey(), entry.getValue(), timestamp);
            }

            for (Map.Entry<String, Meter> entry : meters.entrySet()) {
                reportMetered(series, entry.getKey(), entry.getValue(), timestamp);
            }

            for (Map.Entry<String, Timer> entry : timers.entrySet()) {
                reportTimer(series, entry.getKey(), entry.getValue(), timestamp);
            }

            series.close();
        } catch (HttpException hre) {
            log.error("datadog rejected request with status: {} and response: {}", hre.getStatus(), hre.getResponse());
        } catch (Exception e) {
            log.error("unable to report metrics to datadog", e);
        } finally {
            if (urlWriter != null) try {
                urlWriter.close();
            } catch (IOException e) {
                log.error("unable to close datadog connection", e);
            }
        }
    }

    private void reportEvent(Event event) {
        if (event != null) {
            Writer urlWriter = null;
            try {
                urlWriter = urlWriterFactory.getWriter(new URL(String.format(EVENTS_URL_TEMPLATE, apiKey)));

                urlWriter.write(event.toString());
            } catch (HttpException hre) {
                log.error("datadog rejected request with status: {} and response: {}", hre.getStatus(), hre.getResponse());
            } catch (Exception e) {
                log.error("unable to report event to datadog", e);
            } finally {
                if (urlWriter != null) {
                    try {
                        urlWriter.close();
                    } catch (IOException e) {
                        log.error("unable to close datadog connection", e);
                    }
                }
            }
        }
    }

    private String alias(String name, Metric metric) {
        return aliasStrategy.alias(name, metric);
    }

    private String[] tags(String name, Metric metric) {
        return tagStrategy.tags(name, metric);
    }

    private void reportMetered(SeriesStream series, String originalName, Metered meter,
                               long timestamp) throws IOException {
        String alias = alias(originalName, meter);
        String[] tags = tags(originalName, meter);

        series.writeMetric(name(alias, "count"), timestamp, meter.getCount(), host, tags);
        series.writeMetric(name(alias, "m1_rate"), timestamp, convertRate(meter.getOneMinuteRate()), host, tags);
        series.writeMetric(name(alias, "m5_rate"), timestamp, convertRate(meter.getFiveMinuteRate()), host, tags);
        series.writeMetric(name(alias, "m15_rate"), timestamp, convertRate(meter.getFifteenMinuteRate()), host, tags);
        series.writeMetric(name(alias, "mean_rate"), timestamp, convertRate(meter.getMeanRate()), host, tags);
    }

    private void reportTimer(SeriesStream series, String originalName, Timer timer, long timestamp) throws IOException {
        final Snapshot snapshot = timer.getSnapshot();

        String alias = alias(originalName, timer);
        String[] tags = tags(originalName, timer);

        series.writeMetric(name(alias, "max"), timestamp, convertDuration(snapshot.getMax()), host, tags);
        series.writeMetric(name(alias, "mean"), timestamp, convertDuration(snapshot.getMean()), host, tags);
        series.writeMetric(name(alias, "min"), timestamp, convertDuration(snapshot.getMin()), host, tags);
        series.writeMetric(name(alias, "stddev"), timestamp, convertDuration(snapshot.getStdDev()), host, tags);
        series.writeMetric(name(alias, "p50"), timestamp, convertDuration(snapshot.getMedian()), host, tags);
        series.writeMetric(name(alias, "p75"), timestamp, convertDuration(snapshot.get75thPercentile()), host, tags);
        series.writeMetric(name(alias, "p95"), timestamp, convertDuration(snapshot.get95thPercentile()), host, tags);
        series.writeMetric(name(alias, "p98"), timestamp, convertDuration(snapshot.get98thPercentile()), host, tags);
        series.writeMetric(name(alias, "p99"), timestamp, convertDuration(snapshot.get99thPercentile()), host, tags);
        series.writeMetric(name(alias, "p999"), timestamp, convertDuration(snapshot.get999thPercentile()), host, tags);

        reportMetered(series, originalName, timer, timestamp);
    }

    private void reportHistogram(SeriesStream series, String originalName, Histogram histogram,
                                 long timestamp) throws IOException {
        final Snapshot snapshot = histogram.getSnapshot();

        String alias = alias(originalName, histogram);
        String[] tags = tags(originalName, histogram);

        series.writeMetric(name(alias, "count"), timestamp, histogram.getCount(), host, tags);
        series.writeMetric(name(alias, "max"), timestamp, snapshot.getMax(), host, tags);
        series.writeMetric(name(alias, "mean"), timestamp, snapshot.getMean(), host, tags);
        series.writeMetric(name(alias, "min"), timestamp, snapshot.getMin(), host, tags);
        series.writeMetric(name(alias, "stddev"), timestamp, snapshot.getStdDev(), host, tags);
        series.writeMetric(name(alias, "p50"), timestamp, snapshot.getMedian(), host, tags);
        series.writeMetric(name(alias, "p75"), timestamp, snapshot.get75thPercentile(), host, tags);
        series.writeMetric(name(alias, "p95"), timestamp, snapshot.get95thPercentile(), host, tags);
        series.writeMetric(name(alias, "p98"), timestamp, snapshot.get98thPercentile(), host, tags);
        series.writeMetric(name(alias, "p99"), timestamp, snapshot.get99thPercentile(), host, tags);
        series.writeMetric(name(alias, "p999"), timestamp, snapshot.get999thPercentile(), host, tags);
    }

    private void reportCounter(SeriesStream series, String originalName, Counter counter,
                               long timestamp) throws IOException {
        series.writeMetric(name(alias(originalName, counter), "count"), timestamp, counter.getCount(), host,
                tags(originalName, counter));
    }

    private void reportGauge(SeriesStream series, String originalName, Gauge gauge, long timestamp) throws IOException {
        Number value = (Number) gauge.getValue();
        if (value != null) {
            series.writeMetric(alias(originalName, gauge), timestamp, value, host, tags(originalName, gauge));
        }
    }

}


