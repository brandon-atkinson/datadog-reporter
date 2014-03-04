package com.acknsyn.brandon.datadog.reporter;

/**
 *  A {@link com.acknsyn.brandon.datadog.reporter.LifecycleEventStrategy} which
 *  returns a null {@link com.acknsyn.brandon.datadog.reporter.Event}, indicating
 *  that no event should be published.
 */
public final class NullLifecyleEventStrategy implements LifecycleEventStrategy {
    public Event event(Lifecycle lifecycle) {
        return null;
    }
}
